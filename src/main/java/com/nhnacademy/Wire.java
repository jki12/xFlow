package com.nhnacademy;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.nhnacademy.message.Message;

import lombok.Getter;

@Getter
public class Wire {
    private final UUID id;
    private final BlockingQueue<Message> messageQue = new LinkedBlockingQueue<>();

    public Wire() {
        id = UUID.randomUUID();
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Wire)) return false;
        
        return this.id.equals(((Wire) obj).id);
    }
}
