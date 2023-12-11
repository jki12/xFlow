package com.nhnacademy.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttClientManager {
    private static final Map<String, MqttClient> map = new HashMap<>();

    private MqttClientManager() {
    }

    public static MqttClient getMqttClient(String uri) {
        if (uri == null) throw new IllegalArgumentException();

        if (map.get(uri) == null) {
            UUID uuid = UUID.randomUUID();

            try {
                map.put(uri, new MqttClient(uri, uuid.toString()));

            } catch (MqttException e) {
                log.error(e.getMessage());
            }
        }

        return map.get(uri);
    }
}
