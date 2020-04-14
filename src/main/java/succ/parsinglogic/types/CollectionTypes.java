package succ.parsinglogic.types;

import falsepattern.reflectionhelper.ClassTree;
import succ.Utilities;
import succ.parsinglogic.NodeManager;
import succ.parsinglogic.nodes.KeyNode;
import succ.parsinglogic.nodes.ListNode;
import succ.parsinglogic.nodes.Node;
import succ.parsinglogic.nodes.NodeChildrenType;
import succ.style.FileStyle;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CollectionTypes {
    public static boolean trySetCollection(Node node, Object data, ClassTree<?> collectionType, FileStyle style) {
        if (collectionType.type == null) {
            return false;
        } else if (collectionType.type.isArray()) {
            setArrayNode(node, data, collectionType.type, style);
        } else if (List.class.isAssignableFrom(collectionType.type)) {
            setListNode(node, (List<?>) data, collectionType, style);
        } else if (Set.class.isAssignableFrom(collectionType.type)) {
            setSetNode(node, (Set<?>) data, collectionType, style);
        } else if (Map.class.isAssignableFrom(collectionType.type)) {
            setMapNode(node, (Map<?, ?>) data, collectionType, style, false);
        } else if (node.childNodeType == NodeChildrenType.list) {
            throw new IllegalArgumentException(collectionType.toString() + " is not a supported collection type");
        } else {
            return false;
        }

        return true;
    }

    public static Object tryGetCollection(Node node, ClassTree<?> collectionType) {
        Object data = null;
        if (collectionType.type.isArray()) {
            data = retrieveArray(node, collectionType.type);
        } else if (List.class.isAssignableFrom(collectionType.type)) {
            data = retrieveList(node, collectionType);
        } else if (Set.class.isAssignableFrom(collectionType.type)) {
            data = retrieveSet(node, collectionType);
        } else if (Map.class.isAssignableFrom(collectionType.type)) {
            data = retrieveMap(node, collectionType);
        } else if (node.childNodeType == NodeChildrenType.list) {
            throw new IllegalArgumentException(collectionType.toString() + " is not a supported collection type");
        }

        return data;
    }

    private static void setArrayNode(Node node, Object array, Class<?> arrayType, FileStyle style) {
        Class<?> elementType = arrayType.getComponentType();

        Object[] boi = getArray(array, arrayType);

        node.capChildCount(boi.length);

        for (int i = 0; i < boi.length; i++) {
            NodeManager.setNodeData(node.getChildAddressedByListNumber(i), boi[i], ClassTree.parseFromString(elementType.getTypeName()), style);
        }

    }

    private static Object retrieveArray(Node node, Class<?> arrayType) {
        Class<?> elementType = arrayType.getComponentType();
        int length = node.getChildNodes().size();
        Object array = Array.newInstance(elementType, length);
        for (int i = 0; i < length; i++) {
            ListNode child = node.getChildAddressedByListNumber(i);
            Object element = NodeManager.getNodeData(child, ClassTree.parseFromString(elementType.getTypeName()));
            Array.set(array, i, element);
        }

        return array;
    }

    private static void setListNode(Node node, List<?> list, ClassTree<?> listType, FileStyle style) {
        int size = list.size();
        node.capChildCount(size);
        for (int i = 0; i < size; i++) {
            NodeManager.setNodeData(node.getChildAddressedByListNumber(i), list.get(i), listType.getChildren().get(0), style);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static  List<?> retrieveList(Node node, ClassTree<?> listType) {
        List<?> resultList;
        try {
            resultList = (List<?>)listType.type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Cannot create List of type " + listType.toString(), e);
        }
        int size = node.getChildNodes().size();
        for (int i = 0; i < size; i++) {
            ListNode child = node.getChildAddressedByListNumber(i);
            Object item = NodeManager.getNodeData(child, listType.getChildren().get(0));
            ((List)resultList).add(item);
        }

        return resultList;
    }

    private static void setSetNode(Node node, Set<?> set, ClassTree<?> setType, FileStyle style) {
        int size = set.size();
        node.capChildCount(size);
        Iterator<?> iterator = set.iterator();
        for (int i = 0; i < size; i++) {
            NodeManager.setNodeData(node.getChildAddressedByListNumber(i), iterator.next(), setType.getChildren().get(0), style);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Set<?> retrieveSet(Node node, ClassTree<?> setType) {
        Set<?> resultSet;
        try {
            resultSet = (Set<?>)setType.type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Cannot create Set of type " + setType.toString(), e);
        }

        int size = node.getChildNodes().size();
        for (int i = 0; i < size; i++) {
            ListNode child = node.getChildAddressedByListNumber(i);
            Object item = NodeManager.getNodeData(child, setType.getChildren().get(0));
            ((Set)resultSet).add(item);
        }

        return resultSet;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void setMapNode(Node node, Map<?, ?> map, ClassTree<?> mapTree, FileStyle style, boolean forceArrayMode) {
        ClassTree<?> keyType = mapTree.getChildren().get(0);
        ClassTree<?> valueType = mapTree.getChildren().get(1);
        boolean keyIsBase = BaseTypes.isBaseType(keyType.type);

        if (keyIsBase && !forceArrayMode && !style.alwaysArrayDictionaries) {
            if (node.childNodeType != NodeChildrenType.key) {
                node.clearChildren(NodeChildrenType.key);
            }
            List<String> currentKeys = new ArrayList<>(map.size());
            if (mapTree.getChildren().size() >= 3) {
                //Special case - it's a list-based map
                ClassTree<?> valueType2 = mapTree.getChildren().get(2);
                String keyKey = BaseTypes.serializeBaseType("key", String.class, style);
                String valueKey = BaseTypes.serializeBaseType("value", String.class, style);
                KeyNode keyChild = node.getChildAddressedByName(keyKey);
                KeyNode valueChild = node.getChildAddressedByName(valueKey);
                currentKeys.add(keyKey);
                currentKeys.add(valueKey);
                NodeManager.setNodeData(keyChild, map.get("key"), valueType, style);
                NodeManager.setNodeData(valueChild, map.get("value"), valueType2, style);
            } else {
                for (Object key : map.keySet()) {
                    Object value = map.get(key);
                    String keyAsText = BaseTypes.serializeBaseType(key, keyType.type, style);

                    if (!Utilities.isValidKey(keyAsText)) {
                        setMapNode(node, map, mapTree, style, true);
                        return;
                    }

                    currentKeys.add(keyAsText);
                    KeyNode child = node.getChildAddressedByName(keyAsText);
                    NodeManager.setNodeData(child, value, valueType, style);
                }
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

            Set<? extends Map.Entry<?, ?>> entries = map.entrySet();
            Set<HashMap<String, ?>> resultSet = new HashSet<>();
            entries.forEach((entry) -> {
                HashMap<String, ?> result = new HashMap<>();
                ((Map)result).put("key", entry.getKey());
                ((Map)result).put("value", entry.getValue());
                resultSet.add(result);
            });
            String setTypeString = "java.util.HashSet<java.util.HashMap<java.lang.String" +
                    ", " + keyType.toString() + ", " + valueType.toString() + ">>";
            NodeManager.setNodeData(node, resultSet, ClassTree.parseFromString(setTypeString), style);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Map<?, ?> retrieveMap(Node node, ClassTree<?> mapTree) {
        ClassTree<?> keyType = mapTree.getChildren().get(0);
        ClassTree<?> valueType = mapTree.getChildren().get(1);
        boolean keyIsBase = BaseTypes.isBaseType(keyType.type);
        Map<?, ?> map;
        try {
            map = (Map<?, ?>)mapTree.type.getConstructor(int.class).newInstance(node.getChildNodes().size());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Error while attempting to create map for retrieval destination", e);
        }
        if (keyIsBase && node.childNodeType == NodeChildrenType.key) {
            if (mapTree.getChildren().size() >= 3) {
                //Special case - it's a list-based map
                ClassTree<?> valueType2 = mapTree.getChildren().get(2);
                Object finalKey = null;
                Object finalValue = null;
                for (Node child: node.getChildNodes()) {
                    String childKey = ((KeyNode) child).getKey();
                    String key = BaseTypes.parseBaseType(childKey, String.class);
                    if (key.equals("key") && finalKey == null) {
                        finalKey = NodeManager.getNodeData(child, valueType);
                    } else if (key.equals("value") && finalValue == null) {
                        finalValue = NodeManager.getNodeData(child, valueType2);
                    } else {
                        throw new RuntimeException("Broken config file: list-based map with illegal entry detected!");
                    }
                }
                ((Map)map).put("key", finalKey);
                ((Map)map).put("value", finalValue);
            } else {
                for (Node child : node.getChildNodes()) {
                    String childKey = ((KeyNode) child).getKey();
                    Object key = BaseTypes.parseBaseType(childKey, keyType.type);
                    Object value = NodeManager.getNodeData(child, valueType);
                    ((Map) map).put(key, value);
                }
            }
        } else {

            String setTypeString = "java.util.HashSet<java.util.HashMap<java.lang.String" +
                    ", " + keyType.toString() + ", " + valueType.toString() + ">>";
            Set<Map<?, ?>> array = (Set<Map<?, ?>>) NodeManager.getNodeData(node, ClassTree.parseFromString(setTypeString));
            if (array != null) {
                for (Map<?, ?> kvp : array) {
                    ((Map)map).put(kvp.get("key"), kvp.get("value"));
                }
            }
        }
        return map;
    }

    //Java specific code
    private static final Class<?>[] ARRAY_PRIMITIVE_TYPES = {
            int[].class, float[].class, double[].class, boolean[].class,
            byte[].class, short[].class, long[].class, char[].class };

    private static Object[] getArray(Object array, Class<?> arrayType){
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
