package com.nhnacademy.message;

import org.json.JSONObject;

import lombok.Getter;

@Getter
public class JsonMessage extends Message {
    private JSONObject content;

    public JsonMessage(JSONObject content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content.toString();
    }
}
