package com.github.falsepattern.succ4j.saveloadtests;

import com.github.falsepattern.util.reflectionhelper.ClassTree;
import org.junit.jupiter.api.Test;
import com.github.falsepattern.succ4j.ComplexType;
import com.github.falsepattern.succ4j.TestUtilities;

public class SaveLoad_ComplexTypeTests {
    @Test
    public void saveLoad_ComplexType() {
        TestUtilities.performSaveLoadTest(new ClassTree<>(ComplexType.class), new ComplexType(69, "sugandese nuts lmao", true));
    }

    @Test
    public void saveLoad_ComplexType_Null() {
        TestUtilities.performSaveLoadTest(new ClassTree<>(ComplexType.class), null);
    }
}
