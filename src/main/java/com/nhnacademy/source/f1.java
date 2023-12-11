package com.nhnacademy.source;

import java.util.Set;
import java.util.function.BiConsumer;

import com.nhnacademy.Wire;

public class f1 implements BiConsumer<Set<Wire>, Set<Wire>> {

    @Override
    public void accept(Set<Wire> t, Set<Wire> u) {
        System.out.println("hello");
    }
}
