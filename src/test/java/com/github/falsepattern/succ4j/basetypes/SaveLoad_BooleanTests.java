package com.github.falsepattern.succ4j.basetypes;

import com.github.falsepattern.util.reflectionhelper.ClassTree;
import com.github.falsepattern.succ4j.TestUtilities;
import org.junit.jupiter.api.Test;

public class SaveLoad_BooleanTests {
    @Test
    public void saveLoad_True() {
        TestUtilities.performSaveLoadTest(new ClassTree<>(Boolean.class), true);
    }
    @Test
    public void saveLoad_False() {
        TestUtilities.performSaveLoadTest(new ClassTree<>(Boolean.class), false);
    }
}
