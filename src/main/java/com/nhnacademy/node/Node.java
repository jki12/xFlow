package com.nhnacademy.node;

import java.util.UUID;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Node {
    private final UUID id;
    private final long createdTime;
    private String name;

    protected Node(String name) {
        this.id = UUID.randomUUID();
        this.createdTime = System.currentTimeMillis();

        this.name = name;
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();

        obj.put("name", getName());
        obj.put("id", getId());

        return obj;
    }
}
