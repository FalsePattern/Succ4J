package succ;


import falsepattern.reflectionhelper.ClassTree;
import org.junit.jupiter.api.Assertions;
import succ.datafiles.memoryfiles.MemoryDataFile;

import java.util.Map;

public class TestUtilities {
    public static <T> void performSaveLoadTest(ClassTree<T> type, T savedValue) {
        String savedValueKey = "test key";
        MemoryDataFile file = new MemoryDataFile();
        file.set(type, savedValueKey, savedValue);
        T loadedValue = type.type.cast(file.get(type, savedValueKey));
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
