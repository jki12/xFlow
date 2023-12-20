package com.nhnacademy.exception;

public class Exception03 {

    public static byte[] exception03Error(byte functionCode) {
        byte errorCode =  03;
        byte exceptionCode = (byte)(functionCode + 80);

        byte[] errorBytes = {errorCode, exceptionCode};

        return errorBytes;
    }

}
