package com.nhnacademy.node;

import com.nhnacademy.function.Postprocess;

public class PostprocessNode extends FunctionNode {

    public PostprocessNode(String name) {
        super(new Postprocess(), name);
    }
    
}
