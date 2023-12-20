package com.nhnacademy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.util.ModbusMapperRtoM;

class modbusToJsonTest {

    ModbusMapperRtoM modbusMapperRtoM = new ModbusMapperRtoM("ModbusToJson");
    private final int registerAddress = 103;
    private final int quantity = 3;

    @Test
    void init() {
        assertEquals("ModbusToJson", modbusMapperRtoM.getName());
    }

    @Test
    void readMessageTest() {
        Wire inWire = new Wire();
        JSONObject dummy = new JSONObject();
        dummy.put("header", 010006110005);
        Message message = new JsonMessage(dummy);
        inWire.getMessageQue().add(message);

        modbusMapperRtoM.wireIn(inWire);
        assertEquals(1, modbusMapperRtoM.getInputWires().size());

        Message outputMessage = inWire.getMessageQue().poll();
        assertTrue(outputMessage instanceof JsonMessage);

        modbusMapperRtoM.readMessage();

        JsonMessage jsonOutputMessage = (JsonMessage) outputMessage;

        assertEquals(dummy.toString(), jsonOutputMessage.getContent().toString());
    }

    @Test
    void readModbusDataTest() {
        JSONArray headerData = new JSONArray();
        Wire outWire = new Wire();

        headerData.put((byte) 0x01);
        headerData.put((byte) 0x02);
        headerData.put((byte) 0x03);
        headerData.put((byte) 0x04);
        headerData.put((byte) 0x05);
        headerData.put((byte) 0x06);
        headerData.put((byte) 0x07);

        JSONArray pduData = new JSONArray();
        pduData.put((byte) 0x08);
        pduData.put((byte) 0x09);
        pduData.put((byte) 0x0A);

        int registerAddress = 103;

        modbusMapperRtoM.wireOut(outWire);

        assertEquals(1, modbusMapperRtoM.getOutputWires().size());

        modbusMapperRtoM.readModbusData(pduData, registerAddress);
        modbusMapperRtoM.readModbusData(pduData, registerAddress);
        modbusMapperRtoM.readModbusData(pduData, registerAddress);

        modbusMapperRtoM.getOutputWires().size();

        assertEquals(1, modbusMapperRtoM.getOutputWires().size());

        Wire outputWire = modbusMapperRtoM.getOutputWires().iterator().next();
        assertEquals(3, outputWire.getMessageQue().size());

    }

    @Test
    void convertToJsonTest() {

        int registerAddress = 101;
        JSONArray value = new JSONArray();
        int quantity = 1;
        value.put(1);

        JSONObject result = modbusMapperRtoM.convertToJson(registerAddress, quantity, value);

        assertEquals(registerAddress, result.getInt("registerAddress"));
        assertEquals(quantity, result.getInt("quantity"));
        assertEquals(value, result.getJSONArray("value"));
    }

    @Test
    void spreadMessageTest() {

        Wire outWire = new Wire();

        JSONArray pduData = new JSONArray();

        pduData.put((byte) 0x08);
        pduData.put((byte) 0x09);
        pduData.put((byte) 0x0A);

        modbusMapperRtoM.wireOut(outWire);

        modbusMapperRtoM.readModbusData(pduData, registerAddress);

        Wire outputWire = modbusMapperRtoM.getOutputWires().iterator().next();
        assertEquals(1, outputWire.getMessageQue().size());
    }

}
