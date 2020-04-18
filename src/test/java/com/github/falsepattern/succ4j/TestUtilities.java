package com.github.falsepattern.succ4j;


import com.github.falsepattern.util.reflectionhelper.ClassTree;
import org.junit.jupiter.api.Assertions;
import com.github.falsepattern.succ4j.datafiles.memoryfiles.MemoryDataFile;

import java.util.Map;

public class TestUtilities {
    public static void performSaveLoadTest(ClassTree<?> type, Object savedValue) {
        String savedValueKey = "test key";
        MemoryDataFile file = new MemoryDataFile();
        file.set(type, savedValueKey, savedValue);
        Object loadedValue = type.type.cast(file.get(type, savedValueKey));
        Assertions.assertEquals(savedValue, loadedValue);
    }

    public static void assertMapContentsEqual(Map<?, ?> expected, Map<?, ?> actual) {
        for (Map.Entry<?, ?> savedEntry : expected.entrySet()) {
            boolean anyMatch = false;
            for (Map.Entry<?, ?> loadedEntry: actual.entrySet()) {
                if (savedEntry.getKey().equals(loadedEntry.getKey()) && savedEntry.getValue().equals(loadedEntry.getValue())) {
                    anyMatch = true;
                    break;
                }
            }
            Assertions.assertTrue(anyMatch);
        }

        for (Map.Entry<?, ?> loadedEntry : actual.entrySet()) {
            boolean anyMatch = false;
            for (Map.Entry<?, ?> savedEntry: expected.entrySet()) {
                if (loadedEntry.getKey().equals(savedEntry.getKey()) && loadedEntry.getValue().equals(savedEntry.getValue())) {
                    anyMatch = true;
                    break;
                }
            }
            Assertions.assertTrue(anyMatch);
        }
    }
}
