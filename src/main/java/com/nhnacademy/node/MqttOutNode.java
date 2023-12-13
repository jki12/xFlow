package com.nhnacademy.node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import com.nhnacademy.Input;
import com.nhnacademy.Wire;
import com.nhnacademy.exception.UnsupportedDataTypeException;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.util.MqttClientManager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MqttOutNode extends ActiveNode implements Input {
    private static final String DEFAULT_URI = "tcp://localhost:1883";

    private final Set<Wire> outWires = new HashSet<>();
    private final String uri;
    private IMqttClient client;
    
    public MqttOutNode() {
        this("MqttOutNode");
    }

    public MqttOutNode(String name) {
        this(DEFAULT_URI, name);
    }

    public MqttOutNode(String uri, String name) {
        super(name);

        this.uri = uri;
        preprocess();
    }

    @Override
    public JSONObject toJson() {
        JSONObject obj = super.toJson();
        obj.put("type", "mqtt out");
        obj.put("qos", "");
        
        List<String> wires = new ArrayList<>();

        for(Wire wire: outWires){
            wires.add(wire.getId().toString());
        }

        obj.put("wire", wires.toString());

        return obj;
    }

    @Override
    public void wireIn(Wire wire) {
        outWires.add(wire);
    }

    /*
     * 연결된 wire에서 data를 꺼내 지정된 uri로 보낸다
     */
    @Override
    public void preprocess() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);

            client = MqttClientManager.getMqttClient(uri);
            client.connect(options);
            
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public void process() {
        for (Wire wire : outWires) {
            if (!wire.getMessageQue().isEmpty()) {
                Message msg = wire.getMessageQue().poll();
                if (!(msg instanceof JsonMessage)) throw new UnsupportedDataTypeException();

                JSONObject content = ((JsonMessage) msg).getContent();
                
                try {
                    client.publish(content.getString("topic"), new MqttMessage(content.getJSONObject("payload").toString().getBytes()));
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }
        }
    }
}