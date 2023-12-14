package com.nhnacademy.util;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ModbusClientManager {
    private static final Map<String, Socket> map = new HashMap<>();

    public static Socket getSocket(String host) {
        if (host == null) {
            throw new IllegalArgumentException();
        }
        if (map.get(host) == null) {
            return null;
        }
        return map.get(host);
    }
}
