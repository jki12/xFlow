package com.nhnacademy;

import org.json.JSONObject;

import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.node.MqttOutNode;

public class MqttOutTest {
    public static void main(String[] args) {
        MqttOutNode outNode = new MqttOutNode("OutNode");
        Wire wire = new Wire();
        String topic = "application/df/ddddd";
        JSONObject obj = new JSONObject();
        obj.put("topic", topic);
        obj.put("payload", 11123233);
        obj.put("dfdf", 11123233);


        JsonMessage msg = new JsonMessage(obj);

        outNode.wireIn(wire);
        wire.getMessageQue().add(msg);

        outNode.start();

    }

    
}