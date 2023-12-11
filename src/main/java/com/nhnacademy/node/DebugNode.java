package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Input;
import com.nhnacademy.Wire;
import com.nhnacademy.message.Message;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class DebugNode extends ActiveNode implements Input {
    private Set<Wire> inputWires = new HashSet<>();

    public DebugNode(String name) {
        super(name);
    }

    public DebugNode() {
        this("debug");
    }

    @Override
    public JSONObject toJson() {
        // TODO 필요한 내용 추가

        return super.toJson();
    }

    /**
     * 와이어를 inputWire Set 에 넣는다.
     * 그러면 inputWire를 통해 해당 노드에 연결된 와이어에 있는 Blocking Queue에 접근 할 수있다.
     */
    @Override
    public void wireIn(Wire wire) {
        inputWires.add(wire);
    }

    /**
     * input와이어에 있는 큐가 비어있는 동안 메시지를 출력하지 않다가 큐에 메시지가 들어오면 해당 메시지를 로그로 출력함
     */
    @Override
    public void process() {
        try {
            for (Wire wires : inputWires) {
                if (wires.getMessageQue().isEmpty()) {
                    continue;
                }

                Message msg = wires.getMessageQue().poll();

                log.info(msg.toString());
            }

        } catch (Exception ignore) {
            log.error(ignore.getMessage());
        }
    }
}