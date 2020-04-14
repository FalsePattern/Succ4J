package succ.datafiles.abstractions;

import falsepattern.FalseUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public interface IDataFileOnDisk {
    String getFilePath();

    default String getFileName() {
        return FalseUtil.getFileNameWithoutExtension(new File(getFilePath()).getName());
    }

    default long getSizeOnDisk() {
        try {
            return Files.size(Paths.get(getFileName()));
        } catch (IOException e) {
            throw new RuntimeException("Error while trying to get file size", e);
        }
    }

    boolean getAutoReload();
    void setAutoReload(boolean value);

    Runnable onAutoReload();
}
