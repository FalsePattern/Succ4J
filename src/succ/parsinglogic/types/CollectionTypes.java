package succ.parsinglogic.types;

import succ.Utilities;
import succ.parsinglogic.NodeManager;
import succ.parsinglogic.nodes.KeyNode;
import succ.parsinglogic.nodes.ListNode;
import succ.parsinglogic.nodes.Node;
import succ.parsinglogic.nodes.NodeChildrenType;
import succ.style.FileStyle;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;

public class CollectionTypes {
    public static <T, K> boolean trySetCollection(Node node, T data, Class<T> collectionType, Class<K> elementType, FileStyle style) {
        if (collectionType == null) {
            return false;
        } else if (collectionType.isArray()) {
            setArrayNode(node, data, collectionType, style);
        } else if (List.class.isAssignableFrom(collectionType)) {
            setListNode(node, (List<K>) data, elementType, style);
        } else if (Set.class.isAssignableFrom(collectionType)) {
            setSetNode(node, (Set<K>) data, elementType, style);
        } else if (Map.class.isAssignableFrom(collectionType)) {
            setMapNode(node, (Map<?, ?>)data, Object.class, elementType, style, false);
        } else if (node.childNodeType == NodeChildrenType.list) {
            throw new IllegalArgumentException(collectionType.getTypeName() + " is not a supported collection type");
        } else {
            return false;
        }

        return true;
    }

    public static <T> Object tryGetCollection(Node node, Class<?> collectionType, Class<T> elementType) {
        Object data = null;
        if (collectionType.isArray()) {
            data = retrieveArray(node, (Class<?>)collectionType);
        } else if (List.class.isAssignableFrom(collectionType)) {
            data = retrieveList(node, (Class<List<?>>) collectionType, elementType);
        } else if (Set.class.isAssignableFrom(collectionType)) {
            data = retrieveSet(node, (Class<Set<?>>) collectionType, elementType);
        } else if (Map.class.isAssignableFrom(collectionType)) {
            data = retrieveMap(node, (Class<Map<?, ?>>)collectionType, Object.class, elementType);
        } else if (node.childNodeType == NodeChildrenType.list) {
            throw new IllegalArgumentException(collectionType.getName() + " is not a supported collection type");
        }

        return data;
    }

    private static <T> void setArrayNode(Node node, T array, Class<T> arrayType, FileStyle style) {
        Class<?> elementType = arrayType.getComponentType();

        Object[] boi = getArray(array, arrayType);

        node.capChildCount(boi.length);

        for (int i = 0; i < boi.length; i++) {
            NodeManager.setNodeData(node.getChildAddressedByListNumber(i), boi[i], elementType, style);
        }

    }

    @SuppressWarnings("unchecked")
    private static <T> T retrieveArray(Node node, Class<T> arrayType) {
        Class<?> elementType = arrayType.getComponentType();
        Object[] array = (Object[])Array.newInstance(elementType, node.getChildNodes().size());

        for (int i = 0; i < array.length; i++) {
            ListNode child = node.getChildAddressedByListNumber(i);
            Object element = NodeManager.getNodeData(child, elementType);
            array[i] = element;
        }

        return (T)array;
    }

    private static <T> void setListNode(Node node, List<T> list, Class<T> elementType, FileStyle style) {
        int size = list.size();
        node.capChildCount(size);
        for (int i = 0; i < size; i++) {
            NodeManager.setNodeData(node.getChildAddressedByListNumber(i), list.get(i), elementType, style);
        }
    }

    private static List<?> retrieveList(Node node, Class<List<?>> listType, Class<?> elementType) {
        List<?> resultList = null;
        try {
            resultList = listType.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Cannot create List of type " + listType.getName(), e);
        }
        int size = node.getChildNodes().size();
        for (int i = 0; i < size; i++) {
            ListNode child = node.getChildAddressedByListNumber(i);
            Object item = NodeManager.getNodeData(child, elementType);
            ((List)resultList).add(item);
        }

        return resultList;
    }

