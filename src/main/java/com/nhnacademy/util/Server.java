// package com.nhnacademy.util;

// import java.io.BufferedInputStream;
// import java.lang.reflect.Array;
// import java.net.ServerSocket;
// import java.net.Socket;
// import java.nio.Buffer;
// import java.util.Arrays;

// import org.json.JSONObject;

// public class Server {
//     private static final int BUFFER_SIZE = 1024;
//     private static final int MODBUS_TCP = 0;
//     private static final int READ_INPUT_REGISTER = 0x04;
//     private static final int READ_HOLDING_REGISTERS = 0x03;
//     private static final int WRITE_SINGLE_REGISTER = 0x06;
//     private static final int WRITE_MULTIPLE_REGISTERS = 0x10;

//     private static boolean isValidProtocolId(int protocolId) {
//         return (protocolId == MODBUS_TCP);
//     }

//     private static boolean isValidFunctionCode(int code) {
//         return (code == READ_INPUT_REGISTER || code == READ_HOLDING_REGISTERS || code == WRITE_SINGLE_REGISTER || code == WRITE_MULTIPLE_REGISTERS);
//     }

//     private static short toLittleEndian(short a) { // 0x1234 -> 0x 3412
//         return (short) ((a << 8) | (a >> 8));
//     }

//     private byte[] make() {


//         (byte) a;

//     }

//     /*
//      * format = 00 00 00 1 3
//      */
//     public static JSONObject toJson(byte[] header) { // modbus header to json, include functionCode.
//         if (header.length != 8) throw new IllegalArgumentException();

//         String[] a = new String[] { "transactionId", "protocolId", "length", "unitId", "functionCode" };
//         JSONObject obj = new JSONObject();

//         int index = 0; // 2 bytes
//         for (int i = 0; i < a.length; ++i) {

//             if (i <= 2) {
//                 obj.put(a[i], ((header[index + 1] << 8) | header[index]));
//                 index += 2;

//             } else {
//                 obj.put(a[i], header[index]);
//                 index++;
//             }
//         }

//         if (isValidProtocolId(obj.getInt(a[1]))) throw new RuntimeException();

//         if (isValidFunctionCode(obj.getInt(a[4]))) throw new RuntimeException();

//         return obj;
//     }

//     public static void main(String[] args) throws Exception {
//         try (ServerSocket server = new ServerSocket();) {

//             Socket client = server.accept();

//             byte[] buffer = new byte[BUFFER_SIZE];
//             BufferedInputStream is = new BufferedInputStream(client.getInputStream());

//             // int len = is.read(buffer, 0, buffer.length);

//             int len = is.read(buffer, 0, buffer.length);

            
//             JSONObject request = toJson(buffer); // read only header;

//             // request.getString();

//             int code = request.getInt("functionCode");

//             switch (code) {
//                 case 3:
                    
//                     break;
            
//                 default:
//                     break;
//             }

//             client.close();
            
//         } catch (Exception e) {
//             // TODO: handle exception
//         }
//         // long a = 0x1234567812345678L;
//         long a = 0x00000003L;


//         short b = 0x1234;
//         // short c = toLittleEndian(b);

//         toJson(new byte[] { 0, 0, 0, 0, 0, 0, 0, 3 });

//     }
    
// }
