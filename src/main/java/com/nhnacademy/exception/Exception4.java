package com.nhnacademy.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Exception4 extends RuntimeException {
    public static byte[] exception04Error(byte functionCode) {
        byte[] errorBytes = new byte[2];
        errorBytes[0] = (byte)(functionCode + 80);  // error code
        errorBytes[1] = 04;                         // exception code

        log.error("Illegal Function");
        return errorBytes;
    }
}
