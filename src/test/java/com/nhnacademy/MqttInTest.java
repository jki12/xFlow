package com.nhnacademy;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.MqttOutNode;

public class MqttInTest {
    MqttInNode inNode;
    Wire wire;
    String message = "잘못된 예측입니다";
    

    @BeforeEach
    void reset() {
        inNode = new MqttInNode("inNode");
        wire = new Wire();
        inNode.wireOut(wire);

    }
    
    @Test
    @DisplayName("topic 설정이 잘 되었는가")
    void topicSetting() {
        inNode.setFromTopic("application/#");
        inNode.start();

        assertEquals("application/#",inNode.getFromTopic());
    }

    @Test
    @DisplayName("inNode의 정보가 json으로 잘 나오는 가")
    void toJsonTest() {
        JSONObject inNodeJson = inNode.toJson();

        assertAll(()-> assertEquals("mqtt in", (String)inNodeJson.get("type"),message),
        () -> assertEquals(2, inNodeJson.get("qos"),message),
        () -> assertEquals("["+wire.getId()+"]", (String)inNodeJson.get("wire"),message),
        () -> assertEquals(inNode.getName(), inNodeJson.get("name"),message),
        () -> assertEquals(inNode.getId(),inNodeJson.get("id"),message)
        );
    }

    @Test
    @DisplayName("inNode가 잘 돌아가는가")
    void startTest() {
        

        inNode.wireOut(wire);

        inNode.wantnaSee(true);
        
        inNode.start();
    }
    
}