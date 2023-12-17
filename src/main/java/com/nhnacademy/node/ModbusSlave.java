package com.nhnacademy.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Input;
import com.nhnacademy.Wire;
import com.nhnacademy.exception.Exception03;
import com.nhnacademy.exception.Exception1;
import com.nhnacademy.exception.Exception4;
import com.nhnacademy.message.JsonMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusSlave extends ActiveNode implements Input {
    private final Set<Wire> inWires = new HashSet<>();
    private Map<Integer, String> database = new HashMap<>();
    private final int port;

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private int requestLength;
    private byte functionCode;
    private byte[] request;
    private byte[] response;

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

    
    public void makeResponseHeader(byte[] request) {
        byte[] header = new byte[7];
        response = new byte[1024];
        // trasactionId - 2/2/2/1/1/
        header[0] = request[0];
        header[1] = request[1];
        header[2] = 0;
        header[3] = 0;
        

        // 내가 계산하는게 나을까 아님 pdu 길이를 가져오는게 나을까?
        // quantity * 2 
        int quantity = ((request[5] & 0xFF) << 8) | (request[6] & 0xFF) + 2;
        byte[] quantities = {(byte)(quantity >> 8), (byte)(quantity)};

        header[4] = quantities[0];
        header[5] = quantities[1];
        header[6] = request[6];

        System.arraycopy(header, 0, response, 0, header.length);
    }

    /**
     * 연결된 wire에서 message가 들어오면 꺼내서 map에 저장한다
     */
    public void saveBuffer() {
        for (Wire wire : inWires) {
            if(!wire.getMessageQue().isEmpty()) {
                JSONObject msg = ((JsonMessage) wire.getMessageQue().poll()).getContent();
                int address = Integer.parseInt((String)msg.get("register address"));
                String value = (String)msg.get("value");
                database.put(address, value);
            }
        }
    }


    /**
     * Map에서 key를 통해 저장된 value를 찾고자 한다
     * @param key value를 찾기 위한 key
     * @return 찾고자하는 value
     * @throws NullPointerException map에 key가 없을 경우
     * @hidden
     */
    public String getMapValues(Integer key) throws NullPointerException {
        return database.get(key);
    }


    
    // -----------------------------------------------------------------------------------------------

    
    
    // public static void main(String[] args) {
        //     /* byte a = 7;
    //     System.out.println(a);
    
    //     byte b= 1;
    
    //     int c = (((b & 0xFF) << 8) | (a & 0xFF));
    //     System.out.println((b & 0xFF) << 8);
    //     System.out.println(c); */
    
    //     String a = "34";
    //     byte [] d = Base64.getDecoder().decode(a);
    //     for (byte b : d) {
        //         System.out.println(b);
        
    //     }
    // }
    
    // endcoding decoding 알아보려고 한 거
    public static void main3(String[] args) {
        String df = "25";
        JSONObject dfkj = new JSONObject(23);
        byte[] str = {(byte)25};
        String encodedStr = Base64.getEncoder().encodeToString(df.getBytes());
        String encodedStr2 = Base64.getEncoder().encodeToString(str);
        // String encodedJSONOBject = Base64.getEncoder().encodeToString(dfkj.get(23))
        
        
        System.out.println("encoded string: " + encodedStr2);
        
        // String encodedStr = "dGVzdA==";
        byte[] decodedBytes = Base64.getDecoder().decode(encodedStr);
        byte[] decodedBytes2 = Base64.getDecoder().decode(encodedStr2);
        
        System.out.println("decodedBytes" + decodedBytes.length);
        for (byte b : decodedBytes) {
            System.out.print(b +"/");
        }
        System.out.println("\n");
        
        System.out.println("decodedBytes2" + decodedBytes2.length);
        for (byte b : decodedBytes2) {
            System.out.print(b +"/");
        }
        
        System.out.println("\n");
        String decodedStr = new String(decodedBytes);
        String decodedStr2 = new String(decodedBytes2, StandardCharsets.UTF_8);
        
        System.out.println("decoded string: " + decodedStr);
        System.out.println("decoded string version byte[] : " + decodedStr2);
        
        // byte a = 3;
        // int b = a;
        
        // System.out.println((byte)(b*2));
    }
    
    /**
     * database 속에 저장된 data 찾아오기(encoding한 상태이므로 decoding하여 돌려준다)
     * @param address
     * @return
     */
    public byte[] getData(int address) {
        byte[] decodedValue= Base64.getDecoder().decode(getMapValues(address));     
        return decodedValue;
    }
    
    /* 1. encoding 상태로 저장하기  -> 꺼낼 때 decoding하고 new String하고 다시 byte[]로 
    2. decoding한 상태로 byte[]로 저장하기 -> 
    */
    /**
     * functionCode 3, 4일 경우 response의 pdu 만들기
     * @param pdu request의 pdu
     */
    public void makeReadRegistersPdu(byte[] pdu){ 
        int address = ((pdu[1] & 0xFF) << 8) | (pdu[2] & 0xFF);
        int quantity = pdu[4];
        
        byte[] data = new byte[256];    // pdu의 최대 크기를 계산해보니 256이면 충분함
        byte[] values;
        for (int i = 0; i < quantity*2; i++) {  //address - 101, quantity-4 -> 101+i(=0) = 101 / 101+i(=1) = 102 / 101+2 = 103 / 101 + 3 = 104
            if ((values = getData(address+i)).length < 2){      // Data가 byte일 경우
                data[i*2] = 0;                // response[9+i*2] = 0;
                data[i*2+1] = values[0];      // response[9+i*2+1] = values[0];
            }
            else {                                              // Data가 byte[]일 경우
                data[i*2] = values[0];
                data[i*2+1] = values[1];
            }
        }
        
        response[7] = functionCode;
        response[8] = (byte)(2 * quantity);    // Byte Count
        System.arraycopy(data, 0, response, 9, data.length);    // value
    }

    /**
     * database에 value 값 넣기
     * @param address
     * @param value
     */
    public void setData(int address, String value) {
        database.put(address, value);
    }
    
    /**
     * functionCode가 6일때의 response 만들기
     * @param requestPdu
     */
    public void makeWriteSingleRegister(byte[] requestPdu) {
        response[7] = functionCode;
        int address = ((requestPdu[1] & 0xFF) << 8) | (requestPdu[2] & 0xFF);
        String value = String.valueOf(((requestPdu[3] & 0xFF) << 8) | (requestPdu[4] & 0xFF));
        setData(address, value);
        for (int i = 1; i < requestPdu.length; i++) {
            response[i+7] = requestPdu[i];
        }
    }
    
    /**
     * functionCode가 16일때의 response 만들기
     * @param pdu
     */
    public void makeWriteMultipleRegisters(byte[] pdu) {
        // functionCode - 1, address - 2, quantity - 2, byteCount - 1, value - 2* N
        response[7] = functionCode;
        int address = ((pdu[1] & 0xFF) << 8) | (pdu[2] & 0xFF);
        int quantity = ((pdu[3] & 0xFF) << 8) | (pdu[4] & 0xFF);
        for (int i = 0; i < quantity; i++) {
            String value = String.valueOf(((pdu[i+6] & 0xFF) << 8) | (pdu[i+7] & 0xFF));
            setData(address+i, value);
        }
        
        for (int i = 1; i <= 4; i++) {
            response[7 + i] = pdu[i];
        }
        
    }
    
    @Override
    public void preprocess() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            outputStream = new BufferedOutputStream(socket.getOutputStream());
            inputStream = new BufferedInputStream(socket.getInputStream());
            
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
                if ((requestLength = inputStream.read(request)) != -1){   // inputStream에 들어온게 request에 들어간다
                    // if((request.length > 7)&&(6 + request[5]) == requestLength) {
                    // }
                    // pdu 빠로 뺌
                    byte[] pdu = new byte[1024];
                    System.arraycopy(request, 7, pdu, 0,  request.length-7);
                    functionCode = pdu[0];
                    
                    switch (functionCode) {
                        case 3:
                        case 4:
                        if(0 <= request[11] && request[11] <= 0x7D) {
                            makeResponseHeader(request);
                            makeReadRegistersPdu(pdu);
                        }
                        else {
                            Exception03.exception03Error(functionCode);
                        }
                        
                        break;
                        
                        case 6:
                        makeResponseHeader(request);
                        makeWriteSingleRegister(pdu);
                        break;

                        case 16:
                        makeResponseHeader(request);
                        makeWriteMultipleRegisters(pdu);
                        break;

                        default:
                        Exception1.exception01Error(functionCode);
                        // log.info("Unsupported function code : " + functionCode);
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
