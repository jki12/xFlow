package com.nhnacademy.util;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Input;
import com.nhnacademy.Output;
import com.nhnacademy.Wire;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.node.ActiveNode;

/**
 * 데이터를 받아서 modbus의 pdu 부분을 만드는 class. 각각의 key값의 데이터 분량은 2byte로 가정하고 제작.
 * <p>
 * 와이어는 전부 JSON만 받을 수 있기에 pdu를 byte[]로 만들고, json으로 변환했음.
 * <p>
 * 형태를 예를 들자면,
 * <p>
 * {transactionId : 1234}
 * <p>
 * {unitID : 4567}
 * <p>
 * {pdu : 1234,12345}
 * 이런 식.
 * <p>
 * pdu의 앞 데이터는 register address, 뒤는 value.
 */
public class ModbusMapperMtoR extends ActiveNode implements Input, Output {

    private final Set<Wire> inputWires = new HashSet<>();
    private final Set<Wire> outputWires = new HashSet<>();

    protected ModbusMapperMtoR(String name) {
        super(name);
    }

    @Override
    public void wireOut(Wire wire) {
        outputWires.add(wire);
    }

    @Override
    public void wireIn(Wire wire) {
        inputWires.add(wire);
    }

    @Override
    public void process() {
        readMessage();
    }

    private void readMessage() {
        for (Wire wire : inputWires) {
            var messageQueue = wire.getMessageQue();
            if (!wire.getMessageQue().isEmpty()) {
                Message message = messageQueue.poll();
                if (message != null) {
                    JSONObject content = ((JsonMessage) message).getContent();
                    if (content != null) {
                        int transactionId = content.getInt("transactionId");
                        int unitId = content.getInt("UnidID");
                        int register = content.getInt("register");
                        int value = content.getInt("value");
                        convertToModbus(transactionId, unitId, register, value);
                    }
                }
            }
        }
    }

    private void convertToModbus(int transactionId, int unitId, int register, int value) {
        byte[] byteTransactionId = convertIntToByte(transactionId);
        byte[] byteUnitId = convertIntToByte(unitId);
        byte[] byteRegister = convertIntToByte(register);
        byte[] byteValue = convertIntToByte(value);

        byte[] headerTransactionId = new byte[2];
        byte[] headerUnitId = new byte[2];
        byte[] pdu = new byte[byteRegister.length + byteValue.length];

        System.arraycopy(byteTransactionId, 0, headerTransactionId, 0, byteTransactionId.length);
        System.arraycopy(byteUnitId, 0, headerUnitId, 0, byteUnitId.length);
        System.arraycopy(byteRegister, 0, pdu, 0, byteRegister.length);
        System.arraycopy(byteValue, 0, pdu, byteRegister.length, byteValue.length);

        JSONObject pduJsonMessage = makeJsonMessage(headerTransactionId, headerUnitId, pdu);
        spreadMessage(pduJsonMessage);
    }

    private byte[] convertIntToByte(int value) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (value >> 8);
        bytes[1] = (byte) value;
        return bytes;
    }

    private JSONObject makeJsonMessage(byte[] headerTransactionId, byte[] headerUnitId, byte[] pduData) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("transactionId", headerTransactionId);
        jsonObject.put("unitId", headerUnitId);
        jsonObject.put("pdu", pduData);

        return jsonObject;
    }

    private void spreadMessage(JSONObject pduJson) {
        Message message = new JsonMessage(pduJson);

        for (Wire wire : outputWires) {
            wire.getMessageQue().add(message);
        }
    }
}