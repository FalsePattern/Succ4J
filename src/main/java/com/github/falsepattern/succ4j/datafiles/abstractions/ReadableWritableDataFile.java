package com.github.falsepattern.succ4j.datafiles.abstractions;

import com.github.falsepattern.util.Out;
import com.github.falsepattern.util.reflectionhelper.ClassTree;
import com.github.falsepattern.succ4j.parsinglogic.NodeManager;
import com.github.falsepattern.succ4j.parsinglogic.nodes.KeyNode;
import com.github.falsepattern.succ4j.parsinglogic.nodes.Node;
import com.github.falsepattern.succ4j.parsinglogic.types.BaseTypes;
import com.github.falsepattern.succ4j.style.FileStyle;
import com.github.falsepattern.succ4j.Utilities;
import com.github.falsepattern.succ4j.parsinglogic.types.ComplexTypes;

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
    public <T> T get(ClassTree<T> type, String key, T defaultValue) {
        return super.get(type, key, defaultValue);
    }

    /**
     * Non-generic version of {@link #get(ClassTree, String, Object)}. You probably want to use {@link #get(ClassTree, String, Object)}.
     * @param type The type to get the data as
     * @param key What the data is labeled as within the file
     * @param defaultValue If the key does not exist in the file, this value is saved there and returned
     */
    @Override
    public Object getNonGeneric(ClassTree<?> type, String key, Object defaultValue) {
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
    public <T> void set(ClassTree<?> type, String key, T value) {
        setNonGeneric(type, key, value);
    }

    public void setNonGeneric(ClassTree<?> type, String key, Object value) {
        if (value != null && !type.type.isInstance(value)) {
            throw new ClassCastException("Value is not of type " + type.toString());
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
    public Object getAtPathNonGeneric(ClassTree<?> type, Object defaultValue, String... path) {
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
     * Like {@link #set(ClassTree, String, Object)} but works for nested paths instead of just the top level of the file.
     * @param value The value to save
     * @param path The nested path of the desired data location
     */
    public <T> void setAtPath(ClassTree<T> type, T value, String... path) {
        setAtPathNonGeneric(type, value, path);
    }

    /**
     * Non-generic version of {@link #setAtPath(ClassTree, Object, String...)}. You probably want to use {@link #setAtPath(ClassTree, Object, String...)}.
     * @param type The type to save the data as
     * @param value The value to save
     * @param path The nested path of the desired data location
     */
    public void setAtPathNonGeneric(ClassTree<?> type, Object value, String... path) {
        if (value != null && !type.type.isInstance(value)) {
            throw new ClassCastException("Value is not of type " + type.toString());
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
    public <T> void saveAsObject(ClassTree<T> type, T saveThis) {
        saveAsObjectNonGeneric(type, saveThis);
    }

    /**
     * Non-generic version of {@link #saveAsObject(ClassTree, Object)}. You probably want to use {@link #saveAsObject(ClassTree, Object)}.
     * @param type What type to save this object as
     * @param saveThis The object to save
     */
    public void saveAsObjectNonGeneric(ClassTree<?> type, Object saveThis) {
        boolean _autoSave = autoSave;
        autoSave = false; // don't write to disk when we don't have to

        try {
            for(Field f: ComplexTypes.getValidFields(type.type)) {
                setNonGeneric(ClassTree.parseFromField(f), f.getName(), f.get(saveThis));
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
    public <TKey, TValue> void saveAsMap(ClassTree<Map<?, ?>> classTree, Map<TKey, TValue> map) {

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
                set(classTree.getChildren().get(0), keyText, map.get(key));
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
