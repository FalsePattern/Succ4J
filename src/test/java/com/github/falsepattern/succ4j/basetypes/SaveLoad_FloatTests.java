package com.github.falsepattern.succ4j.basetypes;

import com.github.falsepattern.util.reflectionhelper.ClassTree;
import com.github.falsepattern.succ4j.TestUtilities;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SaveLoad_FloatTests {
    @ParameterizedTest
    @ValueSource(floats = {0f, 1f, -1f, 69.6969f, 1000000000000000000000000f, -1000000000000000000000000f, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NaN})
    public void saveLoad_Float(float savedValue) {
        TestUtilities.performSaveLoadTest(new ClassTree<>(Float.class), savedValue);
    }
    @ParameterizedTest
    @ValueSource(doubles = {0d, 1d, -1d, 69.6969d, 1000000000000000000000000d, -1000000000000000000000000d, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NaN})
    public void saveLoad_Double(double savedValue) {
        TestUtilities.performSaveLoadTest(new ClassTree<>(Double.class), savedValue);
    }
}
