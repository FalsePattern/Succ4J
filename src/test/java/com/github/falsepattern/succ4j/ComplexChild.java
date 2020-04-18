package com.github.falsepattern.succ4j;

import com.github.falsepattern.succ4j.parsinglogic.DoSave;

public class ComplexChild extends ComplexType{
    @DoSave
    private int childValue;

    // parameter-less constructor required for reflection
    public ComplexChild(){}

    public ComplexChild(int integer, String text, boolean aBoolean) {
        super(integer, text, aBoolean);
        childValue = integer * 2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComplexChild) {
            ComplexChild other = (ComplexChild) obj;
            return super.equals(other) && childValue == other.childValue;
        } else {
            return false;
        }
    }
}
