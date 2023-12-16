package com.nhnacademy.util;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nhnacademy.Input;
import com.nhnacademy.Output;
import com.nhnacademy.Wire;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.node.ActiveNode;

import lombok.Getter;

/**
 * 클래스 이름에 괄호가 안들어가져서 이렇게 이름 지음.
 * <p>
 * Modbus mapper (Register to measurement)
 * <p>
 * Modbus data를 json으로 컨버터하는 class.
 * json 형태로 변환해야함.
 * <p>
 * 헤더의 데이터를 분리할 필요 없음.
 */
@Getter
public class ModbusMapperRtoM extends ActiveNode implements Input, Output {

    public final Set<Wire> inputWires = new HashSet<>();
    public final Set<Wire> outputWires = new HashSet<>();

    public ModbusMapperRtoM(String name) {
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

    /**
     * inputWire가 여러개라고 가정하고 작업.
     * <p>
     * 메제시를 받으면 JSONArray로 변환.
     * <p>
     * header, pdu 2개의 jsonarray를 만듦.
     */
    public void readMessage() {
        for (Wire wire : inputWires) {
            if (!wire.getMessageQue().isEmpty()) {
                Message msg = wire.getMessageQue().poll();
                JSONObject content = ((JsonMessage) msg).getContent();
                JSONArray pduData = content.getJSONArray("pdu");
                int registerAddress = content.optInt("registerAddress", 0); // 값이 없을 경우 0

                readModbusData(pduData, registerAddress);
            }
        }
    }

    public void readModbusData(JSONArray pduData, int registerAddress) {
        int functionCode = pduData.getInt(0); // 7
        int quantity = 0;

        int pduDataLength = pduData.length() - 1;

        byte[] pduArrayData = new byte[pduDataLength]; // 7 ~ n

        if ((functionCode == 3) || (functionCode == 4)) {
            quantity = ((pduData.getInt(2) << 8) | (pduData.getInt(3) & 0xFF));
            int byteCount = pduData.getInt(1);

            if (pduData.length() < byteCount + 2) {
                throw new IllegalArgumentException();
            }

            for (int i = 0; i < byteCount; i++) {
                pduArrayData[i] = (byte) pduData.getInt(i + 2);
            }
        } else if ((functionCode == 6) || (functionCode == 16)) {
            quantity = ((pduData.getInt(4) << 8) | (pduData.getInt(5) & 0xFF));
            if (pduData.length() < pduDataLength + 1) {
                throw new IllegalArgumentException();
            }
            for (int i = 0; i < pduDataLength - 1; i++) {
                pduArrayData[i] = (byte) (pduData.getFloat(i + 2) * 10);
            }
        } else {
            for (int i = 0; i < pduDataLength; i++) {
                pduArrayData[i] = (byte) (pduData.getFloat(i + 1) * 10);
            }
        }

        JSONObject convertData = convertToJson(pduArrayData,
                registerAddress, quantity);
        spreadMessage(convertData);
    }

    public JSONObject convertToJson(
            byte[] pduArrayData, int registerAddress, int quantity) {
        JSONObject jsonObject = new JSONObject();
        JSONArray pduDataArray = new JSONArray();
        JSONArray value = new JSONArray();

        jsonObject.put("quantity", quantity);

        for (byte b : pduArrayData) {
            pduDataArray.put(b);
        }
        // Base64 인코딩 가능성 배제
        jsonObject.put("pduData", pduDataArray);

        jsonObject.put("registerAddress", registerAddress);
        jsonObject.put("value", value);

        return jsonObject;
    }

    public void spreadMessage(JSONObject convertData) {
        Message message = new JsonMessage(convertData);

        for (Wire wire : outputWires) {
            wire.getMessageQue().add(message);
        }
    }
}