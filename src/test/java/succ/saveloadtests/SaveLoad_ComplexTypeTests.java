package succ.saveloadtests;

import falsepattern.reflectionhelper.ClassTree;
import org.junit.jupiter.api.Test;
import succ.ComplexType;
import succ.TestUtilities;

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
