package com.github.falsepattern.succ4j;

import com.github.falsepattern.util.reflectionhelper.ClassTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.github.falsepattern.succ4j.datafiles.memoryfiles.MemoryDataFile;

public class ComplexTypeShortcutTests {
    private static final String savedValueKey = "test key";

    @Test
    public void complexTypeShortcut_constructor_loadedValueEqualsShortcutValue() {
        ComplexType savedValue = new ComplexType(0, "example", true);
        MemoryDataFile file = new MemoryDataFile(savedValueKey + ":(0, \"example\", true)");
        ComplexType loadedValue = file.get(new ClassTree<>(ComplexType.class), savedValueKey);
        Assertions.assertEquals(savedValue, loadedValue);
    }
}
