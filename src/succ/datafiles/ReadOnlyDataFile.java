package succ.datafiles;

import falsepattern.CustomFileWatcher;
import falsepattern.FalseUtil;
import succ.datafiles.abstractions.IDataFileOnDisk;
import succ.datafiles.abstractions.ReadableDataFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A read-only version of {@link DataFile}. Data can be read from disk, but not saved to disk.
 */
public class ReadOnlyDataFile extends ReadableDataFile implements IDataFileOnDisk, Runnable {

    /**
     * Creates a new ReadOnlyDataFile object corresponding to a SUCC file in system storage.
     * @param path The path of the file. Can be either absolute or relative to the default path.
     */
    public ReadOnlyDataFile(String path) {
        this(path, null);
    }

    /**
     * Creates a new ReadOnlyDataFile object corresponding to a SUCC file in system storage.
     * @param path The path of the file. Can be either absolute or relative to the default path.
     * @param defaultFileText If there isn't already a file at the path, one can be created from the text supplied here.
     */
    public ReadOnlyDataFile(String path, String defaultFileText) {
        super(defaultFileText);
        path = Utilities.absolutePath(path);
        path = FalseUtil.changeExtension(path, Utilities.fileExtension);
        this.filePath.set(path);

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
    protected synchronized String getSavedText() {
        Path path = Paths.get(filePath.get());
        if (Files.exists(path)) {
            try {
                return new String(Files.readAllBytes(path));
            } catch (IOException e) {
                throw new RuntimeException("Error reading file " + path, e);
            }
        }

        return "";
    }

    //the following code is copied between DataFile and ReadOnlyDataFile
    private final AtomicReference<String> filePath = new AtomicReference<>();
    public final AtomicReference<Runnable> onAutoReload = new AtomicReference<>();
    private final AtomicBoolean _autoReload = new AtomicBoolean(false);
    private final AtomicReference<CustomFileWatcher> watcher = new AtomicReference<>();
    protected final AtomicBoolean ignoreNextFileReload = new AtomicBoolean();

    @Override
    public synchronized String getFilePath() {
        return filePath.get();
    }

    @Override
    public Runnable onAutoReload() {
        return onAutoReload.get();
    }

    @Override
    public synchronized String getIdentifier() {
        return getFilePath();
    }

    @Override
    public synchronized boolean getAutoReload() {
        return _autoReload.get();
    }

    @Override
    public synchronized void setAutoReload(boolean value) {
        _autoReload.set(value);
        //TODO Watcher.EnableRaisingEvents = value;

        if (value) {
            ignoreNextFileReload.set(false);
        }
    }

    protected synchronized void setupWatcher() {
        Path path = Paths.get(getFilePath());
        try {
            WatchService watchService = path.getFileSystem().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            watcher.set(new CustomFileWatcher(watchService));
            watcher.get().addCallback(() -> {
                if (!_autoReload.get())
                    return;

                if (ignoreNextFileReload.get()) {
                    ignoreNextFileReload.set(false);
                    return;
                }

                reloadAllData();
                onAutoReload();
            });
        } catch (IOException e) {
            throw new RuntimeException("Error while initializing SUCC file watcher: ", e);
        }
    }

    @Override
    public void run() {
        if (!_autoReload.get()) {
            return;
        }

        if (ignoreNextFileReload.get()) {
            ignoreNextFileReload.set(false);
            return;
        }

        reloadAllData();
        onAutoReload().run();
    }
}