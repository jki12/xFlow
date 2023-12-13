package com.nhnacademy.node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.json.JSONException;
import org.json.JSONObject;

import com.nhnacademy.Output;
import com.nhnacademy.Wire;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.util.MqttClientManager;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
    
@Slf4j
@Getter
public class MqttInNode extends ActiveNode implements Output {
    private static final String DEFAULT_URI = "tcp://ems.nhnacademy.com:1883";
    private static final String DEFAULT_TOPIC = "#";
    private static IMqttClient client;

    private final Set<Wire> outWires = new HashSet<>();
    private final String uri;
    private boolean wantToSeeLog = false;

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
        preprocess();
    }

    public void wantnaSee(boolean want) {
        wantToSeeLog = want;
    }

    /**
     * json파일로 만들때 필요한 데이터 내놓기
     */
    @Override
    public JSONObject toJson() {
        JSONObject obj = super.toJson();
        obj.put("type", "mqtt in");
        obj.put("qos", 2);
        obj.put("topic", fromTopic);

        List<String> wires = new ArrayList<>();

        for (Wire wire : outWires) {
            wires.add(wire.getId().toString());
        }

        obj.put("wire", wires.toString());
        return obj;
    }

    @Override
    public void wireOut(Wire wire) {
        outWires.add(wire);
    }

    @Override
    public void preprocess() {
        // MqttInNode가 start되는 거랑 상관없이 toJson() 만들려면 
        // client가 생성하자마자 있어야 한다
        try {
            client = MqttClientManager.getMqttClient(uri); 
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);

            if (!(client.isConnected())){
                client.connect(options);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void process() {
        try{
            client.subscribe(fromTopic, (topic, payload) -> {
            JSONObject object = new JSONObject();
            
            try {
                object.put("topic", topic);
                object.put("payload", new JSONObject(payload.toString()));
                if(wantToSeeLog) {
                    log.warn("topic - {}", topic);
                    log.warn("payload - {}", payload);
                }
            } catch(JSONException ignore) {
                log.warn("topic : {} json형식의 데이터가 아닙니다.", topic);
            }
            
            Message msg = new JsonMessage(object);
            for (Wire wire : outWires) {
                    wire.getMessageQue().add(msg);
                }
            });
        } catch(MqttSecurityException e) {
            log.error(e.getMessage());
        } catch(MqttException e) {
            log.error(e.getMessage());
        } 
            
        
    }

}