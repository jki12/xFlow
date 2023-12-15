package com.nhnacademy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nhnacademy.exception.*;
import com.nhnacademy.RuleEngine.RuleEngine;
import com.nhnacademy.message.JsonMessage;
import com.oracle.truffle.regex.tregex.util.json.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleEngineTest {
    
    RuleEngine ruleEngine;
    InMemoryRepo repo = new InMemoryRepo(...);
    JSONObject sucessMqttIn;
    JSONObject failedMqttIn;
    JSONObject sucessModbusIn;
    JSONObject failedModbusIn;

    @BeforeEach
    void setUp(){
        ruleEngine = new RuleEngine("RuleEngine",repo);
        sucessMqttIn = new JSONObject();
        failedMqttIn = new JSONObject();
        sucessMqttIn.put("devEui", "24e124128c067999");
        failedMqttIn.put("sensorName", "강의실A(AM107-067999)");
        sucessMqttIn.put("sensorName", "강의실A(AM107-067999)");
        failedMqttIn.put("sensor","temperature");
        sucessMqttIn.put("sensor","temperature");
        failedMqttIn.put("value", 20.1);
        sucessMqttIn.put("value",20.1);
        sucessMqttIn.put("type",11);
        sucessModbusIn = new JSONObject();
        failedModbusIn = new JSONObject();
        sucessModbusIn.put("header", "0,0,0,0,0,0,0");
        sucessModbusIn.put("pdu","3,2,20");
        sucessModbusIn.put("value","202");
        sucessModbusIn.put("type", 21);
        failedModbusIn.put("header", "0,0,0,0,0,0,0");
        failedModbusIn.put("pdu","3,2,20");
        failedModbusIn.put("type", 21);
        sucessModbusIn.put("registerAdress",101);
        failedModbusIn.put("value","202");


    }

    @Test
    void sendMqttAndDB(){
        JsonMessage message;
        message = ruleEngine.sendMqttAndDB(sucessMqttIn);
        assertNotEquals(null, message,"message success input for success");
        message = ruleEngine.sendMqttAndDB(failedMqttIn);
        assertEquals(null, message,"message failed input for failed");
    }

    @Test
    void dbToMqtt(){
        JsonMessage message;
        JSONObject dbToMqttTest = sucessMqttIn;
        dbToMqttTest.remove("value");
        message = ruleEngine.dbToMqtt(dbToMqttTest);
        assertTrue(message.getContent().has("value"));
        dbToMqttTest = failedMqttIn;
        dbToMqttTest.remove("value");
        message = ruleEngine.dbToMqtt(dbToMqttTest);
        assertEquals(null, message,"message failed input for failed");
    }

    @Test
    void modbusToDB(){
        assertTrue(ruleEngine.modbusToDB(sucessModbusIn));
        assertFalse(ruleEngine.modbusToDB(failedModbusIn));
    }

    @Test
    void dbToModbus(){
        JsonMessage message;
        JSONObject dbToModbusTest = sucessModbusIn;
        dbToModbusTest.remove("value");
        message = ruleEngine.dbToModbus(dbToModbusTest);
        assertTrue(message.getContent().has("value"));
        dbToModbusTest = failedMqttIn;
        dbToModbusTest.remove("value");
        message = ruleEngine.dbToMqtt(dbToModbusTest);
        assertEquals(null, message,"message failed input for failed");
    }

    @Test
    void checkTypeCode(){
        JSONObject inputCode = new JSONObject();
        inputCode.put("type", 11);
        assertThrows(FailedInputException.class, () -> {
            ruleEngine.typeExecute(inputCode);
        });
        inputCode.put("type", 12);
        assertThrows(FailedOutputException.class, () -> {
            ruleEngine.typeExecute(inputCode);
        });
        inputCode.put("type", 21);
        assertThrows(FailedInputException.class, () -> {
            ruleEngine.typeExecute(inputCode);
        });
        inputCode.put("type", 22);
        assertThrows(FailedOutputException.class, () -> {
            ruleEngine.typeExecute(inputCode);
        });
        inputCode.put("type", 46);
        assertThrows(UnsupportedTypeNumberException.class, () -> {
            ruleEngine.typeExecute(inputCode);
        });
    }

    @Test
    void wholeMqttTest(){
        Wire inputWire = new Wire();
        JsonMessage message = new JsonMessage(sucessMqttIn);
        inputWire.getMessageQue().add(message);
        ruleEngine.wireIn(inputWire);
        ruleEngine.process();
        assertTrue(21,repo.getDb(sucessMqttIn.getString("devEui"),sucessMqttIn.getString("sensorType")));
    }

    @Test
    void wholeModbusInDbTest(){
        Wire inputWire = new Wire();
        JSONObject test = sucessModbusIn;
        test.put("type", 22);
        JsonMessage message = new JsonMessage(test);
        inputWire.getMessageQue().add(message);
        ruleEngine.wireIn(inputWire);
        ruleEngine.process();
        assertTrue(20.2,repo.getDb(sucessModbusIn.getString("registerAddress")));
    }
    @Test
    void wholeDbInModbusTest(){
        Wire inputWire = new Wire();
        JSONObject test = sucessModbusIn;
        test.remove("value");
        JsonMessage message = new JsonMessage(test);
        inputWire.getMessageQue().add(message);
        ruleEngine.wireIn(inputWire);
        ruleEngine.process();
        assertTrue(ruleEngine.getOutWiresSize() == 1);
    }
}
