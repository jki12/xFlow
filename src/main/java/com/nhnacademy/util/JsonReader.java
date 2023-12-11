// package com.nhnacademy.util;

// import org.json.JSONObject;

// import ch.qos.logback.core.pattern.parser.Node;

// import java.util.ArrayList;
// import java.util.List;

// import org.graalvm.polyglot.Context;
// import org.graalvm.polyglot.Engine;
// import org.json.JSONArray;

// public class JsonReader {
//     private 
//     // 테스트 하기 편하도록 임시로 만든 변수
//     private Context context;
//     private List<Node> list = new ArrayList<>();

//     public JsonReader() {
//         try {
//             Engine engine = Engine.newBuilder()
//             .option("engine.WarnInterpreterOnly", "false")
//             .build();

//             context = Context.newBuilder("js").engine(engine).build();

//         } catch (Exception e) {
//         }
//     }

//     public void readAndRun(JSONObject obj) { // import만 하도록
//         if (obj.get("type").equals("function")) {

//             String source = obj.getString("func");

//             // context.eval(obj.)



//         }

//         // if () {

//         // }


//     }
// }
