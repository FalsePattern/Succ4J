package succ;

import falsepattern.Out;
import falsepattern.reflectionhelper.ClassTree;
import succ.datafiles.ReadOnlyDataFile;
import succ.datafiles.abstractions.ReadableDataFile;
import succ.datafiles.memoryfiles.MemoryReadOnlyDataFile;
import succ.parsinglogic.ParsingLogicExtensions;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static falsepattern.FalseUtil.changeExtension;

/**
 * Represents SUCC data spread over multiple files.
 * Using this class, you can search all those files at once for a piece of data.
 */
public class DistributedData {
    private final List<ReadableDataFile> dataSources = new ArrayList<>();
    public List<ReadableDataFile> getDataSources() {
        return Collections.unmodifiableList(dataSources);
    }

    /**
     * Adds a {@link ReadableDataFile} as a data source that this {@link DistributedData} can search through.
     */
    public void addDataSource(ReadableDataFile source) {
        dataSources.add(source);
    }

    /**
     * Creates a new {@link DistributedData}.
     */
    public DistributedData() {}

    /**
     * Creates a new {@link DistributedData} by searching a folder for matching SUCC files.
     * @param path The path of the directory to search.
     * @param searchPattern The search regular expression to match against the names of files. You do not need to add the ".succ" extension.
     * @param recursive Whether to iterate through all subdirectories.
     */
    public static DistributedData createBySearching(String path, String searchPattern, boolean recursive) {
        return createBySearching(new File(path), searchPattern, recursive);
    }

    /**
     * Creates a new {@link DistributedData} by searching a folder for matching SUCC files.
     * @param directory The directory to search.
     * @param searchPattern The search regular expression to match against the names of files. You do not need to add the ".succ" extension.
     * @param recursive Whether to iterate through all subdirectories.
     */
    public static DistributedData createBySearching(File directory, String searchPattern, boolean recursive) {
        searchPattern = changeExtension(searchPattern, Utilities.fileExtension);
        Pattern pattern = Pattern.compile(searchPattern);
        List<File> directories = new ArrayList<>();
        List<File> fileList = new ArrayList<>();
        directories.add(directory);
        while (directories.size() > 0) {
            File dir = directories.remove(0);
            File[] files = dir.listFiles(pathname -> {
                if (recursive && pathname.isDirectory()) {
                    directories.add(pathname);
                    return false;
                }
                return pattern.matcher(pathname.getName()).find();
            });
            if (files != null) {
                fileList.addAll(Arrays.asList(files));
            }
        }

        DistributedData data = new DistributedData();
        data.addFilesOnDisk(fileList.stream().map(File::getAbsolutePath).collect(Collectors.toList()));
        return data;
    }

    /**
     * All of the top-level keys in all of the files within this {@link DistributedData}.
     */
    public Collection<String> getTopLevelKeys() {
        Set<String> keys = new HashSet<>();

        for (ReadableDataFile source: dataSources) {
            keys.addAll(source.topLevelKeys());
        }

        return Collections.unmodifiableCollection(keys);
    }

    /**
     * The top level keys in this dataset, in the order they appear in the files, sorted by the file identifier.
     * Unlike {@link #getTopLevelKeys()}, this might contain duplicate keys if there are two files with the same key.
     */
    public List<String> getTopLevelKeysInOrder() {
        List<String> keys = new ArrayList<>();

        for (ReadableDataFile source: dataSources.stream().sorted(Comparator.comparing(ReadableDataFile::getIdentifier)).collect(Collectors.toList())) {
            keys.addAll(Arrays.asList(source.getTopLevelKeysInOrder()));
        }

        return keys;
    }

