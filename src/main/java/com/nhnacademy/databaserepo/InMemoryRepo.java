package com.nhnacademy.databaserepo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * InMemoryRepo
 * <p>
 * Constructor를 통해 Repo를 초기화 할 때 미리 만들어 둔 json 파일을 불러와 static 제어자를 달아 어떤 클래스에서
 * 불러와도 같은 값을 가지는 Map 파일을 불러온다.
 * <p>
 * Map 쓰는 이유는 put을 통해 같은 키를 가지는 값이 들어오면 새로 들어온 값으로 덮어씌우기
 * 때문이다.
 * 
 * <p>
 * 전체적으로 하는 일은 json 파일을 불러와 Map에 추가한 후
 * 레지스터 주소를 키 값으로,  
 * devEui, 센서이름, 타입, 값, TransactionID, UnitID를 value로
 * 저장해 두고 다른 노드들에서 데이터를 요청하는 경우 키 값을 요청하면
 * 키 값에 해당하는 key, value값을 JSONObject로 변환하여 리턴해 준다.
 */
@Slf4j
public class InMemoryRepo implements Repo {
    private static Map<String, Object> database = new HashMap<>();
    private JSONObject jsonObject;

    /**
     * JSON파일을 읽어 DB MAP에 추가하는 생성자이다.
     * 경로에 있는 JSON 파일을 읽어 Iterator를 통해 DB Map에 추가한다.
     * 
     */
    public InMemoryRepo() {
        try {
            String json = new String(
                    Files.readAllBytes(Paths.get("src/main/java/com/nhnacademy/databaserepo/modbustable.json")));
            jsonObject = new JSONObject(json);

            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                database.put(key, jsonObject.get(key));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMapValues(String key) throws NullPointerException {
        return database.get(key).toString();
    }

    /**
     * <p>
     * JSONObject를 생성한 후, 
     * jSONObject에 키 값과 키 값에 해당하는 value 값을 가져와 리턴해준다.
     * <p>
     * 해당하는 키 값이 없는 경우에는 해당하는 키를 찾을 수 없다는 에러와 함께
     * null을 리턴해 준다.
     * @param key
     * @return
     */
    public JSONObject getJsonValues(String key) {
        try {
            JSONObject json = new JSONObject();
            json.put(key, getMapValues(key));
            return json;
        } catch (NullPointerException e) {
            log.error("해당하는 키 값을 찾을 수 없습니다.");
            return null;
        }
    }
    
    /**
     * DB에 있는 값을 수정하고 싶을 때 쓰는 함수로
     * JSONObject를 받아와 키 값을 받은 다음 해당하는 키의 값을 Map의 put 함수를 통해
     * 새로운 값으로 바꿔준다.
     * @param jsonObject
     */
    public void setDB(JSONObject jsonObject) {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            database.put(key, jsonObject.get(key));
        }
    }

    public String getDataValue(String key){
        String[] tmp = database.get(key).toString().split(",");
        for(int i = 0; i < tmp.length; i++){
            tmp[i] = tmp[i].replace("{", "");
            tmp[i] = tmp[i].replace("}", "");
        }
        
        
        return "";
    }

    public static void main(String[] args) {
        InMemoryRepo inMemoryRepo = new InMemoryRepo();

        // json 테스트
        // System.out.println(databaseNodeJson.jsonObject);
        // System.out.println(database);
        // System.out.println(inMemoryRepo.getJsonValues("102"));
        String[] tmp = database.get("101").toString().split(",");
        

    }
}
