package com.nhnacademy.databaserepo;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * InMemoryRepoArray
 * <p>
 * Repo Interface를 상속받아와서 구현한 것으로 들어온 자료를 database에 넣는다.
 */
public class InMemoryRepoArray implements Repo {
    static List<InMemoryArray> database = new ArrayList<>();

    /**
     * <p>
     * 이 함수는 devEui와 sensorType을 이용하여 InMemoryArray에 여러 데이터와 value 값을 자동으로
     * 넣어주는 함수이다.
     * 
     */
    @Override
    public boolean addData(String devEui, String sensorType, double value) {
        try {
            return database.add(new InMemoryArray(devEui, sensorType, value));
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * <p>
     * 이 함수는 registerId를 이용하여 InMemoryArray에 여러 데이터와 value 값을 자동으로
     * 넣어주는 함수이다.
     * 
     */
    @Override
    public boolean addData(String registerId, double value) {
        try {
            return database.add(new InMemoryArray(registerId, value));
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * <p>
     * 이 함수는 데이터 베이스에 들어있는 값들 중 가장 최신값에 해당하는 자료들의 정보를 jSONObject로 보내주는 함수이다.
     * <p>
     * devEUI와 sensorType을 통해 InMemoryArray에서 해당하는 값과 일치하는 가장 최신값을 JSONObjec로 보낸다.
     */
    @Override
    public JSONObject getData(String devEui, String sensorType) {
        for (int i = database.size() - 1; i >= 0; i--) {
            if (database.get(i).devEui.equals(devEui) && database.get(i).sensorType.equals(sensorType)) {
                JSONObject inJson = new JSONObject();
                JSONObject outJson = new JSONObject();
                // inJson.put("number", database.get(i).number);
                inJson.put("registerId", database.get(i).registerId);
                inJson.put("devEui", database.get(i).devEui);
                inJson.put("sensorName", database.get(i).sensorName);
                inJson.put("sensorType", database.get(i).sensorType);
                inJson.put("value", database.get(i).value);
                inJson.put("unitId", database.get(i).unitId);

                outJson.put("requestData", inJson);
                return outJson;
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * <p>
     * 이 함수는 데이터 베이스에 들어있는 값들 중 가장 최신값에 해당하는 자료들의 정보를 jSONObject로 보내주는 함수이다.
     * <p>
     * registerId를 통해 InMemoryArray에서 해당하는 값과 일치하는 가장 최신값을 JSONObjec로 보낸다.
     */
    @Override
    public JSONObject getData(String registerId) {
        for (int i = database.size() - 1; i >= 0; i--) {
            if (database.get(i).registerId.equals(registerId)) {
                JSONObject inJson = new JSONObject();
                JSONObject outJson = new JSONObject();
                // inJson.put("number", database.get(i).number);
                inJson.put("registerId", database.get(i).registerId);
                inJson.put("devEui", database.get(i).devEui);
                inJson.put("sensorName", database.get(i).sensorName);
                inJson.put("sensorType", database.get(i).sensorType);
                inJson.put("value", database.get(i).value);
                inJson.put("unitId", database.get(i).unitId);

                outJson.put("requestData", inJson);
                return outJson;
            }
        }
        throw new NoSuchElementException();
    }

    public JSONArray getDataArray(String registerId, int quantity) {
        int count = 0;
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = database.size() - 1; i >= 0; i--) {
                if (database.get(i).registerId.equals(registerId)) {
                    JSONObject json = new JSONObject();
                    json.put(registerId, database.get(i).value);
                    jsonArray.put(json);
                    if (count == quantity) {
                        break;
                    }
                    count++;
                }
            }
            if(count != 0 && count <quantity){
                for(int i = count ; i < quantity; i++){
                    jsonArray.put(0);
                }

            }
            return jsonArray;
        } catch (RuntimeException e) {
            e.getMessage();
            return null;
        }
    }

    public double getValue(String registerId) {
        for (int i = database.size() - 1; i > 0; i--) {
            if (database.get(i).registerId.equals(registerId)) {
                return database.get(i).value;
            }
        }
        return 0.0;
    }

}
