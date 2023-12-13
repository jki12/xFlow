package com.nhnacademy;

import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.MqttOutNode;

public class MqttInTest {
    public static void main(String[] args) {
        MqttInNode inNode = new MqttInNode("inNode");
        MqttOutNode outNode = new MqttOutNode("OutNode");
        Wire wire = new Wire();
        // inNode.setFromTopic("application/#");
        inNode.toJson();
        outNode.toJson();

        inNode.wireOut(wire);
        outNode.wireIn(wire);

        inNode.wantnaSee(true);
        
        inNode.start();
        outNode.start();
    }
}