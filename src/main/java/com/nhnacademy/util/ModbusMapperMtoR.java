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

import lombok.Getter;

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
@Getter
public class ModbusMapperMtoR extends ActiveNode implements Input, Output {

    private final Set<Wire> inputWires = new HashSet<>();
    private final Set<Wire> outputWires = new HashSet<>();

    public ModbusMapperMtoR(String name) {
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
                JSONObject content = ((JsonMessage) message).getContent();
                int type = content.getInt("type");
                if ((type == 21) || (type == 22)) { // type 걸러내기.
                    int transactionId = content.getInt("transactionId");
                    int unitId = content.getInt("unitId");
                    int functionCode = content.getInt("functionCode");
                    int register = content.getInt("register");
                    int[] values = (int[]) content.get("value");
                    convertToModbus(transactionId, unitId, functionCode, register, values);
                } else {
                    throw new IllegalArgumentException();
                    // TODO exception 추가 제작 필요. 저건 대충 비슷해 보이는거 넣은거.
                }
            }
        }
    }

    private void convertToModbus(int transactionId, int unitId, int functionCode, int register, int[] values) {
        byte[] byteTransactionId = convertIntToByte(transactionId);
        byte[] byteUnitId = convertIntToByte(unitId);
        byte[] byteFucntionCode = convertIntTo1Byte(functionCode);
        byte[] byteRegister = convertIntToByte(register);

        int headerSize = byteTransactionId.length + byteUnitId.length + byteFucntionCode.length;
        int currentIndex = 0;

        byte[] header = new byte[headerSize];
        byte[] pdu = new byte[byteRegister.length + values.length * 2];

        System.arraycopy(byteTransactionId, 0, header, currentIndex, byteTransactionId.length);
        currentIndex += byteTransactionId.length;

        System.arraycopy(byteUnitId, 0, header, currentIndex, byteUnitId.length);

        System.arraycopy(byteFucntionCode, 0, pdu, 0, byteFucntionCode.length);
        currentIndex += byteFucntionCode.length;

        System.arraycopy(byteRegister, 0, pdu, currentIndex, byteRegister.length);
        currentIndex = byteFucntionCode.length;

        for (int i = 0; i < values.length; i++) {
            byte[] byteValue = convertIntToByte(values[i]);
            System.arraycopy(byteValue, 0, pdu, currentIndex, byteValue.length);
            currentIndex += byteValue.length;
        }

        JSONObject pduJsonMessage = makeJsonMessage(byteTransactionId, byteUnitId, byteFucntionCode, pdu);
        spreadMessage(pduJsonMessage);
    }

    private byte[] convertIntToByte(int value) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (value >> 8);
        bytes[1] = (byte) value;
        return bytes;
    }

    private byte[] convertIntTo1Byte(int value) {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) value;
        return bytes;
    }

    private JSONObject makeJsonMessage(byte[] headerTransactionId, byte[] headerUnitId, byte[] byteFucntionCode,
            byte[] pduData) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("transactionId", headerTransactionId);
        jsonObject.put("unitId", headerUnitId);
        jsonObject.put("functionCode", byteFucntionCode);

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