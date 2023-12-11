package com.nhnacademy.info;

import lombok.Getter;

/*
 * Packet(message)의 받은 수, 보낸 수, 비정상으로 처리되지 못한 수를 기록합니다.
 */
@Getter
public class Info {
    private int receiveCount;
    private int sendCount;
    private int abnormalCount;

    public void increaseReceiveCount() {
        receiveCount++;
    }

    public void increaseSendCount() {
        sendCount++;
    }

    public void increaseAbnormalCount() {
        abnormalCount++;
    }
}
