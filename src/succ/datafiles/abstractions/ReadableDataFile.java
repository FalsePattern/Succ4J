package succ.datafiles.abstractions;

import falsepattern.Out;
import succ.parsinglogic.ParsingLogicExtensions;
import succ.parsinglogic.nodes.KeyNode;
import succ.parsinglogic.nodes.Line;
import succ.parsinglogic.types.BaseTypes;

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
            var data = DataConverter.DataStructureFromSUCC(succ, this);
            topLevelLines = data.topLevelLines;
            topLevelNodes = data.topLevelNodes;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing data from file: ", e);
        }
    }

    /**
     * Gets the data as it appears in file.
     */
    public String getRawText() {
        return DataConverter.SUCCFromDataStructure(topLevelLines);
    }

    /**
     * Gets the data as it appears in file, as an array of strings (one for each line).
     */
    public String[] getRawLines() {
        return getRawText().split("\n"); //TODO universal splitting
    }

    /**
     * Returns all top level keys in the file, in the order they appear in the file.
     */
    public String[] getTopLevelKeysInOrder() {
        String[] keys = new String[topLevelNodes.size()];
        int count = 0;
        for (Line line : topLevelLines) {
            if (line instanceof KeyNode) {
                keys[count] = ((KeyNode) line).key; //TODO
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
     * Like {@link #get(String, T)},
     * but the default value is searched for in the default file text.
     * @param type The type to get the data as (required due to type erasure)
     * @param key What the data is labeled as within the file
     */
    public <T> T get(Class<T> type, String key) {
        return (T) getNonGeneric(type, key);
    }

    /**
     * Like {@link #getNonGeneric(Class, String, Object)},
     * but the default value is searched for in the default file text.
     * @param type The type to get the data as
     * @param key What the data is labeled as within the file
     */
    public Object getNonGeneric(Class<?> type, String key) {
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
    public <T> T get(String key, T defaultValue) {
        return (T) getNonGeneric(defaultValue.getClass(), key, defaultValue);
    }

    /**
     * Non-generic version of {@link #get(String, Object)}. You probably want to use {@link #get(String, Object)}.
     * @param type The type to get the data as
     * @param key What the data is labeled as within the file
     * @param defaultValue If the key does not exist in the file, this value is returned instead
     */
    public Object getNonGeneric(Class<?> type, String key, Object defaultValue) {
        if (!keyExists(key)) {
            return defaultValue;
        }

        KeyNode node = topLevelNodes.get(key);
        return NodeManager.getNodeData(node, type);
    }

    /**
     * Like {@link #getAtPath(T, String...)}, but the default value is searched for in the default file text
     * @param type The type to get the data as (required due to type erasure)
     * @param path The nested path of the desired data
     */
    public <T> T getAtPath(Class<T> type, String... path) {
        return (T) getAtPathNonGeneric(type, path);
    }

    /**
     * Like {@link #getAtPathNonGeneric(Class, Object, String...)}, but the value is searched for in the default file text
     * @param type The type to get the data as
     * @param path The nested path of the desired data
     */
    public Object getAtPathNonGeneric(Class<?> type, String... path) {
        Object defaultDefaultValue = ParsingLogicExtensions.getDefaultValue(type);
        Object defaultValue = defaultFileCache != null ? defaultFileCache.getAtPathNonGeneric(type, defaultDefaultValue, path) : defaultDefaultValue;

        return this.getAtPathNonGeneric(type, defaultValue, path);
    }

    /**
     * Like {@link #get(String, Object)} but works for nested paths instead of just the top level of the file.
     * @param defaultValue If the key does not exist in the file, this value is returned instead.
     * @param path The nested path of the desired data
     */
    public <T> T getAtPath(T defaultValue, String... path) {
        return (T) getAtPathNonGeneric(defaultValue.getClass(), defaultValue, path);
    }

    /**
     * Non-generic version of {@link #getAtPath(Object, String...)}. You probably want to use {@link #getAtPath(Object, String...)}.
     * @param type The type to get the data as
     * @param defaultValue If the key does not exist in the file, this value is returned instead.
     * @param path The nested path of the desired data
     */
    public Object getAtPathNonGeneric(Class<?> type, Object defaultValue, String... path) {
        if (defaultValue != null && ! type.equals(defaultValue.getClass())) {
            throw new RuntimeException("defaultValue is not of type " + type.getName());
        }

        if (!keyExistsAtPath(path)) {
            return defaultValue;
        }

        KeyNode topNode = topLevelNodes.get(path[0]);
        for (int i = 1; i < path.length; i++) {
            topNode = topNode.getChildAddressedByName(path[i]);
        }

        return NodeManager.GetNodeData(topNode, type);
    }

    public <T> boolean tryGet(String key, Out<T> value) {
        if (!keyExists(key)) {
            //value = default;
            return false;
        }

        value.value = get(value.type, key);
        return true;
    }

    /**
     * Interpret this file as a map. Top-level keys in the file are interpreted as keys in the map.
     * @param keyType The class of the map keys (required due to type erasure), must be a Base Type
     * @param valueType The class of the map values (required due to type erasure)
     */
    public <TKey, TValue> Map<TKey, TValue> getAsMap(Class<TKey> keyType, Class<TValue> valueType) {
        if (!BaseTypes.isBaseType(keyType)) {
            throw new RuntimeException("When using getAsMap, TKey must be a base type");
        }

        Set<String> keys = this.topLevelKeys();
        Map<TKey, TValue> map = new HashMap<>(keys.size());
        for (String keyText: keys) {
            TKey key = BaseTypes.parseBaseType(keyType, keyText);
            TValue value = NodeManager.getNodeData(valueType, topLevelNodes.get(keyText));
            map.put(key, value);
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
