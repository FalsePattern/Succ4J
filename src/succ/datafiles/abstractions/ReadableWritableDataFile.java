package succ.datafiles.abstractions;

import falsepattern.Out;
import succ.Utilities;
import succ.parsinglogic.NodeManager;
import succ.parsinglogic.nodes.KeyNode;
import succ.parsinglogic.nodes.Node;
import succ.parsinglogic.types.BaseTypes;
import succ.parsinglogic.types.ComplexTypes;
import succ.style.FileStyle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ReadableWritableDataFile extends ReadableDataFile {

    /**
     * Rules for how to format new data saved to this file.
     */
    public FileStyle style = FileStyle.defaultStyle;

    /**
     * If true, the DataFile will automatically save changes to disk with each Get or Set.
     * If false, you must call SaveAllData() manually.
     * Be careful with this. You do not want to accidentally be writing to a user's disk at 1000MB/s for 3 hours.
     */
    public boolean autoSave = true;

    public ReadableWritableDataFile() {
        this(null);
    }

    public ReadableWritableDataFile(String defaultFileText) {
        super(defaultFileText);
    }

    /**
     * Save the file text to wherever you're storing it.
     */
    public abstract void setSavedText(String text);

    /**
     * Reset the file to the default data provided when it was created.
     */
    public void resetToDefaultData() {
        setSavedText(defaultFileCache != null ? defaultFileCache.getRawText() : "");
        reloadAllData();
    }

    /**
     * Serializes the data in this object to the file on disk.
     */
    public void saveAllData() {
        String SUCC = getRawText();
        String existingSUCC = getSavedText();

        if (!SUCC.equals(existingSUCC)) {
            setSavedText(SUCC);
        }
    }

    /**
     * Get some data from the file, saving a new value if the data does not exist.
     * @param key What the data is labeled as within the file
     * @param defaultValue If the key does not exist in the file, this value is saved there and returned
     */
    @Override
    public <T> T get(String key, T defaultValue) {
        return super.get(key, defaultValue);
    }

    /**
     * Non-generic version of {@link #get( String, Object)}. You probably want to use {@link #get(String, Object)}.
     * @param type The type to get the data as
     * @param key What the data is labeled as within the file
     * @param defaultValue If the key does not exist in the file, this value is saved there and returned
     */
    @Override
    public Object getNonGeneric(Class<?> type, String key, Object defaultValue) {
        if (!keyExists(key)) {
            setNonGeneric(type, key, defaultValue);
            return defaultValue;
        }

        KeyNode node = getTopLevelNodes().get(key);
        return NodeManager.getNodeData(node, type);
    }

    /**
     * Save data to the file.
     * @param key What the data is labeled as within the file
     * @param value The value to save
     */
    public  <T> void set(String key, T value) {
        setNonGeneric(value.getClass(), key, value);
    }

    public void setNonGeneric(Class<?> type, String key, Object value) {
        if (value != null && !type.equals(value.getClass())) {
            throw new ClassCastException("Value is not of type " + type.getName());
        }

        if (!keyExists(key)) {
            KeyNode newNode = new KeyNode(0, key, this);
            getTopLevelNodes().put(key, newNode);
            getTopLevelLines().add(newNode);
        }

        KeyNode node = getTopLevelNodes().get(key);
        NodeManager.setNodeData(node, value, type, style);

        if (autoSave) {
            saveAllData();
        }
    }

    @Override
    public Object getAtPathNonGeneric(Class<?> type, Object defaultValue, String... path) {
        if (!keyExistsAtPath(path)) {
            setAtPathNonGeneric(type, defaultValue, path);
            return defaultValue;
        }

        KeyNode topNode = getTopLevelNodes().get(path[0]);
        for (int i = 1; i < path.length; i++) {
            topNode = topNode.getChildAddressedByName(path[i]);
        }

        return NodeManager.getNodeData(topNode, type);
    }

    /**
     * Like {@link #set(String, Object)} but works for nested paths instead of just the top level of the file.
     * @param value The value to save
     * @param path The nested path of the desired data location
     */
    public <T> void setAtPath(T value, String... path) {
        setAtPathNonGeneric(value.getClass(), value, path);
    }

    /**
     * Non-generic version of {@link #setAtPath(Object, String...)}. You probably want to use {@link #setAtPath(Object, String...)}.
     * @param type The type to save the data as
     * @param value The value to save
     * @param path The nested path of the desired data location
     */
    public void setAtPathNonGeneric(Class<?> type, Object value, String... path) {
        if (value != null && !value.getClass().equals(type)) {
            throw new ClassCastException("Value is not of type " + type.getName());
        }

        if (path.length < 1) {
            throw new IllegalArgumentException("Path must have a length greater than 0");
        }

        if (!keyExists(path[0])) {
            KeyNode newNode = new KeyNode(0, path[0], this);
            getTopLevelNodes().put(path[0], newNode);
            getTopLevelLines().add(newNode);
        }

        KeyNode topNode = getTopLevelNodes().get(path[0]);

        for (int i = 1; i < path.length; i++) {
            topNode = topNode.getChildAddressedByName(path[i]);
        }

        NodeManager.setNodeData(topNode, value, type, style);

        if (autoSave) {
            saveAllData();
        }
    }

    /**
     * Remove a top-level key and all its data from the file.
     */
    public void deleteKey(String key) {
        if (!keyExists(key)) {
            return;
        }

        Node node = getTopLevelNodes().get(key);
        getTopLevelNodes().remove(key);
        getTopLevelLines().remove(node);
    }

    /**
     * Save this file as an object of type T, using that type's fields and properties as top-level keys.
     * @param saveThis The object to save
     */
    public <T> void saveAsObject(T saveThis) {
        saveAsObjectNonGeneric(saveThis.getClass(), saveThis);
    }

    /**
     * Non-generic version of {@link #saveAsObject(Object)}. You probably want to use {@link #saveAsObject(Object)}.
     * @param type What type to save this object as
     * @param saveThis The object to save
     */
    public void saveAsObjectNonGeneric(Class<?> type, Object saveThis) {
        boolean _autoSave = autoSave;
        autoSave = false; // don't write to disk when we don't have to

        try {
            for(Field f: ComplexTypes.getValidFields(type)) {
                setNonGeneric(f.getClass(), f.getName(), f.get(saveThis));
            }

            //Properties don't exist in Java 8
            //Un-ported c# code:
            //  foreach (var p in ComplexTypes.GetValidProperties(type))
            //    SetNonGeneric(p.PropertyType, p.Name, p.GetValue(saveThis));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            autoSave = _autoSave;
        }

        if (autoSave) saveAllData();
    }

    /**
     * Save this file as a map, using the map's keys as top-level keys in the file.
     */
    public <TKey, TValue> void saveAsMap(Map<TKey, TValue> map) {

        boolean _autoSave = autoSave;
        autoSave = false; // don't write to disk when we don't have to

        try {
            List<String> currentKeys = new ArrayList<>(map.size());
            boolean first = true;
            for (TKey key: map.keySet()) {
                if (first && !BaseTypes.isBaseType(key.getClass())) {
                    throw new RuntimeException("When using saveAsMap, TKey must be a base type");
                }
                first = false;
                String keyText = BaseTypes.serializeBaseType(key, key.getClass(), style);
                {
                    Out<String> whyNot = new Out<>(String.class);
                    if (!Utilities.isValidKey(keyText, whyNot)) {
                        throw new RuntimeException("Can't save file as this dictionary. A key (" + keyText + ") is not valid: " + whyNot.value);
                    }
                }

                currentKeys.add(keyText);
                set(keyText, map.get(key));
            }

            for(String key: this.topLevelKeys()) {
                if (!currentKeys.contains(key)) {
                    this.getTopLevelNodes().remove(key);
                }
            }
        } finally {
            autoSave = _autoSave;
        }

        if (autoSave) {
            saveAllData();
        }
    }
}
