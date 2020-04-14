package succ.parsinglogic;


import java.lang.annotation.*;

/**
 * Public fields and properties with this attribute will NOT be saved and loaded by SUCC.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DontSave {
}
