package succ.saveloadtests;

import falsepattern.reflectionhelper.ClassTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import succ.ComplexType;
import succ.TestUtilities;
import succ.datafiles.memoryfiles.MemoryDataFile;

import java.util.*;

public class SaveLoad_CollectionTypeTests {
    private static final String savedValueKey = "test key";

    int[] savedIntArray;
    @SuppressWarnings("unchecked")
    @Test
    public void saveLoad_Array_Ints() throws NoSuchFieldException {
        ClassTree<int[]> type = (ClassTree<int[]>) ClassTree.parseFromFieldName(this.getClass(), "savedIntArray");
        savedIntArray = new int[]{0, 1, 2, 3};
        MemoryDataFile file = new MemoryDataFile();
        file.set(type, savedValueKey, savedIntArray);
        int[] loadedValue = file.get(type, savedValueKey);

        Assertions.assertArrayEquals(savedIntArray, loadedValue);
    }

    List<Integer> savedIntList;
    @SuppressWarnings("unchecked")
    @Test
    public void saveLoad_List_Ints() throws NoSuchFieldException {
        savedIntList = Arrays.asList(0, 1, 2, 3);
        MemoryDataFile file = new MemoryDataFile();
        file.set(ClassTree.parseFromFieldName(this.getClass(), "savedIntList"), savedValueKey, savedIntList);
        List<Integer> loadedValue = (List<Integer>)file.get(ClassTree.parseFromString("java.util.ArrayList<java.lang.Integer>"), savedValueKey);

        for (int i = 0; i < 4; i++) {
            Assertions.assertEquals(savedIntList.get(i), loadedValue.get(i));
        }

    }

    Set<Integer> savedIntSet;
    @SuppressWarnings("unchecked")
    @Test
    public void saveLoad_Set_Ints() throws NoSuchFieldException {
        savedIntSet = new HashSet<>(Arrays.asList(0, 1, 2, 3));
        MemoryDataFile file = new MemoryDataFile();
        file.set(ClassTree.parseFromFieldName(this.getClass(), "savedIntSet"), savedValueKey, savedIntSet);
        Set<Integer> loadedValue = (Set<Integer>)file.get(ClassTree.parseFromString("java.util.HashSet<java.lang.Integer>"), savedValueKey);

        for (Integer i : savedIntSet) {
            Assertions.assertTrue(loadedValue.contains(i));
        }
        for (Integer i : loadedValue) {
            Assertions.assertTrue(savedIntSet.contains(i));
        }
    }

    int[][][][] savedNestedIntArray;
    @SuppressWarnings("unchecked")
    @Test
    public void saveLoad_Array_DeeplyNestedInts() {
        ClassTree<int[][][][]> type = (ClassTree<int[][][][]>) ClassTree.parseFromString("[[[[I");
        savedNestedIntArray = deeplyNestedIntArray;
        MemoryDataFile file = new MemoryDataFile();
        file.set(type, savedValueKey, savedNestedIntArray);
        int[][][][] loadedValue = file.get(type, savedValueKey);

        for (int i = 0; i < savedNestedIntArray.length; i++) {
            for (int j = 0; j < savedNestedIntArray[i].length; j++) {
                for (int k = 0; k < savedNestedIntArray[i][j].length; k++) {
                    Assertions.assertArrayEquals(savedNestedIntArray[i][j][k], loadedValue[i][j][k]);
                }
            }
        }
    }

    Map<String, Integer> savedMapStringInt;
    @Test
    @SuppressWarnings("unchecked")
    public void saveLoad_Map_StringToInt() throws NoSuchFieldException {
        savedMapStringInt = new HashMap<>();
        savedMapStringInt.put("one", 1);
        savedMapStringInt.put("two", 2);
        savedMapStringInt.put("three", 3);
        MemoryDataFile file = new MemoryDataFile();
        file.set(ClassTree.parseFromFieldName(getClass(), "savedMapStringInt"), savedValueKey, savedMapStringInt);
        Map<String, Integer> loadedValue = (Map<String, Integer>) file.get(ClassTree.parseFromString("java.util.HashMap<java.lang.String, java.lang.Integer>"), savedValueKey);
        TestUtilities.assertMapContentsEqual(savedMapStringInt, loadedValue);
    }

    Map<ComplexType, ComplexType> savedMapComplex;
    @Test
    @SuppressWarnings("unchecked")
    public void saveLoad_Map_ComplexTypeToComplexType() throws NoSuchFieldException {
        savedMapComplex = new HashMap<>();
        savedMapComplex.put(new ComplexType(832, "jfhkdslfjsd", true), new ComplexType(22323, "\n", false));
        savedMapComplex.put(new ComplexType(Integer.MAX_VALUE, "oof ouch owie my unit test", false), new ComplexType(Integer.MIN_VALUE, "penis lmao", true));
        savedMapComplex.put(new ComplexType(8564698, "I like socialized healthcare", true), new ComplexType(99999, "aaaaaaaaaaaaaaaaa", true));
        MemoryDataFile file = new MemoryDataFile();
        file.set(ClassTree.parseFromFieldName(getClass(), "savedMapComplex"), savedValueKey, savedMapComplex);
        Map<ComplexType, ComplexType> loadedValue = (Map<ComplexType, ComplexType>) file.get(ClassTree.parseFromString("java.util.HashMap<succ.ComplexType, succ.ComplexType>"), savedValueKey);
        TestUtilities.assertMapContentsEqual(savedMapComplex, loadedValue);
    }

