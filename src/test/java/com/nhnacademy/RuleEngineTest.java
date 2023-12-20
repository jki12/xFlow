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
import com.nhnacademy.databaserepo.*;
import com.nhnacademy.message.JsonMessage;
import com.oracle.truffle.regex.tregex.util.json.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleEngineTest {
    
    RuleEngine ruleEngine;
    Repo repo = new InMemoryRepo();
    JSONObject typeMqtt;
    JSONObject typeModbus;

    @BeforeEach
    void setUp(){
        ruleEngine = new RuleEngine("RuleEngine",repo);
        typeMqtt = new JSONObject();
        typeModbus = new JSONObject();
        typeMqtt.put("devEui", "24e124128c067999");
        typeMqtt.put("sensorType","temperature");
        typeMqtt.put("value",20.2);
        typeModbus.put("registerId", "101");
        typeModbus.put("value",20.2);
        
    }

    @Test
    void checkProtocolType(){
        assertEquals("mqtt", ruleEngine.checkProtocolType(typeMqtt));
        assertEquals("modbus", ruleEngine.checkProtocolType(typeModbus));
        JSONObject invalidType = new JSONObject();
        assertThrows(UnsupportedProtocolTypeException.class,  () -> {
            ruleEngine.checkProtocolType(invalidType);
        });
    }

    @Test
    void updatedMqttTest(){
        assertTrue(repo.addData(typeMqtt.getString("devEui"),typeMqtt.getString("sensorType"),typeMqtt.getDouble("value")));
        JSONObject getDataCheck = repo.getData(typeMqtt.getString("devEui"), typeMqtt.getString("sensorType"));
        assertTrue(getDataCheck.has("registerId"));
        assertTrue(getDataCheck.has("value"));
        assertEquals(typeMqtt.getDouble("value"), getDataCheck.getDouble("value"));
    }

    @Test
    void updatedModbusTest(){
        assertTrue(repo.addData(typeModbus.getString("registerId"),typeModbus.getDouble("value")));
        JSONObject getDataCheck = repo.getData(typeModbus.getString("registerId"));
        assertTrue(getDataCheck.has("devEui"));
        assertTrue(getDataCheck.has("sensorType"));
        assertEquals(typeModbus.getDouble("value"), getDataCheck.getDouble("value"));
    }
}
