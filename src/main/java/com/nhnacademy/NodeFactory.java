package com.nhnacademy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.tools.JavaCompiler;
import javax.tools.Tool;
import javax.tools.ToolProvider;

import org.json.JSONObject;

import com.nhnacademy.function.Preprocess;
import com.nhnacademy.node.*;

import lombok.extern.slf4j.Slf4j;
import pl.joegreen.lambdaFromString.LambdaFactory;
import pl.joegreen.lambdaFromString.TypeReference;

@Slf4j
public final class NodeFactory {
    private static final String NODE_DIR_PATH = "com.nhnacademy.node.";
    private static final String DIR_PATH = "./src/main/java/com/nhnacademy/source"; // 설정 파일을 읽고 생성된 java 파일, compile 파일들이 저장되는 폴더 이름.
    private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private final String template;

    public NodeFactory() throws IOException {
        // read template
        this.template = new String(Files.readAllBytes(Paths.get("./src/resources/funcFormat.txt")));
    }

    /*
     * 만약 디렉토리 파일일 경우 내부의 파일들도 전부 삭제합니다.
     */
    private void delete(File file) {
        if (!file.exists()) return;

        if (file.isDirectory()) {
            var fileList = file.listFiles();
            
            for (int i = 0; i < fileList.length; ++i) {
                delete(fileList[i]);
            }
        }

        file.delete();
    }

    private File createSourceFile(String fileName, String content) throws IOException {
        File file = new File(String.format("%s/%s",DIR_PATH, fileName));
        
        try (FileOutputStream fs = new FileOutputStream(file);) {
            
            fs.write(content.getBytes());
            fs.flush();

        } catch (Exception e) {
            // TODO: handle exception
        }

        return file;
    }

    public Node createNode(JSONObject json) {
        String name = json.getString("name");

        try {
            // init : create dir

            File dir = new File(DIR_PATH);

            delete(dir);
            dir.mkdir();


            Class<?> clazz = Class.forName(NODE_DIR_PATH + json.getString("type"));
            Constructor<?> ctor;

            switch (clazz.getSimpleName()) {

                case "MqttInNode":
                    ctor = clazz.getConstructor(String.class, String.class);
                    return (Node) ctor.newInstance(json.getString("uri"), name);
                    
                case "MqttOutNode":
                    ctor = clazz.getConstructor(String.class, String.class);
                    return (Node) ctor.newInstance(json.getString("uri"));

                case "FunctionNode":
                    String a = String.format(template, name, json.get("func"));

                    File sourceFile = createSourceFile(name + ".java", a);
                    int exitCode = compiler.run(null, null, null, sourceFile.getPath());

                    if (exitCode != 0) throw new RuntimeException();

                    // ClassLoader loader = new 

                    URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { sourceFile.toURI().toURL() } );
                    Class<?> cls = Class.forName("com.nhnacademy.source" + "." + name, true, classLoader); // Should print "hello".
                    var ctor1 = cls.getConstructor();
                    BiConsumer<Set<Wire>, Set<Wire>> instance = (BiConsumer<Set<Wire>, Set<Wire>>) ctor1.newInstance(); // Should print "world".

                    
                    instance.accept(null, null);

                    ctor = clazz.getConstructor(BiConsumer.class, String.class);
                    FunctionNode as = (FunctionNode) ctor.newInstance(instance , "");
                    // ctor.
                    return as;



                    

                    // var ctor2 = clazz.getConstructor(BiConsumer.class, String.class);

                    // return (Node) ctor2.newInstance(BiConsumer.class, String.class, null, json.getString("uri"));
            
                default:
                return null;
                    
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        throw new IllegalStateException();
    }

}
