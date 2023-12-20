package com.nhnacademy.RuleEngine;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nhnacademy.Input;
import com.nhnacademy.Output;
import com.nhnacademy.Wire;
import com.nhnacademy.databaserepo.Repo;
import com.nhnacademy.exception.*;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.node.ActiveNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleEngine extends ActiveNode implements Input, Output {
    private final String TYPE_MQTT = "mqtt";
    private final String TYPE_MODBUS = "modbus";
    private Repo repo;
    private Set<Wire> outWires = new HashSet<>();
    private Set<Wire> inWires = new HashSet<>();

    public int getOutWiresSize() {
        return outWires.size();
    }

    public RuleEngine(String name, Repo repo) {
        super(name);
        this.repo = repo;

    }

    @Override
    public void wireOut(Wire wire) {
        outWires.add(wire);
    }

    @Override
    public void wireIn(Wire wire) {
        inWires.add(wire);
    }

    @Override
    public void process() {
        try {
            for (Wire inWire : inWires) {
                var messageQ = inWire.getMessageQue();
                if (!messageQ.isEmpty()) {
                    JsonMessage responseMessage;
                    Message msg = messageQ.poll();
                    JSONObject content = ((JsonMessage) msg).getContent();
                    try {
                        String protocolType = checkProtocolType(content);
                        switch (protocolType) {
                        case TYPE_MQTT:
                            if(repo.addData(content.getString("devEui"), content.getString("sensorType"),content.getDouble("value"))){
                                content = repo.getData(content.getString("devEui"), content.getString("sensorType"));
                                responseMessage = new JsonMessage(content);
                            }else{
                                log.error("Failed Input data !!");
                                throw new FailedInputException();
                            }
                            
                            break;
                        case TYPE_MODBUS:
                            if(repo.addData(content.getString("registerId"), content.getDouble("value"))){
                                content = repo.getData(content.getString("registerId"));
                                responseMessage = new JsonMessage(content);
                            }else{
                                log.error("Failed Input data !!");
                                throw new FailedInputException();
                            }
                            
                            break;
                        default:
                            throw new UnsupportedProtocolTypeException();
                        }
                    } catch (UnsupportedProtocolTypeException e) {
                        log.error("UnsupportedProtocolType!!!");
                    }

                    for (Wire outWire : outWires) {
                        outWire.getMessageQue().add(responseMessage);
                    }
                }

            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
    /**
     * <p>입력되는 프로토콜에 따라 다른 JSONObject 값을 구분하여 특정 프로토콜을 찾는 함수입니다
     * <p>각 JSONObject의 고유한 고유값을 받을 수 있으며 구분 할 수 있습니다.
     * @param content
     * <p>입력 받는 JSONObject 형식의 instance입니다. Protocol을 구멸 할 수 있는 Key : Value를 찾습니다.
     * @return
     * <p>지원이 가능한 Protocol이름을 String으로 반환합니다. 지원 가능한 Protocol은 클래스 상단 final로 선언되어 있습니다.
     */
    public String checkProtocolType(JSONObject content) {
        if (content.has("registerId")) {
            return TYPE_MODBUS;
        } else if ((content.has("devEui")) && (content.has("sensorType"))) {
            return TYPE_MQTT;
        } else {
            throw new UnsupportedProtocolTypeException();
        }

    }
}
