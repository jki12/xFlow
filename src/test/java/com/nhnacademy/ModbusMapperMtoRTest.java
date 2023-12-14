package com.nhnacademy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.util.ModbusMapperMtoR;

class ModbusMapperMtoRTest {

    ModbusMapperMtoR modbusMapperMtoR = new ModbusMapperMtoR("modbusMapperMtoR");
    static final int transactionId = 123;
    static final int unitId = 8;
    static final int functionCode = 0x03;
    static final int register = 63021;
    static final int[] value = { 22, 23, 34 };

    @Test
    void init() {
        assertEquals("modbusMapperMtoR", modbusMapperMtoR.getName());
    }

    @Test
    void readMessageTest() {
        Wire inWire = new Wire();
        JSONObject dummy = new JSONObject();
        dummy.put("transactionId", 01);
        dummy.put("UnitID", 00);
        dummy.put("register", 107);
        dummy.put("value", 23);
        Message message = new JsonMessage(dummy);
        inWire.getMessageQue().add(message);

        modbusMapperMtoR.wireIn(inWire);
        assertEquals(1, modbusMapperMtoR.getInputWires().size());

        Message outputMessage = inWire.getMessageQue().poll();
        assertTrue(outputMessage instanceof JsonMessage);

        modbusMapperMtoR.readMessage();

        JsonMessage jsonOutputMessage = (JsonMessage) outputMessage;

        assertEquals(dummy.toString(), jsonOutputMessage.getContent().toString());
    }

    @Test
    void convertToModbusTest() {

        Wire inWire = new Wire();
        Wire outWire = new Wire();

        modbusMapperMtoR.wireIn(inWire);
        modbusMapperMtoR.wireOut(outWire);

        modbusMapperMtoR.convertToModbus(transactionId, unitId, functionCode, register, value);

        assertEquals(1, modbusMapperMtoR.getOutputWires().size());

        Wire outputWireData = modbusMapperMtoR.getOutputWires().iterator().next();
        assertEquals(1, outputWireData.getMessageQue().size());

    }

    @Test
    void convertIntToByteTest() {
        int value = 17;
        byte[] byteValue = modbusMapperMtoR.convertIntToByte(value);
        assertTrue(Arrays.equals(new byte[] { 0x00, 0x11 }, byteValue));
    }

    @Test
    void makeJsonMessageTest() {
        byte[] byteTransactionId = { 1, 2 };
        byte[] byteUnitId = { 3, 4 };
        byte[] byteFucntionCode = { 0x03 };
        byte[] byteValue = { 5, 6, 7, 8 };
        JSONObject result = modbusMapperMtoR.makeJsonMessage(byteTransactionId, byteUnitId, byteFucntionCode,
                byteValue);

        assertTrue(result.has("transactionId"));
        assertEquals(byteTransactionId, result.get("transactionId"));
        assertTrue(result.has("unitId"));
        assertEquals(byteUnitId, result.get("unitId"));
        assertTrue(result.has("functionCode"));
        assertEquals(byteFucntionCode, result.get("functionCode"));
        assertTrue(result.has("pdu"));
        assertEquals(byteValue, result.get("pdu"));
    }

    @Test
    void spreadMessageTest() {
        Wire inWire = new Wire();
        Wire outWire = new Wire();

        JSONObject dummy = new JSONObject();
        dummy.put("transactionId", 01);
        dummy.put("unitId", 00);
        dummy.put("functionCode", 3);
        dummy.put("register", 107);
        dummy.put("value", 23);
        Message message = new JsonMessage(dummy);
        inWire.getMessageQue().add(message);

        modbusMapperMtoR.wireIn(inWire);
        modbusMapperMtoR.wireOut(outWire);
        modbusMapperMtoR.process();

        Wire outputWire = modbusMapperMtoR.getOutputWires().iterator().next();
        assertEquals(1, outputWire.getMessageQue().size());
    }

}
