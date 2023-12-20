package com.nhnacademy.databaserepo;

import java.util.NoSuchElementException;
import java.util.UUID;

public class InMemoryArray {
    // UUID number;
    String registerId;
    String devEui;
    String sensorName;
    String sensorType;
    double value;
    int unitId;

    public InMemoryArray(String registerId, double value) {

        String TEMPERATURE = "temperature";
        String HUMIDITY = "humidity";
        switch (registerId) {
            case "0":

                this.registerId = registerId;
                this.devEui = "24e124128c067999";
                this.sensorName = "강의실A(AM107-067999)";
                this.sensorType = TEMPERATURE;
                this.value = value;
                this.unitId = 1;
                break;
            case "103":

                this.registerId = registerId;
                this.devEui = "24e124785c389818";
                this.sensorName = "강의실A(EM320-TH-389818)";
                this.sensorType = TEMPERATURE;
                this.value = value;
                this.unitId = 1;
                break;
            case "105":

                this.registerId = registerId;
                this.devEui = "24e124785c421885";
                this.sensorName = "강의실A(EM320-TH-421885)";
                this.sensorType = TEMPERATURE;
                this.value = value;
                this.unitId = 1;
                break;
            case "107":

                this.registerId = registerId;
                this.devEui = "24e124126d152969";
                this.sensorName = "강의실A(EM500-CO2-152969)";
                this.sensorType = TEMPERATURE;
                this.value = value;
                this.unitId = 1;
                break;
            case "109":

                this.registerId = registerId;
                this.devEui = "24e124128c140101";
                this.sensorName = "강의실B(AM107-140101)";
                this.sensorType = TEMPERATURE;
                this.value = value;
                this.unitId = 1;
                break;
            case "111":

                this.registerId = registerId;
                this.devEui = "24e124785c389010";
                this.sensorName = "로비(EM320-389010)";
                this.sensorType = TEMPERATURE;
                this.value = value;
                this.unitId = 1;
                break;
            case "113":

                this.registerId = registerId;
                this.devEui = "24e124136d151368";
                this.sensorName = "서버실(EM300-TH-151368)";
                this.sensorType = TEMPERATURE;
                this.value = value;
                this.unitId = 1;
                break;
            case "115":

                this.registerId = registerId;
                this.devEui = "24e124136d151547";
                this.sensorName = "창고(EM300-151547)";
                this.sensorType = TEMPERATURE;
                this.value = value;
                this.unitId = 1;
                break;
            case "117":

                this.registerId = registerId;
                this.devEui = "24e124126d152919";
                this.sensorName = "사무실(EM500-CO2-152919)";
                this.sensorType = TEMPERATURE;
                this.value = value;
                this.unitId = 1;
                break;
            case "119":

                this.registerId = registerId;
                this.devEui = "24e124136d151485";
                this.sensorName = "페어룸(EM300-151485)";
                this.sensorType = TEMPERATURE;
                this.value = value;
                this.unitId = 1;
                break;
            case "121":

                this.registerId = registerId;
                this.devEui = "24e124126c457594";
                this.sensorName = "냉장고(EM500-PT100-457594)";
                this.sensorType = TEMPERATURE;
                this.value = value;
                this.unitId = 1;
                break;

            case "201":

                this.registerId = registerId;
                this.devEui = "24e124128c067999";
                this.sensorName = "강의실A(AM107-067999)";
                this.sensorType = HUMIDITY;
                this.value = value;
                this.unitId = 1;
                break;
            case "203":

                this.registerId = registerId;
                this.devEui = "24e124785c389818";
                this.sensorName = "강의실A(EM320-TH-389818)";
                this.sensorType = HUMIDITY;
                this.value = value;
                this.unitId = 1;
                break;
            case "205":

                this.registerId = registerId;
                this.devEui = "24e124785c421885";
                this.sensorName = "강의실A(EM320-TH-421885)";
                this.sensorType = HUMIDITY;
                this.value = value;
                this.unitId = 1;
                break;
            case "207":

                this.registerId = registerId;
                this.devEui = "24e124126d152969";
                this.sensorName = "강의실A(EM500-CO2-152969)";
                this.sensorType = HUMIDITY;
                this.value = value;
                this.unitId = 1;
                break;
            case "209":

                this.registerId = registerId;
                this.devEui = "24e124128c140101";
                this.sensorName = "강의실B(AM107-140101)";
                this.sensorType = HUMIDITY;
                this.value = value;
                this.unitId = 1;
                break;
            case "211":

                this.registerId = registerId;
                this.devEui = "24e124785c389010";
                this.sensorName = "로비(EM320-389010)";
                this.sensorType = HUMIDITY;
                this.value = value;
                this.unitId = 1;
                break;
            case "213":

                this.registerId = registerId;
                this.devEui = "24e124136d151368";
                this.sensorName = "서버실(EM300-TH-151368)";
                this.sensorType = HUMIDITY;
                this.value = value;
                this.unitId = 1;
                break;
            case "215":

                this.registerId = registerId;
                this.devEui = "24e124136d151547";
                this.sensorName = "창고(EM300-151547)";
                this.sensorType = HUMIDITY;
                this.value = value;
                this.unitId = 1;
                break;
            case "217":

                this.registerId = registerId;
                this.devEui = "24e124126d152919";
                this.sensorName = "사무실(EM500-CO2-152919)";
                this.sensorType = HUMIDITY;
                this.value = value;
                this.unitId = 1;
                break;
            case "219":

                this.registerId = registerId;
                this.devEui = "24e124136d151485";
                this.sensorName = "페어룸(EM300-151485)";
                this.sensorType = HUMIDITY;
                this.value = value;
                this.unitId = 1;
                break;

            case "301":

                this.registerId = registerId;
                this.devEui = "24e124128c067999";
                this.sensorName = "강의실A(AM107-067999)";
                this.sensorType = "co2";
                this.value = value;
                this.unitId = 1;
                break;
            case "303":

                this.registerId = registerId;
                this.devEui = "24e124126d152969";
                this.sensorName = "강의실A(EM500-CO2-152969)";
                this.sensorType = "co2";
                this.value = value;
                this.unitId = 1;
                break;
            case "305":

                this.registerId = registerId;
                this.devEui = "24e124128c140101";
                this.sensorName = "강의실B(AM107-140101)";
                this.sensorType = "co2";
                this.value = value;
                this.unitId = 1;
                break;
            case "307":

                this.registerId = registerId;
                this.devEui = "24e124126d152919";
                this.sensorName = "사무실(EM500-CO2-152919)";
                this.sensorType = "co2";
                this.value = value;
                this.unitId = 1;
                break;

            case "401":

                this.registerId = registerId;
                this.devEui = "24e124128c067999";
                this.sensorName = "강의실A(AM107-067999)";
                this.sensorType = "tvoc";
                this.value = value;
                this.unitId = 1;
                break;
            case "403":

                this.registerId = registerId;
                this.devEui = "24e124128c140101";
                this.sensorName = "강의실B(AM107-140101)";
                this.sensorType = "tvoc";
                this.value = value;
                this.unitId = 1;
                break;

            case "501":

                this.registerId = registerId;
                this.devEui = "24e124743d012324";
                this.sensorName = "강의실A(WS302-012324)";
                this.sensorType = "leq";
                this.value = value;
                this.unitId = 1;
                break;
            case "503":

                this.registerId = registerId;
                this.devEui = "24e124743c210238";
                this.sensorName = "강의실B(WS302-210238)";
                this.sensorType = "leq";
                this.value = value;
                this.unitId = 1;
                break;

            case "601":

                this.registerId = registerId;
                this.devEui = "24e124743d012324";
                this.sensorName = "강의실A(WS302-012324)";
                this.sensorType = "lmax";
                this.value = value;
                this.unitId = 1;
                break;
            case "603":

                this.registerId = registerId;
                this.devEui = "24e124743c210238";
                this.sensorName = "강의실B(WS302-210238)";
                this.sensorType = "lmax";
                this.value = value;
                this.unitId = 1;
                break;
            default:
                throw new NoSuchElementException();
        }
    }

