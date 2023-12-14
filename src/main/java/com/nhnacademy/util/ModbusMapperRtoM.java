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
 * 헤더는 동일. 뒤에 데이터는 다름.
 * json 형태로 변환해야함.
 * <p>
 * header : Transaction Id(2), protocol Id(2), Length(2), unitId(1)
 * <p>
 * PDU : function code(2), Data(n)
 * BigEndIAN은 데이터를 내보낼 때 하면 되겠지. 굳이 지금 할 필요성은 잘 모르겠음.
 */
@Getter
public class ModbusMapperRtoM extends ActiveNode implements Input, Output {

    private final Set<Wire> inputWires = new HashSet<>();
    private final Set<Wire> outputWires = new HashSet<>();

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

                JSONArray headData = content.getJSONArray("header");
                JSONArray pduData = content.getJSONArray("pdu");
                int typeData = content.getInt("type");
                int registerAddress = content.optInt("registerAddress", 0); // 값이 없을 경우 0

                readModbusData(headData, pduData, typeData, registerAddress);
            }
        }
    }

    public void readModbusData(JSONArray headerData, JSONArray pduData, int typeData, int registerAddress) {
        int transactionId = (headerData.getInt(0) << 8) | (headerData.getInt(1) & 0xFF); // 0, 1
        int protocolId = (headerData.getInt(2) << 8) | (headerData.getInt(3) & 0xFF); // 2, 3
        int length = (headerData.getInt(4) << 8) | (headerData.getInt(5) & 0xFF); // 4, 5
        int unitId = headerData.getInt(6); // 6
        int functionCode = pduData.getInt(0); // 7

        int pduDataLength = pduData.length() - 1;

        byte[] pduArrayData = new byte[pduDataLength]; // 7 ~ n

        if ((functionCode == 3) || (functionCode == 4)) {
            // Byte count를 읽고, 그 다음부터 데이터를 읽는 처리 로직 추가
            int byteCount = pduData.getInt(1);
            if (pduData.length() < byteCount + 2) {
                throw new IllegalArgumentException();
            }
            for (int i = 0; i < byteCount; i++) {
                pduArrayData[i] = (byte) pduData.getInt(i + 2);
            }
        } else if ((functionCode == 6) || (functionCode == 16)) {
            // Reference number를 읽고, 그 다음부터 데이터를 읽는 처리 로직 추가
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

        JSONObject convertData = convertToJson(transactionId, protocolId, length, unitId, functionCode, pduArrayData,
                typeData, registerAddress);
        spreadMessage(convertData);
    }

    public JSONObject convertToJson(int transactionId, int protocolId, int length, int unitId, int functionCode,
            byte[] pduArrayData, int typeData, int registerAddress) {
        JSONObject jsonObject = new JSONObject();
        JSONArray pduDataArray = new JSONArray();

        jsonObject.put("transactionId", transactionId);
        jsonObject.put("protocolId", protocolId);
        jsonObject.put("length", length);
        jsonObject.put("unitId", unitId);

        jsonObject.put("functionCode", functionCode);

        for (byte b : pduArrayData) {
            pduDataArray.put(b);
        }
        // Base64 인코딩 가능성 배제
        jsonObject.put("pduData", pduDataArray);

        jsonObject.put("type", typeData);
        jsonObject.put("registerAddress", registerAddress);

        return jsonObject;
    }

    public void spreadMessage(JSONObject convertData) {
        Message message = new JsonMessage(convertData);

        for (Wire wire : outputWires) {
            wire.getMessageQue().add(message);
        }
    }
}