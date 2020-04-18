package com.github.falsepattern.succ4j.parsinglogic;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GenericID {
    int id();
}
