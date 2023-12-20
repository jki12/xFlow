// package com.nhnacademy.databaserepo;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.Map;
// import java.util.NoSuchElementException;

// import org.json.JSONObject;

// import lombok.extern.slf4j.Slf4j;

// /**
//  * InMemoryRepo
//  * <p>
//  * Constructor를 통해 Repo를 초기화 할 때 미리 만들어 둔 json 파일을 불러와 static 제어자를 달아 어떤 클래스에서
//  * 불러와도 같은 값을 가지는 Map 파일을 초기화한다.
//  * <p>
//  * Map 쓰는 이유는 put을 통해 같은 키를 가지는 값이 들어오면 새로 들어온 값으로 덮어씌우기 때문이다.
//  * <p>
//  * 전체적으로 하는 일은 json 파일을 불러와 Map에 추가한 후 레지스터 주소를 키 값으로,
//  * devEui, 센서이름, 타입, 값, TransactionID, UnitID를 value로
//  * 저장해 두고 다른 노드들에서 데이터를 요청하는 경우 키 값을 요청하면
//  * 키 값에 해당하는 key, value값을 JSONObject로 변환하여 리턴해 준다.
//  */
// @Slf4j
// public class InMemoryRepo implements Repo {

//     private static Map<String, Object> database = new HashMap<>();
//     private JSONObject jsonObject;

//     /**
//      * JSON파일을 읽어 DB MAP에 추가하는 생성자이다.
//      * <p>
//      * 경로에 있는 JSON 파일을 읽어 Iterator를 통해 DB Map에 추가한다.
//      */
//     public InMemoryRepo() {
//         try {
//             String json = new String(
//                     Files.readAllBytes(Paths.get("src/main/java/com/nhnacademy/databaserepo/modbustable.json")));
//             jsonObject = new JSONObject(json);

//             Iterator<String> keys = jsonObject.keys();

//             while (keys.hasNext()) {
//                 String key = keys.next();
//                 database.put(key, jsonObject.get(key));
//             }

//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     /**
//      * key값을 받아와 database의 key값에 해당하는 JSONObject값을 리턴하는 함수이다.
//      * 
//      * @param key key값을 받아온다.
//      * @return database의 key값에 해당하는 value값인 JSONObject를 리턴한다.
//      * @throws NullPointerException database에서 해당하는 key값을 찾을 수 없는 경우
//      *                              NullPointerException을 던진다.
//      */
//     public JSONObject getMapValues(String key) throws NullPointerException {
//         return new JSONObject(database.get(key));
//     }

//     /**
//      * <p>
//      * JSONObject를 생성한 후,
//      * jSONObject에 키 값과 키 값에 해당하는 value 값을 가져와 리턴해준다.
//      * <p>
//      * 해당하는 키 값이 없는 경우에는 해당하는 키를 찾을 수 없다는 에러와 함께
//      * null을 리턴해 준다.
//      * 
//      * @param key database의 key값을 넣는다.
//      * @return database에 있는 값을 getMapValues(key)함수를 통해 가져온 값을 JSONObject에 넣어
//      *         JSONObject를 리턴한다.
//      */
//     public JSONObject getJsonValues(String key) {
//         try {
//             JSONObject json = new JSONObject();
//             json.put(key, getMapValues(key));
//             return json;
//         } catch (NullPointerException e) {
//             log.error("해당하는 키 값을 찾을 수 없습니다.");
//             return null;
//         }
//     }

//     /**
//      * <p>
//      * DB에 있는 값을 수정하고 싶을 때 쓰는 함수이다
//      * <p>
//      * JSONObject를 받아와 키 값을 받은 다음 해당하는 키의 값을 Map의 put 함수를 통해
//      * 새로운 값으로 바꿔준다.
//      * <p>
//      * 이거는 JSONObject 형식이 완전히 지켜져서 들어와야 하기 때문에 효율성이 좋지 않다.
//      * 
//      * @deprecated 사용하지 않을거 같은 함수
//      * @param jsonObject
//      */
//     @Deprecated
//     public void setDBValue(JSONObject jsonObject) {
//         Iterator<String> keys = jsonObject.keys();
//         while (keys.hasNext()) {
//             String key = keys.next();
//             database.put(key, jsonObject.get(key));
//         }
//     }

//     /**
//      * <p>
//      * 해당하는 조건문을 중복하여 사용하기 때문에 중복되는 부분을 함수로 만들었다.
//      * 
//      * @param key
//      * @param devEui
//      * @param sensorType
//      * @return
//      */
//     public boolean equalsTest(String key, String devEui, String sensorType) {
//         return getDataValue(key, "devEui").equals(devEui) && getDataValue(key, "sensorType").equals(sensorType);
//     }

//     /**
//      * db에 있는 key값에 들어있는 값에 value JSONObject의 속성값을 찾아 해당하는 속성값의 내용을 반환한다.
//      * 
//      * @param key          키 값을 받아온다
//      * @param valueKeyName 원하는 값의 이름을 적는다.
//      * @return 원하는 이름의 값을 반환한다.
//      */

