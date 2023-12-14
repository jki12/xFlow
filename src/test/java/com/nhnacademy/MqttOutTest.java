package com.nhnacademy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.Executor;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.node.MqttOutNode;
import com.oracle.truffle.js.runtime.objects.Null;

    public class MqttOutTest {

        MqttOutNode mqttOutNode;
        JSONObject jsonObject;

    @BeforeEach
    void reset(){
        mqttOutNode = new MqttOutNode();
        jsonObject = mqttOutNode.toJson();
    }

    @DisplayName("wireIn 메서드에 매개변수로 오는 wire가 null값인 경우")
    @Test
    void wireTest(){
        // given
        Wire tempWire = null;
        // when
        Executable executable = () -> mqttOutNode.wireIn(tempWire);
        // then
        assertThrows(NullPointerException.class,executable);

    }

    @Test
    @DisplayName("constructor test")
    void constructors() {
        MqttOutNode outNode1 = new MqttOutNode();
        MqttOutNode outNode2 = new MqttOutNode("outNode2");


        assertEquals("MqttOutNode", outNode1.getName());
        assertEquals("outNode2", outNode2.getName());
    }

    @Test
    @DisplayName("toJson test")
    @BeforeEach
    void toJsonTestPreprocess() {
        final String TOPIC = "application/I/want/daldalguri" ;
        MqttOutNode outNode1 = new MqttOutNode();
        Wire wire = new Wire();
        
        outNode1.wireIn(wire);
        outNode1.start();

        JSONObject obj = new JSONObject();

        obj.put("topic", TOPIC);
        obj.put("payload", 11123233);
        obj.put("dfdf", 11123233);
        
        JsonMessage msg = new JsonMessage(obj);

        try {
            wire.getMessageQue().put(msg);
            outNode1.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    
}