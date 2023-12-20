package com.nhnacademy;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nhnacademy.databaserepo.InMemoryRepoArray;

class InMemoryRepoArrayTest {
    InMemoryRepoArray inMemoryRepoArray = new InMemoryRepoArray();

    @BeforeEach
    void init() {
        inMemoryRepoArray.add("24e124128c067999", "temperature", 20);
        inMemoryRepoArray.add("24e124128c067999", "temperature", 21);
        inMemoryRepoArray.add("24e124128c067999", "temperature", 22);
        inMemoryRepoArray.add("24e124128c067999", "temperature", 23);
        inMemoryRepoArray.add("24e124128c067999", "temperature", 24);
        inMemoryRepoArray.add("103", 20);
        inMemoryRepoArray.add("103", 21);
        inMemoryRepoArray.add("103", 22);
    }

    @Test
    void getDataTest() {
        JSONObject json = inMemoryRepoArray.getData("24e124128c067999", "temperature");
        assertEquals(json.toString(),
                "{\"requestData\":{\"registerId\":\"101\",\"sensorName\":\"강의실A(AM107-067999)\",\"sensorType\":\"temperature\",\"unitId\":1,\"value\":23,\"devEui\":\"24e124128c067999\"}}");

        json = inMemoryRepoArray.getData("103");
        assertEquals(json.toString(),
                "{\"requestData\":{\"registerId\":\"103\",\"sensorName\":\"강의실A(EM320-TH-389818)\",\"sensorType\":\"temperature\",\"unitId\":1,\"value\":23,\"devEui\":\"24e124785c389818\"}}");

        assertThrows(NoSuchElementException.class, () -> {
            inMemoryRepoArray.getData("24e124128c067000", "temperature");
            inMemoryRepoArray.getData("24e124128c067999", "none");
            inMemoryRepoArray.getData("102");
        });
    }

    @Test
    void addDataTest() {
        JSONObject json;

        assertTrue(inMemoryRepoArray.add("105", 20));
        json = inMemoryRepoArray.getData("105");
        assertEquals(json.toString(),
                "{\"requestData\":{\"registerId\":\"105\",\"sensorName\":\"강의실A(EM320-TH-421885)\",\"sensorType\":\"temperature\",\"unitId\":1,\"value\":20,\"devEui\":\"24e124785c421885\"}}");

        assertTrue(inMemoryRepoArray.add("105", 21));
        json = inMemoryRepoArray.getData("105");
        assertEquals(json.toString(),
                "{\"requestData\":{\"registerId\":\"105\",\"sensorName\":\"강의실A(EM320-TH-421885)\",\"sensorType\":\"temperature\",\"unitId\":1,\"value\":21,\"devEui\":\"24e124785c421885\"}}");

        assertTrue(inMemoryRepoArray.add("105", 22));
        json = inMemoryRepoArray.getData("105");
        assertEquals(json.toString(),
                "{\"requestData\":{\"registerId\":\"105\",\"sensorName\":\"강의실A(EM320-TH-421885)\",\"sensorType\":\"temperature\",\"unitId\":1,\"value\":22,\"devEui\":\"24e124785c421885\"}}");

        assertFalse(inMemoryRepoArray.add("102", 20));

        assertTrue(inMemoryRepoArray.add("24e124126d152969", "temperature", 20));
        json = inMemoryRepoArray.getData("24e124126d152969", "temperature");
        assertEquals(json.toString(),
                "{\"requestData\":{\"registerId\":\"107\",\"sensorName\":\"강의실A(EM500-CO2-152969)\",\"sensorType\":\"temperature\",\"unitId\":1,\"value\":20,\"devEui\":\"24e124126d152969\"}}");
        assertTrue(inMemoryRepoArray.add("24e124126d152969", "temperature", 21));
        json = inMemoryRepoArray.getData("24e124126d152969", "temperature");
        assertEquals(json.toString(),
                "{\"requestData\":{\"registerId\":\"107\",\"sensorName\":\"강의실A(EM500-CO2-152969)\",\"sensorType\":\"temperature\",\"unitId\":1,\"value\":21,\"devEui\":\"24e124126d152969\"}}");
        assertTrue(inMemoryRepoArray.add("24e124126d152969", "temperature", 22));
        json = inMemoryRepoArray.getData("24e124126d152969", "temperature");
        assertEquals(json.toString(),
                "{\"requestData\":{\"registerId\":\"107\",\"sensorName\":\"강의실A(EM500-CO2-152969)\",\"sensorType\":\"temperature\",\"unitId\":1,\"value\":22,\"devEui\":\"24e124126d152969\"}}");

        assertFalse(inMemoryRepoArray.add("24e124126d152900", "temperature", 20));
        assertFalse(inMemoryRepoArray.add("24e124126d152969", "none", 20));

    }

    @Test
    void getDataArrayTest(){
        JSONArray jsonArray = inMemoryRepoArray.getDataArray("101", 5);
        assertEquals(jsonArray.toString(), "[{\"101\":24},{\"101\":23},{\"101\":22},{\"101\":21},{\"101\":20}]");
        jsonArray = inMemoryRepoArray.getDataArray("103", 5);
        assertEquals(jsonArray.toString(), "[{\"103\":22},{\"103\":21},{\"103\":20},0,0]");
    }

    @Test
    void getValueTest(){
        assertEquals(inMemoryRepoArray.getValue("101"), 24);
        
    }
}