    /**
     * Does data exist in any of our files at this top-level key?
     */
    public boolean keyDoesNotExist(String key) {
        for (ReadableDataFile file: dataSources) {
            if (file.keyExists(key)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Does data exist in any of our files at this nested path?
     */
    public boolean keyExistsAtPath(String... path) {
        for (ReadableDataFile file: dataSources) {
            if (file.keyExistsAtPath(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get some data from our files, or return a default value if the data does not exist.
     * @param type The class type of the return value
     * @param key What the data is labeled as within the file.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(ClassTree<T> type, String key) {
        return (T)getNonGeneric(type, key, ParsingLogicExtensions.getDefaultValue(type));
    }

    /**
     * Get some data from our files, or return a default value if the data does not exist.
     * @param key What the data is labeled as within the file.
     * @param defaultValue If the key does not exist in the file, this value is returned instead.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(ClassTree<T> type, String key, T defaultValue) {
        return (T) getNonGeneric(type, key, defaultValue);
    }

    public Object getNonGeneric(ClassTree<?> type, String key) {
        return getNonGeneric(type, key, ParsingLogicExtensions.getDefaultValue(type));
    }

    public Object getNonGeneric(ClassTree<?> type, String key, Object defaultValue) {
        if (defaultValue != null && !defaultValue.getClass().equals(type.type)) {
            throw new IllegalArgumentException("Type " + type.toString() + " must match type of default value with type " + defaultValue.getClass());
        }

        for (ReadableDataFile file: dataSources) {
            if (file.keyExists(key)) {
                return file.getNonGeneric(type, key, defaultValue);
            }
        }

        return defaultValue;
    }

    /**
     * Like {@link #get(ClassTree, String, Object)}, but works for nested paths instead of just the top level of the file.
     * @param defaultValue If the key does not exist in the file, this value is returned instead.
     * @param path The path to search for the data at.
     */
    @SuppressWarnings("unchecked")
    public <T> T getAtPath(ClassTree<T> type, T defaultValue, String... path) {
        return (T) getAtPathNonGeneric(type, defaultValue, path);
    }

    public Object getAtPathNonGeneric(ClassTree<?> type, String... path) {
        return getAtPathNonGeneric(type, ParsingLogicExtensions.getDefaultValue(type), path);
    }

    public Object getAtPathNonGeneric(ClassTree<?> type, Object defaultValue, String... path) {
        if (defaultValue != null && !defaultValue.getClass().equals(type.type)) {
            throw new IllegalArgumentException("Type " + type.toString() + " must match type of default value with type " + defaultValue.getClass());
        }

        for (ReadableDataFile file: dataSources) {
            if (file.keyExistsAtPath(path)) {
                return file.getAtPathNonGeneric(type, defaultValue, path);
            }
        }

        return defaultValue;
    }

    public <T> boolean tryGet(ClassTree<T> type, String key, Out<T> value) {
        if (keyDoesNotExist(key)) {
            value.value = ParsingLogicExtensions.getDefaultValue(type);
            return false;
        }

        value.value = get(type, key);
        return true;
    }

    public boolean tryGetNonGeneric(ClassTree<?> type, String key, Out<Object> value) {
        if (keyDoesNotExist(key)) {
            value.value = null;
            return false;
        }

        value.value = getNonGeneric(type, key);
        return true;
    }


    //Imported extensions from DistributedDataExtensions.cs
    public void addDataSources(ReadableDataFile... sources) {
        addDataSources(Arrays.asList(sources));
    }

    public <T extends ReadableDataFile> void addDataSources(Iterable<T> sources) {
        sources.forEach(this::addDataSource);
    }

    public void addFilesOnDisk(String... paths) {
        addFilesOnDisk(Arrays.asList(paths));
    }

    public void addFilesOnDisk(Iterable<String> paths) {
        paths.forEach(this::addFileOnDisk);
    }

    public void addFileOnDisk(String path) {
        addDataSource(new ReadOnlyDataFile(path));
    }

    public void addRawSuccData(String... rawSuccDatas) {
        addRawSuccData(Arrays.asList(rawSuccDatas));
    }

    public void addRawSuccData(Iterable<String> rawSuccDatas) {
        rawSuccDatas.forEach(this::addRawSuccData);
    }

    public void addRawSuccData(String rawSuccData) {
        addDataSource(new MemoryReadOnlyDataFile(rawSuccData));
    }
}
