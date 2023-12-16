package com.nhnacademy.util;

import java.util.Base64;
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
 * rule engine에서 데이터를 받고, 그걸 재가공. 값을 modbus protocol에 맞춰 byte로 변환.
 * <p>
 * db의 register address 1개에는 값이 1개만. 즉 array를 사용할 필요성이 정당하지 않음.
 * <p>
 * 고로 폐기.
 * <p>
 * register address, value만 받고, 이걸 byte로 변환한 후, 다음 wire에 담기 위해 jsonObject로 만든 후
 * 보냄.
 * 어느 정도의 값이 들어올지 모름. 그래서 2byte로 만듦.
 * 
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

    public void readMessage() {
        for (Wire wire : inputWires) {
            var messageQueue = wire.getMessageQue();
            if (!messageQueue.isEmpty()) {
                Message message = messageQueue.poll();
                JSONObject content = ((JsonMessage) message).getContent();
                int register = content.getInt("register");
                int values = (int) content.getDouble("value");
                convertToModbus(register, values);
            }
        }
    }

    public void convertToModbus(int register, int values) {
        byte[] byteRegister = convertIntToByte(register);
        byte[] byteValues = convertIntToByte(values);

        JSONObject pduJsonMessage = makeJsonMessage(byteRegister, byteValues);
        spreadMessage(pduJsonMessage);
    }

    public byte[] convertIntToByte(int value) {
        byte[] bytes = new byte[2];

        // 상위 바이트(8 bit)는 정수 값을 8비트 오른쪽으로 쉬프트하여 얻고,
        bytes[0] = (byte) (value >> 8);

        // 하위 바이트는 정수를 바이트로 타입 캐스팅하여, 자동으로 상위 24bit를 버리고 얻음.
        bytes[1] = (byte) value;
        return bytes;
    }

    /**
     * byte[]를 그대로 jsonObject에 넣으면 toString을 통해서 바이트 배열을 문자열로 만들려고 할텐데,
     * byte[].toString은 우리가 원하는 문자열이 아닌, [a@hashCode의 형태로 나타남.
     * <p>
     * byte[]를 jsonObject에 원하는 형태로 넣기 위해서는 바이트 배열을 문자열로 인코딩해야함.
     * <p>
     * => Base64를 이용해서 인코딩한 이유.
     * 
     * @param byteRegister
     * @param byteValue
     * @return
     */
    public JSONObject makeJsonMessage(byte[] byteRegister, byte[] byteValue) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("registerAddress", Base64.getEncoder().encodeToString(byteRegister));
        jsonObject.put("value", Base64.getEncoder().encodeToString(byteValue));

        return jsonObject;
    }

    public void spreadMessage(JSONObject pduJson) {
        Message message = new JsonMessage(pduJson);

        for (Wire wire : outputWires) {
            wire.getMessageQue().add(message);
        }
    }
}