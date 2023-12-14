package com.nhnacademy.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Input;
import com.nhnacademy.Wire;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.util.ModbusClientManager;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.UnknownHostException;

@Slf4j
public class ModbusSlave extends ActiveNode implements Input{
    private final Set<Wire> inWires = new HashSet<>();
    @Setter
    private String host = "172.19.0.8"; // 내 포트 번호

    private BufferedOutputStream outputStream;
    private byte[] response;

    public ModbusSlave() {
        this("modbus slave");            
    }

    public ModbusSlave(String name) {
        super(name);
    }

    public ModbusSlave(String host, String name) {
        super(name);
        this.host = host;
    }

    @Override
    public void wireIn(Wire wire) {
        inWires.add(wire);
    }
    

    /**
     * header 만들기
     * @param msg 필요한 데이터 내용들
     * @return
     */
    public byte[] makeHeader(JSONObject msg){
        byte[] trasactionId = (byte[]) msg.get("transactionId");
        byte unidID = (byte) msg.get("unitId");
        byte[] pdu = (byte[]) msg.get("pdu");

        byte[] header = new byte[7];
        response  = new byte[1024];

        header[0] = (byte) ((trasactionId[0] << 8) | 0xFF);
        header[1] = (byte) (trasactionId[1] | 0xFF);
        header[2] = 0;
        header[3] = 0;
        header[4] = (byte)((pdu.length/2+1)>>8 | 0xFF);
        header[5] = (byte)((pdu.length/2+1) | 0xFF);
        header[6] = unidID;

        System.arraycopy(header, 0, response, 0, header.length);
        return response;
    }

    // preprocess를 process로 나누기, request가 들어오면 어케 처리할 것인가?, exception 처리
    @Override
    public void preprocess() {
        try (Socket socket = ModbusClientManager.getSocket(host)){
            outputStream = new BufferedOutputStream(socket.getOutputStream());
            for (Wire wire : inWires) {
                if(wire.getMessageQue().poll()!= null) {
                    JSONObject msg = ((JsonMessage)wire.getMessageQue().poll()).getContent();
                    byte[] pdu = (byte[]) msg.get("pdu");
                    byte functionCode = (byte) msg.get("functionCode");
                    
                    response = new byte[1024];
                    
                    switch (functionCode) {
                        case 0x03:
                        response = makeHeader(msg);
                        response[7] = functionCode;
                        response[8] = (byte) (pdu.length/2);    // byteCount
                        
                        // value
                        for(int i = 0; i < pdu.length/2; i++) {
                            response[9+i*2] = (byte)(pdu[1+i*2] >> 8 | 0xFF);
                            response[9+i*2+1] = (byte)(pdu[1+i*2] | 0xFF);
                        }
                        break;
                        
                        
                        case 0x04:
                        response = makeHeader(msg);
                        response[7] = functionCode;
                        response[8] = (byte) (pdu.length/2);    // byteCount
                        
                        // input register
                        for(int i = 0; i < pdu.length/2; i++) {
                            response[9+i*2] = (byte)(pdu[i*2] >> 8 | 0xFF);
                            response[9+i*2+1] = (byte)(pdu[i*2] | 0xFF);
                        }
                        break;
                        
                        //resp과 req의 header 동일
                        case 0x06:
                        response = makeHeader(msg);
                        response[7] = functionCode;
                        
                        response[8] = (byte)(pdu[0] >> 8 | 0xFF);
                        response[9] = (byte) (pdu[0] | 0xFF);
                        
                        response[10] = (byte)((pdu[1] >> 8) | 0xFF);
                        response[11] = (byte)(pdu[1] | 0xFF);
                        
                        case 0x10:
                        response = makeHeader(msg);
                        response[7] = functionCode;
                        
                        // starting address
                        response[8] = (byte)(pdu[0] >> 8 | 0xFF);
                        response[9] = (byte) (pdu[0] | 0xFF);
                        
                        // Quantity
                        response[10] = (byte)((pdu.length/2 >> 8) | 0xFF);
                        response[11] = (byte)(pdu.length/2 | 0xFF);
                        break;
                        default:
                        
                        break;
                    }
                }
                outputStream.write(response);
                outputStream.flush();

                // transactionId/ protocol(00)/ length(pdu.length + 1(=unitId))/unitId/ functionCode/ byteCount/내용물
				// 				              header						          /				pdu

            }
            // byte[] request = {0,1,0,0,0,6,1,3,0,0,0,5}; // fuction , address, counter
            

        } catch (UnknownHostException e) {
            System.err.println("Unknown host!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process() {
        
    }

    @Override
    public void run() {
        
    }
    
}
