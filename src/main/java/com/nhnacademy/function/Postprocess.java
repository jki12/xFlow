package com.nhnacademy.function;

import com.nhnacademy.Wire;
import com.nhnacademy.exception.UnsupportedDataTypeException;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import org.json.JSONObject;

import com.nhnacademy.Config;

import lombok.extern.slf4j.Slf4j;

/**
 * 전처리 한 데이터를 가지고, 후처리를 하는 class입니다.
 * <p>
 * mqtt in =wire= 전처리 =wire= 후처리 =wire= mqtt out.
 * <p>
 * 
 * <p>
 * 센서 데이터가 여러개일 경우에 대한 대처. = map
 * <p>
 * value는 double값으로 가정. 타입 캐스팅 전 undefined는 걸러냄.
 * <p>
 * 
 * 데이터는 센서별로 분리.
 * 센서 정보에는 지사, 위치, 장비 식별 번호, 시간 정보, 센서 값이 포함이 되어야 합니다.
 * <p>
 * 예를 들면, temperature라는 센서 내부에는, 이 센서가 들어온 정보.
 * <p>
 * 즉, 위의 5가지의 값이 들어있어야 합니다.
 * + topic 수정까지.
 */
@Slf4j
public class Postprocess implements BiConsumer<Set<Wire>, Set<Wire>> {
    private boolean isAllSensorDataReceived = false;
    private Set<String> required;

    private void recursive(JSONObject out, JSONObject data) {
        Set<String> keys = data.keySet();

        for (String key : keys) {
            if (required.contains(key) || (isAllSensorDataReceived && key.equals("object"))) { // 모든 센서를 허용하는 경우 (object 값에 센서 데이터가 전부 들어 있음)
                out.put(key, data.get(key));
            }
            else if (data.get(key) instanceof JSONObject) {
                JSONObject temp = new JSONObject();
                recursive(temp, data.getJSONObject(key));

                if (!temp.isEmpty()) {
                    out.put(key, temp);
                }
            }
        }
    }

    private void init() {
        if (required == null) {
            required = new HashSet<>();

            if (Config.getCurrentConfig().get("s").equals("all")) {
                isAllSensorDataReceived = true;
            }

            if (!isAllSensorDataReceived) {
                String[] allowedSeneor = Config.getCurrentConfig().getString("s").split(",");

                required.addAll(Arrays.asList(allowedSeneor));
            }

            String[] requiredList = Config.getCurrentConfig().getString("required").split(",");

            required.addAll(Arrays.asList(requiredList));
        }
    }

    @Override
    public void accept(Set<Wire> inWires, Set<Wire> outWires) {
        init();

        for (Wire wire : inWires) {
            var messageQ = wire.getMessageQue();

            if (!messageQ.isEmpty()) {
                Message msg = messageQ.poll();
                if (!(msg instanceof JsonMessage)) throw new UnsupportedDataTypeException();

                JSONObject content = ((JsonMessage) msg).getContent();

                JSONObject result = new JSONObject();
                JSONObject parsedData = new JSONObject();
                recursive(parsedData, content.getJSONObject("payload"));

                result.put("topic", content.get("topic"));
                result.put("payload", parsedData);

                for (Wire outWire : outWires) {
                    outWire.getMessageQue().add(new JsonMessage(result));
                }
            }
        }
    }
}