    public InMemoryArray(String devEui, String sensorType, double value) {
        switch (devEui) {
            case "24e124128c067999":// 강의실A(AM107-067999)
                switch (sensorType) {
                    case "temperature":

                        this.registerId = "101";
                        this.devEui = devEui;
                        this.sensorName = "강의실A(AM107-067999)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "humidity":

                        this.registerId = "201";
                        this.devEui = devEui;
                        this.sensorName = "강의실A(AM107-067999)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "co2":

                        this.registerId = "301";
                        this.devEui = devEui;
                        this.sensorName = "강의실A(AM107-067999)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "tvoc":

                        this.registerId = "401";
                        this.devEui = devEui;
                        this.sensorName = "강의실A(AM107-067999)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    default:
                        throw new NoSuchElementException();
                }
                break;
            case "24e124785c389818":// 강의실(EM320-TH-389818)
                switch (sensorType) {
                    case "temperature":

                        this.registerId = "103";
                        this.devEui = devEui;
                        this.sensorName = "강의실A(EM320-TH-389818)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "humidity":

                        this.registerId = "203";
                        this.devEui = devEui;
                        this.sensorName = "강의실A(EM320-TH-389818)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    default:
                        throw new NoSuchElementException();
                }
                break;
            case "24e124785c421885":// 강의실A(EM320-TH-421885)
                switch (sensorType) {
                    case "temperature":

                        this.registerId = "105";
                        this.devEui = devEui;
                        this.sensorName = "강의실A(EM320-TH-421885)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "humidity":

                        this.registerId = "205";
                        this.devEui = devEui;
                        this.sensorName = "강의실A(EM320-TH-421885)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    default:
                        throw new NoSuchElementException();
                }
                break;
            case "24e124126d152969":// 강의실A(EM500-CO2-152969)
                switch (sensorType) {
                    case "temperature":

                        this.registerId = "107";
                        this.devEui = devEui;
                        this.sensorName = "강의실A(EM500-CO2-152969)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "humidity":

                        this.registerId = "207";
                        this.devEui = devEui;
                        this.sensorName = "강의실A(EM500-CO2-152969)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "co2":

                        this.registerId = "303";
                        this.devEui = devEui;
                        this.sensorName = "강의실A(EM500-CO2-152969)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    default:
                        throw new NoSuchElementException();
                }
                break;
            case "24e124128c140101":// 강의실B(AM107-140101)
                switch (sensorType) {
                    case "temperature":

                        this.registerId = "109";
                        this.devEui = devEui;
                        this.sensorName = "강의실B(AM107-140101)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "humidity":

                        this.registerId = "209";
                        this.devEui = devEui;
                        this.sensorName = "강의실B(AM107-140101)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "co2":

                        this.registerId = "305";
                        this.devEui = devEui;
                        this.sensorName = "강의실B(AM107-140101)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "tvoc":

                        this.registerId = "403";
                        this.devEui = devEui;
                        this.sensorName = "강의실B(AM107-140101)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    default:
                        throw new NoSuchElementException();
                }
                break;
            case "4e124785c389010":// 로비(EM320-389010)
                switch (sensorType) {
                    case "temperature":

                        this.registerId = "111";
                        this.devEui = devEui;
                        this.sensorName = "로비(EM320-389010)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "humidity":

                        this.registerId = "211";
                        this.devEui = devEui;
                        this.sensorName = "로비(EM320-389010)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    default:
                        throw new NoSuchElementException();
                }
                break;
            case "24e124136d151368":// 서버실(EM300-TH-151368)
                switch (sensorType) {
                    case "temperature":

                        this.registerId = "113";
                        this.devEui = devEui;
                        this.sensorName = "서버실(EM300-TH-151368)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "humidity":

                        this.registerId = "213";
                        this.devEui = devEui;
                        this.sensorName = "서버실(EM300-TH-151368)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    default:
                        throw new NoSuchElementException();
                }
                break;
            case "24e124136d151547":// 창고(EM300-151547)
                switch (sensorType) {
                    case "temperature":

                        this.registerId = "115";
                        this.devEui = devEui;
                        this.sensorName = "창고(EM300-151547)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "humidity":

                        this.registerId = "215";
                        this.devEui = devEui;
                        this.sensorName = "창고(EM300-151547)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    default:
                        throw new NoSuchElementException();
                }
                break;
            case "24e124126d152919":// 사무실(EM500-CO2-152919)
                switch (sensorType) {
                    case "temperature":

                        this.registerId = "117";
                        this.devEui = devEui;
                        this.sensorName = "사무실(EM500-CO2-152919)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "humidity":

                        this.registerId = "217";
                        this.devEui = devEui;
                        this.sensorName = "사무실(EM500-CO2-152919)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "co2":

                        this.registerId = "307";
                        this.devEui = devEui;
                        this.sensorName = "사무실(EM500-CO2-152919)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    default:
                        throw new NoSuchElementException();
                }
                break;
            case "24e124136d151485":// 페어룸(EM300-151485)
                switch (sensorType) {
                    case "temperature":

                        this.registerId = "119";
                        this.devEui = devEui;
                        this.sensorName = "페어룸(EM300-151485)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "humidity":

                        this.registerId = "219";
                        this.devEui = devEui;
                        this.sensorName = "페어룸(EM300-151485)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    default:
                        throw new NoSuchElementException();
                }
                break;
            case "24e124126c457594":// 냉장고(EM500-PT100-457594)
                if (sensorType.equals("temperature")) {

                    this.registerId = "121";
                    this.devEui = devEui;
                    this.sensorName = "냉장고(EM500-PT100-457594)";
                    this.sensorType = sensorType;
                    this.value = value;
                    this.unitId = 1;
                    break;
                }
                throw new NoSuchElementException();
            case "24e124743d012324":// 강의실A(WS302-012324)
                switch (sensorType) {
                    case "leq":

                        this.registerId = "501";
                        this.devEui = devEui;
                        this.sensorName = "강의실A(AM107-067999)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "lmax":

                        this.registerId = "601";
                        this.devEui = devEui;
                        this.sensorName = "강의실A(AM107-067999)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    default:
                        throw new NoSuchElementException();
                }
                break;
            case "24e124743c210238":// 강의실B(WS302-210238)
                switch (sensorType) {
                    case "leq":

                        this.registerId = "503";
                        this.devEui = devEui;
                        this.sensorName = "강의실B(AM107-140101)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    case "lmax":

                        this.registerId = "603";
                        this.devEui = devEui;
                        this.sensorName = "강의실B(AM107-140101)";
                        this.sensorType = sensorType;
                        this.value = value;
                        this.unitId = 1;
                        break;
                    default:
                        throw new NoSuchElementException();
                }
                break;
            default:
                throw new NoSuchElementException();
        }
    }
}
