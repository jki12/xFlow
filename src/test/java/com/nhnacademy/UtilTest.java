package com.nhnacademy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.nhnacademy.util.Util;

public class UtilTest {

    @Test
    void a() {
        byte a = 0x12;
        byte b = 0x34;

        assertEquals(0x1234, Util.toShort(a, b));
    }
    
}
