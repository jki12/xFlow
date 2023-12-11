package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import org.json.JSONObject;

import com.nhnacademy.Input;
import com.nhnacademy.Output;
import com.nhnacademy.Wire;

import lombok.Getter;

@Getter
public class FunctionNode extends ActiveNode implements Input, Output {
    // private static final String DEFAULT_LANG = "java";

    private Set<Wire> outWires = new HashSet<>();
    private Set<Wire> inWires = new HashSet<>();

    private final BiConsumer<Set<Wire>, Set<Wire>> function;
    // private final String lang;


    public FunctionNode(BiConsumer<Set<Wire>, Set<Wire>> function, String name) {
        super(name);

        this.function = function;
    }

    @Override
    public JSONObject toJson() {
        JSONObject obj = super.toJson();

        // obj.put("lang", lang);
        // obj.put("func", function.toString()); TODO add export 

        // String[] out = new String[outWires.size()];
        // String[] in = new String[inWires.size()];

        // int index = 0;
        // for (Wire wire : outWires) {
        //     out[index++] = wire.getId().toString();
        // }

        // index = 0;
        // for (Wire wire : inWires) {
        //     in[index++] = wire.getId().toString();
        // }

        // obj.put("outWires", out);
        // obj.put("inWires", in);

        return obj;
    }

    @Override
    public void wireIn(Wire wire) {
        inWires.add(wire);
    }

    @Override
    public void wireOut(Wire wire) {
        outWires.add(wire);
    }

    @Override
    public void process() {
        function.accept(inWires, outWires);
    }
}
