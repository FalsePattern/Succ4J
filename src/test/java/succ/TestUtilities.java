package succ;


import falsepattern.reflectionhelper.ClassTree;
import org.junit.jupiter.api.Assertions;
import succ.datafiles.memoryfiles.MemoryDataFile;

public class TestUtilities {
    public static <T> void performSaveLoadTest(ClassTree<T> type, T savedValue) {
        String savedValueKey = "test key";
        MemoryDataFile file = new MemoryDataFile();
        file.set(type, savedValueKey, savedValue);
        T loadedValue = type.type.cast(file.get(type, savedValueKey));
        Assertions.assertEquals(savedValue, loadedValue);
    }
}
