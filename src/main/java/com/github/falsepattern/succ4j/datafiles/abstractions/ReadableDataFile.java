package com.github.falsepattern.succ4j.datafiles.abstractions;

import com.github.falsepattern.util.Out;
import com.github.falsepattern.util.Pair;
import com.github.falsepattern.util.reflectionhelper.ClassTree;
import com.github.falsepattern.succ4j.datafiles.memoryfiles.MemoryReadOnlyDataFile;
import com.github.falsepattern.succ4j.parsinglogic.DataConverter;
import com.github.falsepattern.succ4j.parsinglogic.NodeManager;
import com.github.falsepattern.succ4j.parsinglogic.ParsingLogicExtensions;
import com.github.falsepattern.succ4j.parsinglogic.nodes.KeyNode;
import com.github.falsepattern.succ4j.parsinglogic.nodes.Line;
import com.github.falsepattern.succ4j.parsinglogic.types.BaseTypes;

import java.util.*;

/**
 * A SUCC file that can be read from
 */
public abstract class ReadableDataFile {
    private List<Line> topLevelLines = new ArrayList<>(); // {get; private set;}
    private Map<String, KeyNode> topLevelNodes = new HashMap<>(); // {get; private set;}

    /**
     * A quasi-unique string, to be used when you need to sort a bunch of DataFiles.
     */
    public abstract String getIdentifier();

    // When a default value is not supplied, we search for it in this.
    protected final MemoryReadOnlyDataFile defaultFileCache;

    public ReadableDataFile() {
        this(null);
    }

    public ReadableDataFile(String defaultFileText) {
        if (defaultFileText == null) {
            defaultFileCache = null;
        } else {
            defaultFileCache = new MemoryReadOnlyDataFile(defaultFileText, null);
        }
    }

    /**
     * Load the file text from wherever you're storing it.
     */
    protected abstract String getSavedText();

    public void reloadAllData() {
        try {
            String succ = getSavedText();
            //TODO
            Pair<List<Line>, Map<String, KeyNode>> data = DataConverter.dataStructureFromSUCC(succ, this);
            topLevelLines = data.key;
            topLevelNodes = data.value;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing data from file: ", e);
        }
    }

    /**
     * Gets the data as it appears in file.
     */
    public String getRawText() {
        return DataConverter.succFromDataStructure(topLevelLines);
    }

    /**
     * Gets the data as it appears in file, as an array of strings (one for each line).
     */
    public String[] getRawLines() {
        return ParsingLogicExtensions.splitIntoLines(getRawText());
    }

    /**
     * Returns all top level keys in the file, in the order they appear in the file.
     */
    public String[] getTopLevelKeysInOrder() {
        String[] keys = new String[topLevelNodes.size()];
        int count = 0;
        for (Line line : topLevelLines) {
            if (line instanceof KeyNode) {
                keys[count] = ((KeyNode) line).getKey();
                count++;
            }
        }
        return keys;
    }

    /**
     * This is faster than GetTopLevelKeysInOrder() but the keys may not be in the order they appear in the file.
     */
    public Set<String> topLevelKeys() {
        return topLevelNodes.keySet();
    }

    /**
     * Whether a top-level key exists in the file.
     */
    public boolean keyExists(String key) {
        return topLevelNodes.containsKey(key);
    }

