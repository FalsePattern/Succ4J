package succ.datafiles.memoryfiles;

import succ.Utilities;
import succ.datafiles.ReadOnlyDataFile;
import succ.datafiles.abstractions.ReadableDataFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

/**
 * A read-only version of DataFile. Data can be read from disk, but not saved to disk.
 */
public class MemoryReadOnlyDataFile extends ReadableDataFile {

    /**
     * Creates an empty ReadOnlyDataFile in memory.
     * Why would you do this?
     */
    public MemoryReadOnlyDataFile() {
        this("");
    }

    public MemoryReadOnlyDataFile(String rawFileText) {
        this(rawFileText, rawFileText);
    }

    public MemoryReadOnlyDataFile(String rawFileText, String identifier) {
        this(rawFileText, identifier, null);
    }

    public MemoryReadOnlyDataFile(String rawFileText, String identifier, String defaultFileText) {
        super(defaultFileText);
        memoryTextData = rawFileText;
        this.identifier = identifier;
        this.reloadAllData();
    }

    private final String identifier;

    @Override
    public String getIdentifier() {
        return identifier;
    }

    private final String memoryTextData;

    @Override
    protected String getSavedText() {
        return memoryTextData;
    }

    /**
     * Saves the contents of this MemoryDataFile to disk and returns a disk DataFile corresponding to the new file.
     * @param relativeOrAbsolutePath The path of the new file.
     */
    public ReadOnlyDataFile convertToFileOnDisk(String relativeOrAbsolutePath) {
        return convertToFileOnDisk(relativeOrAbsolutePath, true);
    }

    /**
     * Saves the contents of this MemoryDataFile to disk and returns a disk DataFile corresponding to the new file.
     * @param relativeOrAbsolutePath The path of the new file.
     * @param overwrite If this is false, don't save the data if the file already exists on disk.
     * @return Null if overwrite was set to false and a file already existed.
     */
    public ReadOnlyDataFile convertToFileOnDisk(String relativeOrAbsolutePath, boolean overwrite) {
        if (!overwrite && Utilities.succFileExists(relativeOrAbsolutePath)) {
            return null;
        }

        String path = Utilities.absolutePath(relativeOrAbsolutePath);
        try {
            Files.write(Paths.get(path), Collections.singleton(getRawText()), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error writing file " + path, e);
        }
        return new ReadOnlyDataFile(path);
    }
}
