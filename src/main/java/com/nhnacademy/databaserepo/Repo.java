package com.nhnacademy.databaserepo;

import org.json.JSONObject;

public interface Repo {
    public boolean setData(String devEui, String sensorType, double value);

    public boolean setData(String registerId, double value);

    public JSONObject getData(String devEui, String sensorType);

    public JSONObject getData(String registerId);
}
