package com.nhnacademy.RuleEngine;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Input;
import com.nhnacademy.Output;
import com.nhnacademy.Wire;
import com.nhnacademy.exception.*;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.node.ActiveNode;
import com.oracle.truffle.regex.tregex.util.json.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleEngine extends ActiveNode implements Input, Output {
    // TODO 데이터 베이스 클래스 전체를 받아와서 처리 하자! 어때 즐겁지?
    // private DB database;
    private Set<Wire> outWires = new HashSet<>();
    private Set<Wire> inWires = new HashSet<>();

    protected RuleEngine(String name) {
        super(name);
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
                    Message msg = messageQ.poll();
                    JSONObject content = ((JsonMessage) msg).getContent();
                    switch (TypeExecute(content)) {
                    case 11:
                        if (MqttToDB()) {
                            log.info("Success input DB from Mqtt");
                        } else {
                            throw new FailedInputException();
                        }
                        break;
                    case 12:
                        DBToMqtt();
                        break;
                    case 21:
                        if (ModbusToDB()) {
                            log.info("Success input DB from Modbus");
                        } else {
                            throw new FailedInputException();
                        }
                        
                        break;
                    case 22:
                        DBToModbus();
                        break;

                    default:
                        throw new UnsupportedTypeNumberException();
                    }
                }

            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    /**
     * decimal Type = 10 ~ mqtt 20 ~ modbus ~1 = inDB ~2 = outDB ~3 = broadcast
     */
    public int TypeExecute(JSONObject content) {
        if (content.has("Type")) {
            int decimal = content.getInt("Type");
            return decimal;
        } else {
            throw new UndefindedTypeException();
        }
    }

    public boolean MqttToDB() {
        // TODO db의 데이터 타입과 같은 타입으로 리턴 해서 넣을 것;
        return false;
    }

    public JsonMessage DBToMqtt() {
        // TODO db의 데이터 타입을 형식에 맞춰서 보내줄 것;
        return null;
    }

    public boolean ModbusToDB() {
        // TODO db의 데이터 타입과 같은 타입으로 리턴 해서 넣을 것;
        return false;
    }

    public JsonMessage DBToModbus() {
        // TODO db의 데이터 타입을 형식에 맞춰서 보내줄 것;
        return null;
    }

}