//     public String getDataValue(String key, String valueKeyName) {
//         if (database.get(key) != null) {
//             JSONObject json = getMapValues(key);
//             return String.valueOf(json.get(valueKeyName));
//         }

//         throw new NoSuchElementException();
//     }

//     /**
//      * devEui의 값과 sensorType을 주면 해당하는 값을 가진 키 값을 찾아 그 키의 속성값과 내용을 JSONObject로 반환한다.
//      * 
//      * @param devEui     devEui 값을 받아온다.
//      * @param sensorType sensorType 값을 받아온다.
//      * @return 해당하는 값을 가진 키 값의 value값을 반환한다.
//      */
//     @Override
//     public JSONObject getData(String devEui, String sensorType) {
//         // TODO Auto-generated method stub
//         Iterator<String> keys = database.keySet().iterator();
//         while (keys.hasNext()) {
//             String key = keys.next();
//             if (equalsTest(key, devEui, sensorType)) {
//                 JSONObject json = getMapValues(key);
//                 if (!json.isEmpty()) {
//                     JSONObject returnJson = new JSONObject();
//                     returnJson.put("value", json.get("value"));
//                     return json;
//                 }
//             }
//         }
//         throw new NoSuchElementException();
//     }

//     /**
//      * registerID 값을 주면 해당하는 값을 가진 키 값을 찾아 그 키의 속성값과 내용을 JSONObject로 반환한다.
//      * 
//      * @param registerId registerId값을 받아온다.
//      */
//     @Override
//     public JSONObject getData(String registerId) {
//         // TODO Auto-generated method stub
//         JSONObject json = getMapValues(registerId);
//         JSONObject returnJson = new JSONObject();
//         returnJson.put("value", json.get("value"));
//         return returnJson;
//     }

//     /**
//      * <p>
//      * 이 함수는 devEui와 sensorType을 가지고 있는 key 값을 찾아
//      * key값에 해당하는 value값을 지정한 값으로 바꾸어 database에 새로 저장하는 함수이다.
//      * <p>
//      * 지정한 값으로 바꾸는데 성공하면 True, 해당하는 키 값을 찾을 수 없거나 바꾸는데 실패하면 False를 반환한다.
//      * <p>
//      * JSONObject를 받아와야 바꿀 수 있는 것이 아니라 특정 값을 통해서 value 값을 바꿀 수 있어 효율성이 있을거라 사료됨
//      * 
//      * @param devEui     devEui 값을 받아온다.
//      * @param sensorType sensorType 값을 받아온다.
//      * @param value      바꾸고 싶은 value값을 받아온다.
//      * @return 값을 바꾸는데 성공하면 True, 키 값이 없거나 바꾸는데 실패시 False를 반환한다.
//      */
//     @Override
//     public boolean setData(String devEui, String sensorType, double value) {
//         Iterator<String> keys = database.keySet().iterator();
//         while (keys.hasNext()) {
//             String key = keys.next();
//             if (equalsTest(key, devEui, sensorType)) {
//                 JSONObject outJson = new JSONObject();
//                 JSONObject inJson = getMapValues(key);
//                 if (!inJson.isEmpty()) {
//                     inJson.put("value", value);
//                     outJson.put(key, inJson);
//                     database.put(key, outJson.get(key));
//                     return true;
//                 }
//             }
//         }
//         return false;
//     }

//     /**
//      * <p>
//      * 이 함수는 registerId에 해당하는 value값을 지정한 값으로 바꾸어 database에 새로 저장하는 함수이다.
//      * <p>
//      * 지정한 값으로 바꾸는데 성공하면 True, 해당하는 키 값을 찾을 수 없거나 바꾸는데 실패하면 False를 반환한다.
//      * 
//      * @param registerId registerId를 받아온다.
//      * @param value      바꾸고 싶은 value값을 받아온다.
//      */
//     @Override
//     public boolean setData(String registerId, double value) {
//         JSONObject outJson = new JSONObject();
//         JSONObject inJson = getMapValues(registerId);
//         if (!inJson.isEmpty()) {
//             inJson.put("value", value);
//             outJson.put(registerId, inJson);
//             database.put(registerId, outJson.get(registerId));
//             return true;
//         }

//         return false;
//     }

//     public static void main(String[] args) {
//         InMemoryRepo inMemoryRepo = new InMemoryRepo();

//         // json 테스트
//         // System.out.println(databaseNodeJson.jsonObject);
//         // System.out.println(database);
//         // System.out.println(inMemoryRepo.getJsonValues("101"));
//         // System.out.println(inMemoryRepo.getDataValue("101", "devEui"));
//         // System.out.println(inMemoryRepo.getDataValue("101", "sensorType"));
//         // System.out.println(inMemoryRepo.getJsonValues("101"));
//         // System.out.println(database.get("101"));
//         // inMemoryRepo.setDBValue("24e124128c067999", "temperature", 25);
//         // System.out.println(inMemoryRepo.getJsonValues("101"));
//         // System.out.println(database.get("101"));
//         // System.out.println(inMemoryRepo.getValue("24e124128c067999", "temperature"));
//     }
// }
