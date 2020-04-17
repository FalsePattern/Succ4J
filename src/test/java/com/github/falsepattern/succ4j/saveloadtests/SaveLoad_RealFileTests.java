package com.github.falsepattern.succ4j.saveloadtests;

import com.github.falsepattern.util.reflectionhelper.ClassTree;
import com.github.falsepattern.succ4j.datafiles.DataFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.github.falsepattern.succ4j.Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class SaveLoad_RealFileTests {
    private static final String savedValueKey = "test key";

    @Test
    public void saveLoad_WriteAndReadFromFile() throws IOException {
        String fileName = generateUniqueFilePath();
        String savedValue = "test value";
        DataFile file = new DataFile(fileName);
        file.set(new ClassTree<>(String.class), savedValueKey, savedValue);
        DataFile otherFile = new DataFile(fileName);
        String loadedValue = otherFile.get(new ClassTree<>(String.class), savedValueKey);
        Assertions.assertEquals(savedValue, loadedValue);
        deleteFile(fileName);
    }

    @Test
    public void saveLoad_TestAutoReload() throws InterruptedException, IOException {
        String fileName = generateUniqueFilePath();
        String savedValueA = "test1";
        String savedValueB = "test2";
        DataFile file = new DataFile(fileName);
        file.set(new ClassTree<>(String.class), savedValueKey, savedValueA);
        file.setAutoReload(true);
        DataFile otherFile = new DataFile(fileName);
        otherFile.set(new ClassTree<>(String.class), savedValueKey, savedValueB);
        Thread.sleep(1500); // await auto-update
        String loadedValue = file.get(new ClassTree<>(String.class), savedValueKey);
        Assertions.assertEquals(savedValueB, loadedValue);
        file.setAutoReload(false);
        otherFile.setAutoReload(false);
        deleteFile(fileName);
    }

    private static String generateUniqueFilePath() {
        String filePath;
        do {
            filePath = Utilities.absolutePath(UUID.randomUUID().toString() + ".succ");
        } while (Files.exists(Paths.get(filePath)));
        return filePath;
    }

    private static void deleteFile(String path) throws IOException {
        Files.delete(Paths.get(path));
    }

}
