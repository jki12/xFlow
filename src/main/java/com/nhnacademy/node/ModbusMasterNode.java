package com.nhnacademy.node;

import com.nhnacademy.Output;
import com.nhnacademy.Wire;
import com.nhnacademy.info.Info;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.util.Util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

@Slf4j
@Getter
public class ModbusMasterNode extends ActiveNode implements Output {
    private static final int DEFUALT_INTERVAL = 1_000;
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final short DEFAULT_ADDRESS = 0;
    private static final short DEFAULT_QUANTITY = 2;
    private static final byte DEFAULT_FUNCTION_CODE = 3;

    private final Set<Wire> outWires = new HashSet<>();
    private final Info info = new Info();
    private String host;
    private int port;
    private short transactionId;
    private byte[] address;
    private byte[] quantity;
    private byte functionCode;

    public ModbusMasterNode(String name, String host, int port) {
       this(name, host, port, DEFAULT_FUNCTION_CODE, DEFAULT_ADDRESS, DEFAULT_QUANTITY);
    }

    public ModbusMasterNode(String name, String host, int port, byte functionCode, short address, short quantity) {
        super(name);

        this.host = host;
        this.port = port;

        this.address = Util.toByteArray(address);
        this.quantity = Util.toByteArray(quantity);
        this.functionCode = functionCode;
    }

    /*
     * 특정 시간마다 계속 slave에게 값을 요청하는 thread를 실행시킨다.
     */
    @Override
    public void preprocess() {
        Thread thread = new Thread(() -> {
            Socket client = null;

            try {
                client = new Socket(host, port);

            } catch (Exception ignore) {
                log.error(ignore.getMessage());

                Thread.interrupted();
            }

            while (!Thread.interrupted()) {
                try {
                    // header 부분
                    byte[] pdu = new byte[] { functionCode, address[0], address[1], quantity[0], quantity[1] };
                    byte[] mbap = Util.makeMBAP(++transactionId, (short) (pdu.length + 1), (byte) 1);
                    
                    client.getOutputStream().write(Util.concat(mbap, pdu));
                    client.getOutputStream().flush();
                    
                    byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
                    int len = client.getInputStream().read(bytes);

                    info.increaseReceiveCount();
                    // log.debug("{}", Arrays.toString(Arrays.copyOfRange(bytes, 0, len)));

                    JSONObject content = Util.toJson(bytes, len);
                    content.put("registerAddress", Util.toShort(address[0], address[1]));

                    Message msg = new JsonMessage(content);
                    for (var wire : outWires) {
                        wire.getMessageQue().add(msg);
                        
                        info.increaseSendCount();
                    }

                    Thread.sleep(DEFUALT_INTERVAL);
                    
                } catch (Exception e) {
                    info.increaseAbnormalCount();

                    log.warn(e.getMessage());
                }
            }
        });

        thread.start();
    }

    @Override
    public void wireOut(Wire wire) {
        outWires.add(wire);
    }
}