    private static void setSetNode(Node node, Set<?> set, Class<?> elementType, FileStyle style) {
        int size = set.size();
        node.capChildCount(size);
        Iterator<?> iterator = set.iterator();
        for (int i = 0; i < size; i++) {
            NodeManager.setNodeData(node.getChildAddressedByListNumber(i), iterator.next(), elementType, style);
        }
    }

    private static Set<?> retrieveSet(Node node, Class<Set<?>> setType, Class<?> elementType) {
        Set<?> resultSet = null;
        try {
            resultSet = setType.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Cannot create Set of type " + setType.getName(), e);
        }

        int size = node.getChildNodes().size();
        for (int i = 0; i < size; i++) {
            ListNode child = node.getChildAddressedByListNumber(i);
            Object item = NodeManager.getNodeData(child, elementType);
            ((Set)resultSet).add(item);
        }

        return resultSet;
    }

    private static void setMapNode(Node node, Map<?, ?> map, Class<?> keyType, Class<?> valueType, FileStyle style, boolean forceArrayMode) {
        boolean keyIsBase = BaseTypes.isBaseType(keyType);

        if (keyIsBase && !forceArrayMode && !style.alwaysArrayDictionaries) {
            if (node.childNodeType != NodeChildrenType.key) {
                node.clearChildren(NodeChildrenType.key);
            }

            List<String> currentKeys = new ArrayList<>(map.size());
            for (Object key: map.keySet()) {
                Object value = map.get(key);
                String keyAsText = BaseTypes.serializeBaseType(key, keyType, style);

                if (!Utilities.isValidKey(keyAsText)) {
                    setMapNode(node, map, keyType, valueType, style, true);
                    return;
                }

                currentKeys.add(keyAsText);
                KeyNode child = node.getChildAddressedByName(keyAsText);
                NodeManager.setNodeData(child, value, valueType, style);
            }

            for (String key: node.getChildKeys()) {
                if (!currentKeys.contains(key)) {
                    node.removeChild(key);
                }
            }
        } else {
            if (node.childNodeType != NodeChildrenType.list) {
                node.clearChildren(NodeChildrenType.list);
            }

            Set<?> array = map.entrySet();
            NodeManager.setNodeData(node, array, array.getClass(), style);
        }
    }

    private static Map<?, ?> retrieveMap(Node node,  Class<Map<?, ?>> mapType, Class<?> keyType, Class<?> valueType) {
        boolean keyIsBase = BaseTypes.isBaseType(keyType);
        Map<?, ?> map;
        try {
            map = mapType.getConstructor(int.class).newInstance(node.getChildNodes().size());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Error while attempting to create map for retrieval destination", e);
        }
        if (keyIsBase && node.childNodeType == NodeChildrenType.key) {
            for (Node child : node.getChildNodes()) {
                String childKey = ((KeyNode) child).getKey();
                Object key = BaseTypes.parseBaseType(childKey, keyType);
                Object value = NodeManager.getNodeData(child, valueType);
            }
        } else {
            /*Set<Map.Entry<K, V>> array = NodeManager.getNodeData<Set<Entry<K, V>>>(node);
            for (Map.Entry<K, V> kvp: array) {
                map.put(kvp.getKey(), kvp.getValue());
            }*/
        }
        return map;
    }

    //Java specific code
    private static final Class<?>[] ARRAY_PRIMITIVE_TYPES = {
            int[].class, float[].class, double[].class, boolean[].class,
            byte[].class, short[].class, long[].class, char[].class };

    private static <T> Object[] getArray(T array, Class<T> arrayType){
        Object[] outputArray = null;

        for(Class<?> arrClass : ARRAY_PRIMITIVE_TYPES){
            if(arrayType.isAssignableFrom(arrClass)){
                int arrLength = Array.getLength(array);
                outputArray = new Object[arrLength];
                for(int i = 0; i < arrLength; ++i){
                    outputArray[i] = Array.get(array, i);
                }
                break;
            }
        }
        if(outputArray == null) // not primitive type array
            outputArray = (Object[])array;

        return outputArray;
    }

}