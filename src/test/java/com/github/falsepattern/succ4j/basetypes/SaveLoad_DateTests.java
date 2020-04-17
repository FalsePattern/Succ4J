package com.github.falsepattern.succ4j.basetypes;

import com.github.falsepattern.util.reflectionhelper.ClassTree;
import com.github.falsepattern.succ4j.TestUtilities;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveLoad_DateTests {
    @Test
    public void saveLoad_Date_MinValue() throws ParseException {
        TestUtilities.performSaveLoadTest(new ClassTree<>(Date.class), new SimpleDateFormat("yyyy-MM-dd").parse("0002-01-01")); //TODO stuff break before this date
    }

    @Test
    public void saveLoad_Date_JimmysBirthday() throws ParseException {
        TestUtilities.performSaveLoadTest(new ClassTree<>(Date.class), new SimpleDateFormat("yyyy-MM-dd").parse("2000-07-21"));
    }
}
