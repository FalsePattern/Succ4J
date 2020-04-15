package succ.datafiles;

import falsepattern.CustomFileWatcher;
import falsepattern.FalseUtil;
import succ.Utilities;
import succ.datafiles.abstractions.IDataFileOnDisk;
import succ.datafiles.abstractions.ReadableWritableDataFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DataFile extends ReadableWritableDataFile implements IDataFileOnDisk {

    /**
     * Creates a new DataFile object corresponding to a SUCC file in system storage.
     * @param path The path of the file. Can be either absolute or relative to the default path.
     */
    public DataFile(String path) {
        this(path, null);
    }

    /**
     * Creates a new DataFile object corresponding to a SUCC file in system storage.
     * @param path The path of the file. Can be either absolute or relative to the default path.
     * @param defaultFileText If there isn't already a file at the path, one can be created from the text supplied here.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public DataFile(String path, String defaultFileText) {
        super(defaultFileText);
        path = Utilities.absolutePath(path);
        path = FalseUtil.changeExtension(path, Utilities.fileExtension);
        this.filePath = path;

        try {
            if (!Utilities.succFileExists(path)) {
                new File(path).getParentFile().mkdirs();
                if (defaultFileText == null) {
                    Files.createFile(Paths.get(path));
                } else {
                    Files.write(Paths.get(path), Collections.singleton(defaultFileText));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while creating succ file at " + path, e);
        }

        this.reloadAllData();

        setupWatcher();
    }

    @Override
    protected String getSavedText() {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            try {
                return new String(Files.readAllBytes(path));
            } catch (IOException e) {
                throw new RuntimeException("Error reading file " + path, e);
            }
        }

        return "";
    }

    @Override
    public void setSavedText(String text) {
        try {
            Files.write(Paths.get(filePath), Collections.singleton(text), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error while writing file " + filePath, e);
        }

        ignoreNextFileReload.set(true);
    }

    //the following code is copied between DataFile and ReadOnlyDataFile
    private final String filePath;
    private final AtomicBoolean _autoReload = new AtomicBoolean(false);
    protected final AtomicBoolean ignoreNextFileReload = new AtomicBoolean();
    public Runnable onAutoReload = () -> {
        if (!_autoReload.get()) {
            return;
        }

        if (ignoreNextFileReload.get()) {
            ignoreNextFileReload.set(false);
            return;
        }
        reloadAllData();
        onAutoReload().run();
    };

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public Runnable onAutoReload() {
        return onAutoReload;
    }

    @Override
    public String getIdentifier() {
        return getFilePath();
    }

    @Override
    public boolean getAutoReload() {
        return _autoReload.get();
    }

    @Override
    public void setAutoReload(boolean value) {
        _autoReload.set(value);
        //TODO Watcher.EnableRaisingEvents = value;

        if (value) {
            ignoreNextFileReload.set(false);
        }
    }

    protected void setupWatcher() {
        Path path = Paths.get(getFilePath());
        try {
            WatchService watchService = path.getParent().getFileSystem().newWatchService();
            path.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            CustomFileWatcher watcher = new CustomFileWatcher(watchService);
            watcher.addCallback(getFilePath(), onAutoReload);
        } catch (IOException e) {
            throw new RuntimeException("Error while initializing SUCC file watcher: ", e);
        }
    }


}
