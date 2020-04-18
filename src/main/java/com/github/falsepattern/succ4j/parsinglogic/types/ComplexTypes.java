package com.github.falsepattern.succ4j.parsinglogic.types;

import com.github.falsepattern.util.reflectionhelper.ClassTree;
import com.github.falsepattern.succ4j.parsinglogic.NodeManager;
import com.github.falsepattern.succ4j.parsinglogic.nodes.KeyNode;
import com.github.falsepattern.succ4j.parsinglogic.nodes.Node;
import com.github.falsepattern.succ4j.style.FileStyle;
import com.github.falsepattern.succ4j.parsinglogic.DoSave;
import com.github.falsepattern.succ4j.parsinglogic.DontSave;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComplexTypes {
    public static void setComplexNode(Node node, Object item, ClassTree<?> type, FileStyle style) {
        // clear the shortcut if there is any
        if (!(node.getValue() == null || node.getValue().isEmpty())) {
            node.setValue("");
        }

        for (Field f: getValidFields(type.type)) {
            KeyNode child = node.getChildAddressedByName(f.getName());
            try {
                NodeManager.setNodeData(child, f.get(item), ClassTree.parseFromField(f), style);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error while reading field " + f.getName() + " from type " + type.toString(), e);
            }
        }
    }

    public static <T> T retrieveComplexType(Node node, ClassTree<T> type) {
        T returnThis;
        try {
            returnThis = type.type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Error while trying to instantiate " + type.toString(), e);
        }
        for (Field f: getValidFields(type.type)) {
            if (!node.containsChildNode(f.getName())) continue;

            KeyNode child = node.getChildAddressedByName(f.getName());
            Object data = NodeManager.getNodeData(child, ClassTree.parseFromField(f));
            try {
                f.set(returnThis, data);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error while trying to set field " + f.getName() + " in type " + type.toString(), e);
            }
        }

        return returnThis;
    }

    public static List<Field> getValidFields(Class<?> type) {
        List<Field> validFields = new ArrayList<>();

        Field[] allFields = getFieldsUpTo(type, Object.class).toArray(new Field[0]);
        for (Field f: allFields) {
            int mod = f.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) continue;
            if (f.isAnnotationPresent(DontSave.class)) continue;
            if (Modifier.isPrivate(mod) && !f.isAnnotationPresent(DoSave.class)) continue;
            f.setAccessible(true);
            validFields.add(f);
        }

        return validFields;
    }

    private static List<Field> getFieldsUpTo(Class<?> startClass,
                                                Class<?> rootParent) {
        List<Field> currentClassFields = new ArrayList<>(Arrays.asList(startClass.getDeclaredFields()));
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null && (isSubClass(parentClass, rootParent) || parentClass.equals(rootParent))) {
            List<Field> parentClassFields = getFieldsUpTo(parentClass, rootParent);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }

    private static boolean isSubClass(Class<?> child, Class<?> parent) {
        if (parent == null) {
            return true;
        }
        try {
            child.asSubclass(parent);
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
