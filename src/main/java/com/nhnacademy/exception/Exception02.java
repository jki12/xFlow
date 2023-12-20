package com.nhnacademy.exception;

public class Exception02 {

    public static byte[] Exception02Error(byte functionCode) {
        byte[] errorCodes = {(byte)(functionCode+80), 02};
        return errorCodes;
    }

}
