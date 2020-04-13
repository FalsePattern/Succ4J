package succ.parsinglogic.types;

import succ.parsinglogic.DoSave;
import succ.parsinglogic.DontSave;
import succ.parsinglogic.NodeManager;
import succ.parsinglogic.nodes.KeyNode;
import succ.parsinglogic.nodes.Node;
import succ.style.FileStyle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ComplexTypes {
    public static void setComplexNode(Node node, Object item, Class<?> type, FileStyle style) {
        // clear the shortcut if there is any
        if (!(node.getValue() == null || node.getValue().isEmpty())) {
            node.setValue("");
        }

        for (Field f: getValidFields(type)) {
            KeyNode child = node.getChildAddressedByName(f.getName());
            try {
                NodeManager.setNodeData(child, f.get(item), f.getType(), style);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error while reading field " + f.getName() + " from type " + type.getName(), e);
            }
        }
    }

    public static Object retrieveComplexType(Node node, Class<?> type) {
        Object returnThis;
        try {
            returnThis = type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Error while trying to instantiate " + type.getName(), e);
        }
        for (Field f: getValidFields(type)) {
            if (!node.containsChildNode(f.getName())) continue;

            KeyNode child = node.getChildAddressedByName(f.getName());
            Object data = NodeManager.getNodeData(child, f.getType());
            try {
                f.set(returnThis, data);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error while trying to set field " + f.getName() + " in type " + type.getName(), e);
            }
        }

        return returnThis;
    }

    public static List<Field> getValidFields(Class<?> type) {
        List<Field> validFields = new ArrayList<>();

        Field[] allFields = type.getFields();
        for (Field f: allFields) {
            int mod = f.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) continue;
            if (f.isAnnotationPresent(DontSave.class)) continue;
            if (Modifier.isPrivate(mod) && !f.isAnnotationPresent(DoSave.class)) continue;

            validFields.add(f);
        }

        return validFields;
    }
}
