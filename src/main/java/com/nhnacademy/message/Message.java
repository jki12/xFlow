package com.nhnacademy.message;

import java.util.UUID;

import lombok.Getter;

@Getter
public abstract class Message {
    private final UUID id;

    protected Message() {
        id = UUID.randomUUID();
    }
}
