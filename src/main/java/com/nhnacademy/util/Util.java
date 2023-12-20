package com.nhnacademy.util;

import java.util.Arrays;

import org.json.JSONObject;

import com.nhnacademy.exception.UnsupportedFunctionCodeException;

public class Util {
    public static final int MODBUS_TCP = 0x0000;
    public static final int MBAP_BYTE_LENGTH = 7;
    public static final int DEFAULT_UNIT_ID = 0x01;

    private Util() {
        throw new IllegalStateException();
    }

    public static byte[] toLittleEndian(short n) {
        n = Short.reverseBytes(n);

        return new byte[] { (byte) ((0xff00 & n) >> 8), (byte) n };
    }

    public static byte[] toByteArray(short n) {
        return new byte[] { (byte) ((0xff00 & n) >> 8), (byte) n };
    }

    public static byte[] makeMBAP(short transactionId, short length) {
        return makeMBAP(transactionId, length, (byte) 0x01);
    }

    /*
     *  MODBUS-TCP는 0x0000의 고정값을 사용합니다.
     */
    public static byte[] makeMBAP(short transactionId, short length, byte unitId) {
        if (transactionId < 0 || length < 0 || unitId < 0) throw new IllegalArgumentException();

        byte[] header = new byte[MBAP_BYTE_LENGTH];

        byte[] temp = toByteArray(transactionId);
        header[0] = temp[0];
        header[1] = temp[1];

        temp = toByteArray(length);
        header[4] = temp[0];
        header[5] = temp[1];

        header[6] = unitId;

        return header;
    }

    public static short toShort(byte high, byte low) {
        return (short) ((high << 8) | (0xff & low));
    }

    public static int toInteger(short high, short low) {
        return (high << 16) | low;
    }

    public static byte[] concat(byte[] mbap, byte[] pdu) {
        if (mbap == null || pdu == null) throw new IllegalArgumentException();

        byte[] adu = new byte[mbap.length + pdu.length];

        int index = 0;
        for (int i = 0; i < mbap.length; ++i) {
            adu[index++] = mbap[i];
        }

        for (int i = 0; i < pdu.length; ++i) {
            adu[index++] = pdu[i];
        }

        return adu;
    }

    /*
     * modbus response를 json 형식으로 바꿔주는 함수
     * 0x03번 함수코드의 결과 처리
     */
    public static JSONObject toJson(byte[] response, int length) {
        if (response == null || response.length < 9) throw new IllegalArgumentException();

        if (response[7] != 0x03) throw new UnsupportedFunctionCodeException();

        JSONObject obj = new JSONObject();

        // parsing mbap.
        obj.put("transactionId", toShort(response[0], response[1]));
        obj.put("protocolId", 0);
        obj.put("length", toShort(response[4], response[5]));
        obj.put("unitId", DEFAULT_UNIT_ID);

        // parsing pdu.
        obj.put("functionCode", response[7]);
        obj.put("byteCount", response[8]);
        obj.put("value", toShort(response[9], response[10]));

        return obj;
    }
}
