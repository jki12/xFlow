package com.nhnacademy.databaserepo;

import java.util.HashMap;
import java.util.Map;

import com.nhnacademy.node.ActiveNode;

/**
 * 메모리 데이터 베이스라고 봐야할듯 하다.
 * 나중에 influxDB로 교체할 수 있어야 한다.
 * Object를 어떻게 넣어야 지금보다 깔끔하게 넣을 수 있을까?
 * 디폴트 온도는 섭씨 20도, 그 외는 0의 값을 가진다.
 * 나중에 값이 새로 들어오게 되면 새로운 값으로 갱신된다.
 * 테이블의 구조는
 * | 레지스터 주소 | 위치 | devEUI | 센서 이름(사람용) | 타입 | 값 |
 * 이렇게 구성되어 있으며
 * 해시맵을 이용하여
 * key 값은 레지스터 주소 ex)101, 102, 201, 202
 * value 값은 Object의 배열을 이용하여 정보를 넣는다.
 * value에서 온도 및 습도 등의 값은 object 배열의 [3]번 에 있다.
 * 
 */

public class DatabaseNode extends ActiveNode {

    private static Map<Integer, Object[]> database = new HashMap<>();
    private Object[] object;

    // 온도
    private static final String TEMPERATURE = "temperature";
    // 습도
    private static final String HUMIDITY = "humidity";
    // CO2
    private static final String CO2 = "co2";
    // 대기질
    private static final String TVOC = "tvoc";
    // 평균소음
    private static final String LEQ = "leq";
    // 최대소음
    private static final String LMAX = "lmax";

    /**
     * 이름을 ActiveNode에 있는 생성자에게 던져주고 메모리에 센서 정보들을 넣는다.
     * 
     * @param name
     */
    public DatabaseNode(String name) {
        super(name);

        // 온도
        object = new Object[] { "24e124128c067999", "강의실A(AM107-067999)", TEMPERATURE, 20 };
        database.put(101, object);
        object = new Object[] { "24e124785c389818", "강의실A(EM320-TH-389818)", TEMPERATURE, 20 };
        database.put(102, object);
        object = new Object[] { "24e124785c421885", "강의실A(EM320-TH-421885)", TEMPERATURE, 20 };
        database.put(103, object);
        object = new Object[] { "24e124126d152969", "강의실A(EM500-CO2-152969)", TEMPERATURE, 20 };
        database.put(104, object);
        object = new Object[] { "24e124128c140101", "강의실B(AM107-140101)", TEMPERATURE, 20 };
        database.put(105, object);
        object = new Object[] { "24e124785c389010", "로비(EM320-389010)", TEMPERATURE, 20 };
        database.put(106, object);
        object = new Object[] { "24e124136d151368", "서버실(EM300-TH-151368)", TEMPERATURE, 20 };
        database.put(107, object);
        object = new Object[] { "24e124136d151547", "창고(EM300-151547)", TEMPERATURE, 20 };
        database.put(108, object);
        object = new Object[] { "24e124126d152919", "사무실(EM500-CO2-152919)", TEMPERATURE, 20 };
        database.put(109, object);
        object = new Object[] { "24e124136d151485", "페어룸(EM300-151485)", TEMPERATURE, 20 };
        database.put(110, object);
        object = new Object[] { "24e124126c457594", "냉장고(EM500-PT100-457594)", TEMPERATURE, 20 };
        database.put(111, object);

        // 습도
        object = new Object[] { "24e124128c067999", "강의실A(AM107-067999)", HUMIDITY, 0 };
        database.put(201, object);
        object = new Object[] { "24e124785c389818", "강의실A(EM320-TH-389818)", HUMIDITY, 0 };
        database.put(202, object);
        object = new Object[] { "24e124785c421885", "강의실A(EM320-TH-421885)", HUMIDITY, 0 };
        database.put(203, object);
        object = new Object[] { "24e124126d152969", "강의실A(EM500-CO2-152969)", HUMIDITY, 0 };
        database.put(204, object);
        object = new Object[] { "24e124128c140101", "강의실B(AM107-140101)", HUMIDITY, 0 };
        database.put(205, object);
        object = new Object[] { "24e124785c389010", "로비(EM320-389010)", HUMIDITY, 0 };
        database.put(206, object);
        object = new Object[] { "24e124136d151368", "서버실(EM300-TH-151368)", HUMIDITY, 0 };
        database.put(207, object);
        object = new Object[] { "24e124136d151547", "창고(EM300-151547)", HUMIDITY, 0 };
        database.put(208, object);
        object = new Object[] { "24e124126d152919", "사무실(EM500-CO2-152919)", HUMIDITY, 0 };
        database.put(209, object);
        object = new Object[] { "24e124136d151485", "페어룸(EM300-151485)", HUMIDITY, 0 };
        database.put(210, object);

        // CO2
        object = new Object[] { "24e124128c067999", "강의실A(AM107-067999)", CO2, 400 };
        database.put(301, object);
        object = new Object[] { "24e124126d152969", "강의실A(EM500-CO2-152969)", CO2, 400 };
        database.put(302, object);
        object = new Object[] { "24e124128c140101", "강의실B(AM107-140101)", CO2, 400 };
        database.put(303, object);
        object = new Object[] { "24e124126d152919", "사무실(EM500-CO2-152919)", CO2, 400 };
        database.put(304, object);

        // 대기질(tvoc)
        object = new Object[] { "24e124128c067999", "강의실A(AM107-067999)", TVOC, 0 };
        database.put(401, object);
        object = new Object[] { "24e124128c140101", "강의실B(AM107-140101)", TVOC, 0 };
        database.put(402, object);

        // 평균소음(leq)
        object = new Object[] { "24e124743d012324", "강의실A(WS302-012324)", LEQ, 0 };
        database.put(501, object);
        object = new Object[] { "24e124743c210238", "강의실B(WS302-210238)", LEQ, 0 };
        database.put(502, object);

        // 최대소음(lmax)
        object = new Object[] { "24e124743d012324", "강의실A(WS302-012324)", LMAX, 0 };
        database.put(601, object);
        object = new Object[] { "24e124743c210238", "강의실B(WS302-210238)", LMAX, 0 };
        database.put(602, object);
    }

    /**
     * 같은 클래스에 있는 String 매개변수를 가지는 생성자에 맡기는 생성자이다.
     */
    public DatabaseNode() {
        this("database");
    }

    public int getValue(int key) {
        return (int) database.get(key)[3];
    }

    public void setValue(int key, int value) {
        database.get(key)[3] = value;
    }

    public static void main(String[] args) {
        DatabaseNode db = new DatabaseNode();
        System.out.println(db.database.get(101)[3]);
    }

	public char[] getJsonObject() {
		return null;
	}

}
