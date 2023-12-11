package com.nhnacademy.function;

import java.util.Set;
import java.util.function.BiConsumer;

import org.json.JSONObject;

import com.nhnacademy.Config;
import com.nhnacademy.Wire;
import com.nhnacademy.exception.UnsupportedDataTypeException;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Preprocess implements BiConsumer<Set<Wire>, Set<Wire>> {
    private static final String WIDECARD_ALL = "#";
    private static final String WILDCARD_ANY = "+";

    public static boolean enableTopic(String filter, final String topic) {
        if (topic == null || filter == null) throw new IllegalArgumentException();

        var topicList = topic.split("/");
        var filterList = filter.split("/");

        boolean temp = false; // 필터의 마지막 값이 WILDCARD_ANY인지 확인하는 변수
        int len = Math.min(topicList.length, filterList.length);
        for (int i = 0; i < len; ++i) {
            if (filterList[i].equals(WIDECARD_ALL)) return true;
            if (!filterList[i].equals(topicList[i]) || !filterList[i].equals(WILDCARD_ANY)) return false;

            temp = (filterList[i].equals(WILDCARD_ANY));
        }

        return !(temp && filterList.length != topicList.length);
    }

    private static boolean enableSensor(String allowedSensor, final JSONObject payload) {
        if (allowedSensor.equals("all")) return true;

        var allowedSeneorList = allowedSensor.split(",".trim());

        int count = 0;
        if(payload.has("object")) {
            JSONObject object = payload.getJSONObject("object");
            for(int i = 0; i < allowedSeneorList.length; i++) {
                if (object.has(allowedSeneorList[i])) count++;
            }
        }

        return (allowedSeneorList.length == count);
    }

    @Override
    public void accept(Set<Wire> inWires, Set<Wire> outWires) {
        try {
            for (Wire inWire : inWires) {
                var messageQ = inWire.getMessageQue();
    
                if (!messageQ.isEmpty()) {
                    Message msg = messageQ.poll();
                    if (!(msg instanceof JsonMessage)) throw new UnsupportedDataTypeException();

                    JSONObject content = ((JsonMessage) msg).getContent();
                    
                    boolean isAllowedTopic = enableTopic(Config.getCurrentConfig().getString("applicationName"), content.getString("topic"));
                    boolean isAllowedSensor = enableSensor(Config.getCurrentConfig().getString("s"), content.getJSONObject("payload"));
                    
                    if (isAllowedTopic && isAllowedSensor) {
                        for (Wire outWire : outWires) {
                            outWire.getMessageQue().put(msg);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
