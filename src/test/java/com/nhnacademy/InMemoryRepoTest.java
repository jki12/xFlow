package com.nhnacademy;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nhnacademy.databaserepo.InMemoryRepo;


public class InMemoryRepoTest {
    InMemoryRepo inMemoryRepo;

    @BeforeEach
    void init(){
        inMemoryRepo = new InMemoryRepo();
    }

    @Test
    void getMapValuesTest(){
        assertEquals(inMemoryRepo.getMapValues("101"), "{\"UnitID\":1,\"sensorName\":\"강의실A(AM107-067999)\",\"sensorType\":\"temperature\",\"value\":20,\"devEui\":\"24e124128c067999\"}");
        assertThrows(NullPointerException.class, () ->{
            inMemoryRepo.getMapValues("102");
        });
    }

    @Test
    void jsonSetTest(){

    }
}
