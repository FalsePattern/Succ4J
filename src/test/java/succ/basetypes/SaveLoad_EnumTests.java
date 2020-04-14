package succ.basetypes;

import falsepattern.reflectionhelper.ClassTree;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import succ.TestUtilities;

public class SaveLoad_EnumTests {
    @ParameterizedTest
    @EnumSource(TestEnum.class)
    public void saveLoad_Enum(TestEnum value) {
        TestUtilities.performSaveLoadTest(new ClassTree<>(TestEnum.class), value);
    }


    public enum TestEnum {
        a, b, c, d, e, f
    }
}
