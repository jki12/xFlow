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

    private static final String REGISTER_ADDRESS = "registerAddress";
    private static final String PDU = "pdu";
    private static final String VALUE = "value";
    private static final String QUANTITY = "quantity";

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
     * pdu의 데이터만 가져와서 register address, value , quantity의 값만 가져오고 나머지는 처리하지 않음.
     */
    public void readMessage() {
        for (Wire wire : inputWires) {
            if (!wire.getMessageQue().isEmpty()) {
                Message msg = wire.getMessageQue().poll();
                JSONObject content = ((JsonMessage) msg).getContent();
                if (!content.has(REGISTER_ADDRESS)) {
                    throw new IllegalArgumentException();
                }

                JSONArray pduData = content.getJSONArray(PDU);
                int registerAddress = content.getInt(REGISTER_ADDRESS);

                readModbusData(pduData, registerAddress);
            }
        }
    }

    /**
     * 
     * @param pduData
     * @param registerAddress
     */
    public void readModbusData(JSONArray pduData, int registerAddress) {
        int functionCode = pduData.getInt(0); // 7
        int quantity = 0;
        JSONArray value = new JSONArray();

        if ((functionCode == 3) || (functionCode == 4)) {
            quantity = ((pduData.getInt(2) << 8) | (pduData.getInt(3) & 0xFF));
            int byteCount = pduData.getInt(1);

            if (pduData.length() < byteCount + 2) {
                throw new IllegalArgumentException();
            } else {
                for (int i = 0; i < byteCount; i++) {
                    value.put(pduData.getInt(i + 2));
                }
            }

        } else if ((functionCode == 6) || (functionCode == 16)) {
            quantity = ((pduData.getInt(4) << 8) | (pduData.getInt(5) & 0xFF));

            for (int i = 0; i < quantity; i++) {
                value.put(pduData.getInt(i + 2));
            }
        } else {
            // functionCode가 3,4 6,16이 아닐 경우에는 처리하지 않음.
        }
        JSONObject convertData = convertToJson(registerAddress, quantity, value);

        spreadMessage(convertData);
    }

    /**
     * 
     * @param registerAddress db address
     * @param quantity        count
     * @param value           sensor value
     * @return
     */
    public JSONObject convertToJson(int registerAddress, int quantity, JSONArray value) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(REGISTER_ADDRESS, registerAddress);
        jsonObject.put(QUANTITY, quantity);
        jsonObject.put(VALUE, value);

        return jsonObject;
    }

    public void spreadMessage(JSONObject convertData) {
        Message message = new JsonMessage(convertData);

        for (Wire wire : outputWires) {
            wire.getMessageQue().add(message);
        }
    }
}