package succ.saveloadtests;

import falsepattern.reflectionhelper.ClassTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import succ.ComplexType;
import succ.TestUtilities;
import succ.datafiles.memoryfiles.MemoryDataFile;

import java.util.HashMap;
import java.util.Map;

public class SaveLoad_NestedPaths {
    private static final String[] path0 = new String[]{"some", "body", "once", "told", "me"};
    private static final String[] path1 = new String[]{"the", "world", "is", "gonna", "roll", "me"};
    private static final String[] path2 = new String[]{"i", "aint", "the", "sharpest", "tool", "in", "the", "shed"};
    private static final String[] path3 = new String[]{"well", "the", "years", "start", "coming"};

    private static final String savedValue0 = "test1";
    private static final int savedValue1 = 1;
    private static final ComplexType savedValue2 = new ComplexType(1273, "down the Rockefeller street", true);
    private static final Map<String, String> savedValue3 = new HashMap<>();

    private static final ClassTree<String> tree0 = new ClassTree<>(String.class);
    private static final ClassTree<Integer> tree1 = new ClassTree<>(Integer.class);
    private static final ClassTree<ComplexType> tree2 = new ClassTree<>(ComplexType.class);
    @SuppressWarnings("unchecked")
    private static final ClassTree<Map<String, String>> tree3 = (ClassTree<Map<String, String>>) ClassTree.parseFromString("java.util.HashMap<java.lang.String, java.lang.String>");
    static {
        savedValue3.put("\uD83D\uDC40", "\uD83D\uDC4C");
        savedValue3.put("why", "not");
    }

    @Test
    public void saveLoad_nestedPaths() {
        MemoryDataFile file = new MemoryDataFile();
        file.setAtPath(tree0, savedValue0, path0);
        file.setAtPath(tree1, savedValue1, path1);
        file.setAtPath(tree2, savedValue2, path2);
        file.setAtPath(tree3, savedValue3, path3);
        String loadedValue0 = file.getAtPath(tree0, path0);
        Integer loadedValue1 = file.getAtPath(tree1, path1);
        ComplexType loadedValue2 = file.getAtPath(tree2, path2);
        Map<String, String> loadedValue3 = file.getAtPath(tree3, path3);
        Assertions.assertEquals(savedValue0, loadedValue0);
        Assertions.assertEquals(savedValue1, loadedValue1);
        Assertions.assertEquals(savedValue2, loadedValue2);
        TestUtilities.assertMapContentsEqual(savedValue3, loadedValue3);
    }
}
