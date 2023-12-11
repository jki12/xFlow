package com.nhnacademy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nhnacademy.function.Preprocess;

public class topicFilter {

    @Test
    @DisplayName("topic filter test")
    void a() {
        String f1 = "application/#";
        String f2 = "application/+";
        String f3 = "application/+/+/#";
        String f4 = "application/+/+/test3";
        String f5 = "application/+/#";

        String t1 = "application/test1/test2/test3";
        String t2 = "application/test1";
        String t3 = "test/gateway";

        assertTrue(Preprocess.enableTopic(f1, t1));
        assertFalse(Preprocess.enableTopic(f1, t3));
        assertTrue(Preprocess.enableTopic(f2, t2));
        assertFalse(Preprocess.enableTopic(f2, t1));
        assertTrue(Preprocess.enableTopic(f3, t1));
        assertTrue(Preprocess.enableTopic(f4, t1));
        assertTrue(Preprocess.enableTopic(f5, t1));
        assertFalse(Preprocess.enableTopic(f5, t2));
    }
    
}
