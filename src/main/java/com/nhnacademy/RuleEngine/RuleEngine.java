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
    private InMemoryRepo repo;
    private Set<Wire> outWires = new HashSet<>();
    private Set<Wire> inWires = new HashSet<>();

    /* protected RuleEngine(String name) {
        super(name);
    } */
    protected RuleEngine(String name, InMemoryRepo repo) {
        this.repo = repo;
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
                        if (SendMqttAndDB(content) != null) {
                            log.info("Success input DB from Mqtt");
                        } else {
                            throw new FailedInputException();
                        }
                        break;
                    case 12:
                        JsonMessage outMqttMessage = DBToMqtt(content);
                        if(outMqttMessage != null){
                            Wire outWire = new Wire();
                            outWire.getMessageQue().add(outMqttMessage);
                            wireOut(outWire);
                        }else{
                            throw new FailedOutputException();
                        }
                        break;
                    case 21:
                        if (ModbusToDB(content)) {
                            log.info("Success input DB from Modbus");
                        } else {
                            throw new FailedInputException();
                        }
                        
                        break;
                    case 22:
                        JsonMessage outModbusMessage = DBToModbus(content);
                        if(outModbusMessage != null){
                            Wire outWire = new Wire();
                            outWire.getMessageQue().add(outModbusMessage);
                            wireOut(outWire);
                        }else{
                            throw new FailedOutputException();
                        }
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

    public JsonMessage SendMqttAndDB(JSONObject content) {
        String Euid = content.getString("devEui");
        String sensor = content.getString("sensor");
        int value = content.getInt("value");
        try {
            repo.setDb(Euid,sensor,value);
            JsonMessage sendJsonMessage = new JsonMessage(content);
            return sendJsonMessage;
        } catch (Exception e) {
            log.warn("Do not have any keys");
            return null;
        }
    }

    public JsonMessage DBToMqtt(JSONObject content) {
        String Euid = content.getString("devEui");
        String sensor = content.getString("sensor");
        int value;
        try {
            value = repo.getDb(Euid,sensor);
            content.put("value", value);
            JsonMessage sendMessage = new JsonMessage(content);
            return sendMessage;
        } catch (Exception e) {
            log.warn("Do not have any sensor");
            return null;
        }
        
    }

    public boolean ModbusToDB(JSONObject content) {
        int registerID = content.getInt("adress");
        int value = content.getInt("value");
        try{
            repo.setDb(registerID,value);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public JsonMessage DBToModbus(JSONObject content) {
        int registerID = content.getInt("adress");
        int value;
        try {
            value = repo.getDb(registerID);
            content.put("value", value);
            JsonMessage sendJsonMessage = new JsonMessage(content);
            return sendJsonMessage;
        } catch (Exception e) {
            log.warn("Do not have registerId!");
            return null;
        }
        
    }

}
