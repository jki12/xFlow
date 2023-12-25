package com.nhnacademy.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Input;
import com.nhnacademy.Wire;
import com.nhnacademy.exception.Exception01;
import com.nhnacademy.exception.Exception02;
import com.nhnacademy.exception.Exception03;
import com.nhnacademy.message.JsonMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusSlave extends ActiveNode implements Input {
    private final Set<Wire> inWires = new HashSet<>();
    // private Map<Integer, String> buffer = new HashMap<>();
    byte[] buffer = new byte[1024];
    private final int port;

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private int requestLength;
    private byte functionCode;
    private byte[] header;
    private byte[] responsePdu;
    private byte[] request;
    private byte[] response;
    private byte[] errorBytes;

    public ModbusSlave() {
        this("modbus slave");
    }

    public ModbusSlave(String name) {
        this(502, name);
    }

    public ModbusSlave(int port, String name) {
        super(name);
        this.port = port;
    }

    @Override
    public void wireIn(Wire wire) {
        inWires.add(wire);
    }

    
    public byte[] makeResponseHeader(byte[] request) {
        header = new byte[7];
        // trasactionId - 2/2/2/1/1/
        header[0] = request[0];
        header[1] = request[1];
        header[2] = 0;
        header[3] = 0;
        
        // quantity * 2 
        int quantity = ((request[5] & 0xFF) << 8) | (request[6] & 0xFF) + 2;
        byte[] quantities = {(byte)(quantity >> 8), (byte)(quantity)};

        header[4] = quantities[0];
        header[5] = quantities[1];
        header[6] = request[6];

        return header;
    }

    /**
     * 연결된 wire에서 message가 들어오면 꺼내서 저장
     */
    public void saveBuffer() {
        for (Wire wire : inWires) {
            if(!wire.getMessageQue().isEmpty()) {
                JSONObject msg = ((JsonMessage) wire.getMessageQue().poll()).getContent();
                int address = Integer.parseInt((String)msg.get("register address"));
                String value = (String)msg.get("value");
                setData(address, value);
            }
        }
    }

    /**
     * buffer 속에 저장된 data 찾아오기(encoding한 상태이므로 decoding하여 돌려준다)
     * @param address
     * @return
     */
    public byte[] getData(int address) {
        byte[] datas = {buffer[address], buffer[address + 1]}; 
        return datas;
    }
    
    /**
     * database에 value 값 넣기
     * @param address
     * @param value
     */
    public void setData(int address, String value) {
        buffer[address] = value.getBytes()[0];
        buffer[address + 1] = value.getBytes()[1];
    }

    /**
     * functionCode 3, 4일 경우 response의 pdu 만들기
     * @param pdu request의 pdu
     */
    public byte[] makeReadRegistersPdu(byte[] pdu){ 
        responsePdu = new byte[1024];
        int address = ((pdu[1] & 0xFF) << 8) | (pdu[2] & 0xFF);
        int quantity = pdu[4];
        
        byte[] data = new byte[256];    // pdu의 최대 크기를 계산해보니 256이면 충분함
        byte[] values;
        for (int i = 0; i < quantity*2; i++) {  //address - 101, quantity-4 -> 101+i(=0) = 101 / 101+i(=1) = 102 / 101+2 = 103 / 101 + 3 = 104
            if ((values = getData(address)) == null) {
                errorBytes = Exception02.Exception02Error(functionCode);
                data[i*2] = 0;
                data[i*2+1] = 0;
            }else {
                if (getData(address+i).length < 2){
                    data[i*2] = 0;                // response[9+i*2] = 0;
                    data[i*2+1] = values[0];      // response[9+i*2+1] = values[0];
                }
                else {
                    data[i*2] = values[0];
                    data[i*2+1] = values[1];
                }
            }
            
        }

        responsePdu[0] = functionCode;
        responsePdu[1] = (byte)(2 * quantity);    // Byte Count
        System.arraycopy(data, 0, responsePdu, 2, data.length);    // value
        return responsePdu;
    }

    
    /**
     * functionCode가 6일때의 response 만들기
     * @param requestPdu
     */
    public byte[] makeWriteSingleRegister(byte[] pdu) {
        responsePdu = new byte[1024];

        int address = ((pdu[1] & 0xFF) << 8) | (pdu[2] & 0xFF);
        byte [] valuesByte = {pdu[3], pdu[4]};
        String value = Base64.getEncoder().encodeToString(valuesByte);
        setData(address, value);

        for (int i = 0; i < pdu.length; i++) {
            responsePdu[i] = pdu[i];
        }

        return responsePdu;
    }
    
    /**
     * functionCode가 16일때의 response 만들기
     * @param pdu
     */
    public byte[] makeWriteMultipleRegisters(byte[] pdu) {
        // functionCode - 1, address - 2, quantity - 2, byteCount - 1, value - 2* N
        responsePdu = new byte[1024];

        int address = ((pdu[1] & 0xFF) << 8) | (pdu[2] & 0xFF);
        int quantity = ((pdu[3] & 0xFF) << 8) | (pdu[4] & 0xFF);
        for (int i = 0; i < quantity; i++) {
            byte[] valuesByte = {pdu[i+6], pdu[i+7]};
            String value = Base64.getEncoder().encodeToString(valuesByte);
            setData(address+i*2, value); // 010006130005
        }
        // Arrays.copyOf, Arrays.asList, Collections.addAll or System.arraycopy
        for (int i = 0; i < 5; i++) {
            responsePdu[i] = pdu[i];
        }
        return responsePdu;
    }
    
    @Override
    public void preprocess() {
        // TODO server니까 thread로 돌려주세요.
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            new Thread(()-> {
                try {
                    socket = serverSocket.accept();
                    outputStream = new BufferedOutputStream(socket.getOutputStream());
                    inputStream = new BufferedInputStream(socket.getInputStream());
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            });
            
            // byte[] request = {0,1,0,0,0,6,1,3,0,0,0,5}; // fuction , address, counter

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void process() {
        request = new byte[1024];
        try {
            while(socket.isConnected()) {
                // - - - - - - - - - - - - - - - 여기부터 - - - - - - - - - - - -
                if ((requestLength = inputStream.read(request)) != -1) {   // inputStream에 들어온게 request에 들어간다
                    for (int i = 0; i < 11; ++i) {
                        if (request[i] > 0) request[i] -= '0';
                    }

                    request[11] = 0;

                    log.debug(Arrays.toString(request));
                    // if((request.length > 7)&&(6 + request[5]) == requestLength) {
                    // }
                    
                    // pdu 따로 뺌
                    byte[] pdu = new byte[1024];
                    System.arraycopy(request, 7, pdu, 0,  request.length-7);
                    functionCode = pdu[0];
                    ByteBuffer byteBuffer;
                    switch (functionCode) {
                        case 3:
                        case 4:
                            // quantity 범위 벗어났을 경우
                            if(!(0 <= request[11] && request[11] <= 0x7D)) {
                                errorBytes = Exception03.exception03Error(functionCode);
                            }
                            byte[] requestHeader = makeResponseHeader(request);
                            byte[] requestPdu = makeReadRegistersPdu(pdu);
                            
                            byteBuffer = ByteBuffer.allocate(requestHeader.length+requestPdu.length);
                            byteBuffer.put(requestHeader);
                            byteBuffer.put(requestPdu);

                            response = byteBuffer.array();
                            if(errorBytes != null) {
                                byteBuffer = ByteBuffer.allocate(requestHeader.length+errorBytes.length+requestPdu.length);
                                byteBuffer.put(requestHeader);
                                byteBuffer.put(errorBytes);
                                byteBuffer.put(requestPdu);

                                response = byteBuffer.array();
                            }
                        
                        break;
                        
                        case 6:
                        // 비트 연산자(&) 우선 순위 : 8위, 비트 시프트 연산자(<<) :  5위, 비트 OR 연산자(|) : 10위,
                        // 관계 연산자(<=, >=) 우선 순위 : 6위, 논리 AND 연산자(&&) 우선 순위 11위
                            // 0x0000 <= Register Value <= 0xFFFF 아니면 exception 3
                            int registerValue = ((pdu[4] & 0xFF) << 8) | (pdu[5] & 0xFF);
                            if(!(0 <= registerValue && registerValue <= 0xFFFF)){
                                errorBytes = Exception03.exception03Error(functionCode);
                            }

                            requestHeader = makeResponseHeader(request);
                            requestPdu = makeWriteSingleRegister(pdu);
                            
                            byteBuffer = ByteBuffer.allocate(requestHeader.length+requestPdu.length);
                            byteBuffer.put(requestHeader);
                            byteBuffer.put(requestPdu);

                            response = byteBuffer.array();
                            if(errorBytes != null) {
                                byteBuffer = ByteBuffer.allocate(requestHeader.length+errorBytes.length+requestPdu.length);
                                byteBuffer.put(requestHeader);
                                byteBuffer.put(errorBytes);
                                byteBuffer.put(requestPdu);

                                response = byteBuffer.array();
                            }
                        
                        break;

                        case 16:
                        int quantityValue = ((pdu[4] & 0xFF) << 8) | (pdu[5] & 0xFF);
                        // 0x0001 <= Quantity of Registers <= 0x007B AND Byte Count == Quantity of Registers x 2 아니면 exception 3
                            if(!((0 <= pdu[5] && pdu[5] <= 0xFFFF) && (pdu[6] == quantityValue*2))){
                                errorBytes = Exception03.exception03Error(functionCode);
                            }

                            requestHeader = makeResponseHeader(request);
                            requestPdu = makeWriteMultipleRegisters(pdu);

                            byteBuffer = ByteBuffer.allocate(requestHeader.length+requestPdu.length);
                            byteBuffer.put(requestHeader);
                            byteBuffer.put(requestPdu);

                            response = byteBuffer.array();
                            if(errorBytes != null) {
                                byteBuffer = ByteBuffer.allocate(requestHeader.length+errorBytes.length+requestPdu.length);
                                byteBuffer.put(requestHeader);
                                byteBuffer.put(errorBytes);
                                byteBuffer.put(requestPdu);

                                response = byteBuffer.array();
                            }
                        break;

                        default:
                            errorBytes = Exception01.exception01Error(functionCode);
                            // >> ? 이건 rsp 어케 써야하는거야?? => 걍 request에다가 error 코드만 더함
                            // log.info("Unsupported function code : " + functionCode);
                            System.arraycopy(request, 0, response, 0, 7);
                            System.arraycopy(errorBytes, 0, response, 7, errorBytes.length);
                            System.arraycopy(pdu, 0, response, 7+ errorBytes.length, pdu.length);
                            break;
                    }
                    outputStream.write(response);
                    outputStream.flush();
                }
            }
        } catch (IOException e) {
        log.error(e.getMessage());
        }
    
    }
    
}
