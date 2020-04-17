package com.github.falsepattern.succ4j.parsinglogic;

import java.lang.annotation.*;

/**
 * Private fields and properties with this attribute WILL be saved and loaded by SUCC.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DoSave {
}
