package com.nhnacademy.node;

import com.nhnacademy.Output;
import com.nhnacademy.Wire;

import ch.qos.logback.classic.pattern.Util;

public class ModbusMasterNode extends ActiveNode implements Output {
    private static final int MODBUS_TCP = 0x0000;

    private int address;
    private int quantity;

    


    public ModbusMasterNode(String name) {
        super(name);
    }

    @Override
    public void process() {

    }

    @Override
    public void wireOut(Wire wire) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'wireOut'");
    }

    
    
}
