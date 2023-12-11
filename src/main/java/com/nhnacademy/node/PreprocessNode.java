package com.nhnacademy.node;

import com.nhnacademy.function.Preprocess;

public class PreprocessNode extends FunctionNode {

    public PreprocessNode(String name) {
        super(new Preprocess(), name);
    }
}
