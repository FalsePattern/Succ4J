package com.github.falsepattern.succ4j.basetypes;

import com.github.falsepattern.util.reflectionhelper.ClassTree;
import com.github.falsepattern.succ4j.TestUtilities;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SaveLoad_IntTests {
    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, -1L, Long.MIN_VALUE, Long.MAX_VALUE})
    public void saveLoad_Int(long savedValue) {
        TestUtilities.performSaveLoadTest(new ClassTree<>(Long.class), savedValue);
    }
    @ParameterizedTest
    @ValueSource(ints = {0, 1, -1, Integer.MIN_VALUE, Integer.MAX_VALUE})
    public void saveLoad_Int(int savedValue) {
        TestUtilities.performSaveLoadTest(new ClassTree<>(Integer.class), savedValue);
    }
    @ParameterizedTest
    @ValueSource(shorts = {(short)0, (short)1, (short)-1, Short.MIN_VALUE, Short.MAX_VALUE})
    public void saveLoad_Int(short savedValue) {
        TestUtilities.performSaveLoadTest(new ClassTree<>(Short.class), savedValue);
    }
    @ParameterizedTest
    @ValueSource(bytes = {(byte)0, (byte)1, (byte)-1, Byte.MIN_VALUE, Byte.MAX_VALUE})
    public void saveLoad_Int(byte savedValue) {
        TestUtilities.performSaveLoadTest(new ClassTree<>(Byte.class), savedValue);
    }
}
