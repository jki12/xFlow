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

        modbusMapperRtoM.wireOut(outWire);

        assertEquals(1, modbusMapperRtoM.getOutputWires().size());

        modbusMapperRtoM.readModbusData(headerData, pduData);
        modbusMapperRtoM.readModbusData(headerData, pduData);
        modbusMapperRtoM.readModbusData(headerData, pduData);

        modbusMapperRtoM.getOutputWires().size();

        assertEquals(1, modbusMapperRtoM.getOutputWires().size());

        Wire outputWire = modbusMapperRtoM.getOutputWires().iterator().next();
        assertEquals(3, outputWire.getMessageQue().size());

    }

    @Test
    void convertToJsonTest() {
        int transactionId = 123;
        int protocolId = 345;
        int length = 567;
        int unitId = 8;

        int functionCode = 0x03;
        byte[] pduData = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10 };

        JSONObject result = modbusMapperRtoM.convertToJson(transactionId, protocolId, length, unitId, functionCode,
                pduData);

        assertTrue(result.has("transactionId"));
        assertEquals(transactionId, result.getInt("transactionId"));

        assertTrue(result.has("protocolId"));
        assertEquals(protocolId, result.getInt("protocolId"));

        assertTrue(result.has("length"));
        assertEquals(length, result.getInt("length"));

        assertTrue(result.has("unitId"));
        assertEquals(unitId, result.getInt("unitId"));

        assertTrue(result.has("functionCode"));
        assertEquals(functionCode, result.getInt("functionCode"));

        assertTrue(result.has("pduData"));
        assertEquals(pduData, result.get("pduData"));

    }

    @Test
    void spreadMessageTest() {
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

        modbusMapperRtoM.wireOut(outWire);

        modbusMapperRtoM.readModbusData(headerData, pduData);

        Wire outputWire = modbusMapperRtoM.getOutputWires().iterator().next();
        assertEquals(1, outputWire.getMessageQue().size());
    }

}
