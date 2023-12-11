package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.nhnacademy.Output;
import com.nhnacademy.Wire;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
    
@Slf4j
@Getter
public class MqttInNode extends ActiveNode implements Output {
    private static final String DEFAULT_URI = "tcp://ems.nhnacademy.com:1883";
    private static final String DEFAULT_TOPIC = "#";

    private final Set<Wire> outWires = new HashSet<>();
    private final String uri;

    @Setter
    private String fromTopic;

    public MqttInNode(String name) {
        this(DEFAULT_URI, name);
    }
    
    public MqttInNode(String uri, String name) {
        this(uri, DEFAULT_TOPIC, name);
    }

    public MqttInNode(String uri, String topic, String name) {
        super(name);

        this.uri = uri;
        fromTopic = topic;
    }

    @Override
    public JSONObject toJson() {
        JSONObject obj = super.toJson();
        obj.put("topic", fromTopic);

        String[] out = new String[outWires.size()];
        int index = 0;

        for (Wire wire : outWires) {
            out[index++] = wire.getId().toString();
        }

        obj.put("outWires", out);

        return obj;
    }

    @Override
    public void wireOut(Wire wire) {
        outWires.add(wire);
    }

    @Override
    public void preprocess() {
        UUID clientId = UUID.randomUUID(); // node, client id를 분리해서 사용하기 위해 따로 받았습니다.

        try (IMqttClient client = new MqttClient(uri, clientId.toString())) {
            client.connect();

            client.subscribe(fromTopic, (topic, payload) -> {
                JSONObject object = new JSONObject();

                try {
                    object.put("topic", topic);
                    object.put("payload", new JSONObject(payload.toString()));
                } catch(JSONException ignore) {
                    log.warn("topic : {} json형식의 데이터가 아닙니다.", topic);
                }

                Message msg = new JsonMessage(object);
                for (Wire wire : outWires) {
                    wire.getMessageQue().add(msg);
                }
            });
            
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}