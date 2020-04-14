package succ.basetypes;

import falsepattern.reflectionhelper.ClassTree;
import org.junit.jupiter.api.Test;
import succ.TestUtilities;

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
