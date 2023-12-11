package com.nhnacademy.node;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class ActiveNode extends Node implements Runnable {
    private static final int DEFAULT_INTERVAL = 1_000;

    protected long startTime;
    protected Thread thread;
    @Setter
    protected long interval;

    protected ActiveNode(String name) {
        super(name);

        thread = new Thread(this, name);
        interval = DEFAULT_INTERVAL;
    }

    public void start() {
        startTime = System.currentTimeMillis();

        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    public void process() {
    }

    public void preprocess() {
    }

    public void postprocess() {
    }

    public void run() {
        preprocess();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                process();

                Thread.sleep(interval);
            } catch (Exception ignore) {
                Thread.currentThread().interrupt();
            }
        }

        postprocess();
    }
}
