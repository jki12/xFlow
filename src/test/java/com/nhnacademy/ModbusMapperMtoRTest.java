package com.nhnacademy;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Base64;

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
    static final int value = 23;

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

        modbusMapperMtoR.convertToModbus(register, value);

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
        byte[] byteRegister = { 3 };
        byte[] byteValue = { 5, 6, 7, 8 };
        JSONObject result = modbusMapperMtoR.makeJsonMessage(byteRegister, byteValue);

        String encodedRegister = result.getString("registerAddress");
        String encodedValue = result.getString("value");

        byte[] decodedRegister = Base64.getDecoder().decode(encodedRegister);
        byte[] decodedValue = Base64.getDecoder().decode(encodedValue);

        assertArrayEquals(byteRegister, decodedRegister);
        assertArrayEquals(byteValue, decodedValue);
    }

    @Test
    void spreadMessageTest() {
        Wire inWire = new Wire();
        Wire outWire = new Wire();

        JSONObject dummy = new JSONObject();
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
