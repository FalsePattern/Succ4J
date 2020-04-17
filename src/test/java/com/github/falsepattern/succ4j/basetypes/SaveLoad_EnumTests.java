package com.github.falsepattern.succ4j.basetypes;

import com.github.falsepattern.util.reflectionhelper.ClassTree;
import com.github.falsepattern.succ4j.TestUtilities;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class SaveLoad_EnumTests {
    @ParameterizedTest
    @EnumSource(TestEnum.class)
    public void saveLoad_Enum(TestEnum value) {
        TestUtilities.performSaveLoadTest(new ClassTree<>(TestEnum.class), value);
    }


    public enum TestEnum {
        a, b, c, d, e, f
    }
}
