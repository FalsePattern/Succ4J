package com.github.falsepattern.succ4j;

import com.github.falsepattern.succ4j.parsinglogic.DoSave;
import com.github.falsepattern.succ4j.parsinglogic.GenericID;

public class ComplexChild<T> extends ComplexType{
    @DoSave
    @GenericID(id=0)
    private T childValue;

    // parameter-less constructor required for reflection
    public ComplexChild(){}

    public ComplexChild(int integer, String text, boolean aBoolean, T genericValue) {
        super(integer, text, aBoolean);
        childValue = genericValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComplexChild) {
            ComplexChild<?> other = (ComplexChild<?>) obj;
            return super.equals(other) && childValue.equals(other.childValue);
        } else {
            return false;
        }
    }
}