    private static final int[][][][] deeplyNestedIntArray = new int[][][][]
            {
                    new int[][][]
                            {
                                    new int[][]
                                            {
                                                    new int[]
                                                            {
                                                                    104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119,
                                                                    46, 121, 111, 117, 116, 117, 98, 101, 46, 99, 111,
                                                                    109, 47, 119, 97, 116, 99, 104, 63, 118, 61, 100,
                                                                    81, 119, 52, 119, 57, 87, 103, 88, 99, 81
                                                            },
                                                    new int[]
                                                            {
                                                                    110, 101, 105, 108, 99, 105, 99, 46, 99, 111, 109,
                                                                    47, 109, 111, 117, 116, 104, 109, 111, 111, 100, 115
                                                            },
                                                    new int[]
                                                            {
                                                                    104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119,
                                                                    46, 121, 111, 117, 116, 117, 98, 101, 46, 99, 111,
                                                                    109, 47, 119, 97, 116, 99, 104, 63, 118, 61, 100,
                                                                    81, 119, 52, 119, 57, 87, 103, 88, 99, 81
                                                            },
                                            },
                                    new int[][]
                                            {
                                                    new int[]
                                                            {
                                                                    104, 116, 116, 112, 58, 47, 47, 115, 117, 99, 99,
                                                                    46, 115, 111, 102, 116, 119, 97, 114, 101, 47
                                                            },
                                                    new int[]
                                                            {
                                                                    104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119,
                                                                    46, 121, 111, 117, 116, 117, 98, 101, 46, 99, 111,
                                                                    109, 47, 119, 97, 116, 99, 104, 63, 118, 61, 100,
                                                                    81, 119, 52, 119, 57, 87, 103, 88, 99, 81
                                                            },
                                                    new int[]
                                                            {
                                                                    104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119,
                                                                    46, 121, 111, 117, 116, 117, 98, 101, 46, 99, 111,
                                                                    109, 47, 119, 97, 116, 99, 104, 63, 118, 61, 119,
                                                                    48, 97, 99, 110, 102, 108, 87, 114, 90, 52
                                                            },
                                            },
                            },
                    new int[][][]
                            {
                                    new int[][]
                                            {
                                                    new int[]
                                                            {
                                                                    115, 117, 103, 97, 110, 100, 101, 115, 101, 32, 110,
                                                                    117, 116, 115, 32, 108, 109, 97, 111
                                                            },
                                                    new int[]
                                                            {
                                                                    104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119,
                                                                    46, 121, 111, 117, 116, 117, 98, 101, 46, 99, 111,
                                                                    109, 47, 119, 97, 116, 99, 104, 63, 118, 61, 100,
                                                                    81, 119, 52, 119, 57, 87, 103, 88, 99, 81
                                                            },
                                                    new int[]
                                                            {
                                                                    104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119,
                                                                    46, 121, 111, 117, 116, 117, 98, 101, 46, 99, 111,
                                                                    109, 47, 119, 97, 116, 99, 104, 63, 118, 61, 100,
                                                                    81, 119, 52, 119, 57, 87, 103, 88, 99, 81
                                                            },
                                            },
                                    new int[][]
                                            {
                                                    new int[]
                                                            {
                                                                    119, 111, 117, 108, 100, 32, 121, 111, 117, 32, 97,
                                                                    99, 99, 101, 112, 116, 32, 111, 110, 101, 32, 109,
                                                                    105, 108, 108, 105, 111, 110, 32, 100, 111, 108,
                                                                    108, 97, 114, 115, 32, 102, 111, 114, 32, 111, 110,
                                                                    101, 32, 116, 104, 111, 117, 115, 97, 110, 100, 32,
                                                                    114, 97, 110, 100, 111, 109, 32, 112, 101, 111, 112,
                                                                    108, 101, 32, 116, 111, 32, 100, 105, 101, 63
                                                            },
                                                    new int[]
                                                            {
                                                                    104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119,
                                                                    46, 121, 111, 117, 116, 117, 98, 101, 46, 99, 111,
                                                                    109, 47, 119, 97, 116, 99, 104, 63, 118, 61, 100,
                                                                    81, 119, 52, 119, 57, 87, 103, 88, 99, 81
                                                            },
                                                    new int[]
                                                            {
                                                                    104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119,
                                                                    46, 121, 111, 117, 116, 117, 98, 101, 46, 99, 111,
                                                                    109, 47, 119, 97, 116, 99, 104, 63, 118, 61, 100,
                                                                    81, 119, 52, 119, 57, 87, 103, 88, 99, 81
                                                            },
                                            },
                            }
            };
}