    /**
     * Whether a key exists in the file at a nested path
     */
    public boolean keyExistsAtPath(String... path) {
        if (path.length < 1) {
            throw new IllegalArgumentException("Path must have a length greater than 0");
        }

        if (!keyExists(path[0])) {
            return false;
        }

        KeyNode topNode = topLevelNodes.get(path[0]);
        for (int i = 1; i < path.length; i++) {
            if (topNode.containsChildNode(path[i])) {
                topNode = topNode.getChildAddressedByName(path[i]);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Like {@link #get(ClassTree, String, T)},
     * but the default value is searched for in the default file text.
     * @param type The type to get the data as (required due to type erasure)
     * @param key What the data is labeled as within the file
     */
    @SuppressWarnings("unchecked")
    public <T> T get(ClassTree<T> type, String key) {
        return (T) getNonGeneric(type, key);
    }

    /**
     * Like {@link #getNonGeneric(ClassTree, String, Object)},
     * but the default value is searched for in the default file text.
     * @param type The type to get the data as
     * @param key What the data is labeled as within the file
     */
    public Object getNonGeneric(ClassTree<?> type, String key) {
        Object defaultDefaultValue = ParsingLogicExtensions.getDefaultValue(type);
        Object defaultValue = defaultFileCache != null ? defaultFileCache.getNonGeneric(type, key, defaultDefaultValue) : defaultDefaultValue;
        return this.getNonGeneric(type, key, defaultValue);
    }

    //~~~~~c# specific comment from original source~~~~~
    // // many of these methods are virtual so that their overrides in ReadableWritableDataFile
    // // can have differing xml documentation.
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Get some data from the file, or return a default value if the data does not exist.
     * @param key What the data is labeled as within the file
     * @param defaultValue If the key does not exist in the file, this value is returned instead.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(ClassTree<T> type, String key, T defaultValue) {
        return (T) getNonGeneric(type, key, defaultValue);
    }

    /**
     * Non-generic version of {@link #get(ClassTree, String, Object)}. You probably want to use {@link #get(ClassTree, String, Object)}.
     * @param type The type to get the data as
     * @param key What the data is labeled as within the file
     * @param defaultValue If the key does not exist in the file, this value is returned instead
     */
    public Object getNonGeneric(ClassTree<?> type, String key, Object defaultValue) {
        if (!keyExists(key)) {
            return defaultValue;
        }

        KeyNode node = topLevelNodes.get(key);
        return NodeManager.getNodeData(node, type);
    }

    /**
     * Like {@link #getAtPath(ClassTree, Object, String[])}, but the default value is searched for in the default file text
     * @param type The type to get the data as (required due to type erasure)
     * @param path The nested path of the desired data
     */
    @SuppressWarnings("unchecked")
    public <T> T getAtPath(ClassTree<T> type, String[] path) {
        return (T) getAtPathNonGeneric(type, path);
    }

    /**
     * Like {@link #getAtPathNonGeneric(ClassTree, Object, String[])}, but the value is searched for in the default file text
     * @param type The type to get the data as
     * @param path The nested path of the desired data
     */
    public Object getAtPathNonGeneric(ClassTree<?> type, String[] path) {
        Object defaultDefaultValue = ParsingLogicExtensions.getDefaultValue(type);
        Object defaultValue = defaultFileCache != null ? defaultFileCache.getAtPathNonGeneric(type, defaultDefaultValue, path) : defaultDefaultValue;

        return this.getAtPathNonGeneric(type, defaultValue, path);
    }

    /**
     * Like {@link #get(ClassTree, String, Object)} but works for nested paths instead of just the top level of the file.
     * @param defaultValue If the key does not exist in the file, this value is returned instead.
     * @param path The nested path of the desired data
     */
    @SuppressWarnings("unchecked")
    public <T> T getAtPath(ClassTree<T> type, T defaultValue, String[] path) {
        return (T) getAtPathNonGeneric(type, defaultValue, path);
    }

    /**
     * Non-generic version of {@link #getAtPath(ClassTree, Object, String[])}. You probably want to use {@link #getAtPath(ClassTree, Object, String[])}.
     * @param type The type to get the data as
     * @param defaultValue If the key does not exist in the file, this value is returned instead.
     * @param path The nested path of the desired data
     */
    public Object getAtPathNonGeneric(ClassTree<?> type, Object defaultValue, String[] path) {
        if (defaultValue != null && ! type.type.equals(defaultValue.getClass())) {
            throw new RuntimeException("defaultValue is not of type " + type.toString());
        }

        if (!keyExistsAtPath(path)) {
            return defaultValue;
        }

        KeyNode topNode = topLevelNodes.get(path[0]);
        for (int i = 1; i < path.length; i++) {
            topNode = topNode.getChildAddressedByName(path[i]);
        }

        return NodeManager.getNodeData(topNode, type);
    }

    public <T> boolean tryGet(ClassTree<T> type, String key, Out<T> value) {
        if (!keyExists(key)) {
            //value = default;
            return false;
        }

        value.value = get(type, key);
        return true;
    }

    /**
     * Interpret this file as a map. Top-level keys in the file are interpreted as keys in the map.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Map<?, ?> getAsMap(ClassTree<?> mapType) {
        ClassTree<?> keyType = mapType.getChildren().get(0);
        ClassTree<?> valueType = mapType.getChildren().get(1);
        if (!BaseTypes.isBaseType(mapType.getChildren().get(0).type)) {
            throw new RuntimeException("When using getAsMap, the key type must be a base type");
        }

        Set<String> keys = this.topLevelKeys();
        Map<?, ?> map = new HashMap<>(keys.size());
        for (String keyText: keys) {
            Object key = BaseTypes.parseBaseType(keyText, keyType.type);
            Object value = NodeManager.getNodeData(topLevelNodes.get(keyText), valueType);
            ((Map)map).put(key, value);
        }

        return map;
    }




    //getters and setters
    private void setTopLevelLines(List<Line> topLevelLines) {
        this.topLevelLines = topLevelLines;
    }

    public List<Line> getTopLevelLines() {
        return topLevelLines;
    }

    private void setTopLevelNodes(Map<String, KeyNode> topLevelNodes) {
        this.topLevelNodes = topLevelNodes;
    }

    public Map<String, KeyNode> getTopLevelNodes() {
        return topLevelNodes;
    }
}
