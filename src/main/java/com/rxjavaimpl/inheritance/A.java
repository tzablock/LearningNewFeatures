package com.rxjavaimpl.inheritance;

public abstract class A {
    public String createString(){
        return firstPart() + "A";
    }

    protected abstract String firstPart();
}
