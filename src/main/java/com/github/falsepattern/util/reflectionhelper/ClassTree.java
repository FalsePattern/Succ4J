package com.github.falsepattern.util.reflectionhelper;

import com.github.falsepattern.util.FalseUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ClassTree<T> {
  private final List<ClassTree<?>> children;
  public Class<T> type;
  public ClassTree(Class<T> type) {
    children = new ArrayList<>();
    this.type = type;
  }

  public ClassTree<?> addChildNode(Class<?> type) {
    ClassTree<?> child = new ClassTree<>(type);
    children.add(child);
    return child;
  }

  public List<ClassTree<?>> getChildren() {
    return Collections.unmodifiableList(children);
  }

  public String toString() {
    return type.getName() + (children.size() > 0 ? "<" + children.stream().map(ClassTree::toString).collect(Collectors.joining(", ")) + ">" : "");
  }

  public static ClassTree<?> parseFromFieldName(Class<?> clazz, String fieldName) throws NoSuchFieldException {
    return parseFromField(clazz.getDeclaredField(fieldName));
  }

  public static ClassTree<?> parseFromField(Field field) {
    return parseFromString(field.getGenericType().getTypeName());
  }

  public static ClassTree<?> parseFromString(String genericTypeString) {
    if (FalseUtil.countChar(genericTypeString, '<') != FalseUtil.countChar(genericTypeString, '>')) {
      throw new RuntimeException("Unbalanced angle brackets in type " + genericTypeString);
    }
    Stack<ClassTree<?>> stack = new Stack<>();
    stack.push(new ClassTree<>(Object.class));
    String formatted = genericTypeString
            .replace("<", "\\<")
            .replace(">", "\\>")
            .replace(",", "\\,")
            .replace(" ", "");

    String[] parts = formatted.split("\\\\");
    try {
      for (String part : parts) {
        if (primitiveToBoxClass.containsKey(part)) {
          part = primitiveToBoxClass.get(part);
        }
        if (part.contains("[]")) { //TODO hardcoded stuff, make it safer
          if (part.startsWith("<") || part.startsWith(",")) {
            part = part.substring(1);
          }
          String endText = part.startsWith("long") ? "J"
                  : part.startsWith("int") ? "I"
                  : part.startsWith("short") ? "S"
                  : part.startsWith("byte") ? "B"
                  : part.startsWith("float") ? "F"
                  : part.startsWith("double") ? "D"
                  : part.startsWith("boolean") ? "Z"
                  : "L" + part.substring(0, part.indexOf('['));
          StringBuilder result = new StringBuilder();
          while (part.contains("[]")) {
            part = part.replaceFirst("\\[]", "");
            result.append("[");
          }
          result.append(endText);
          stack.push(stack.peek().addChildNode(Class.forName(result.toString())));
        } else if (!(part.contains(",") || part.contains(">"))) {
          stack.push(stack.peek().addChildNode(Class.forName(part.startsWith("<") ? part.substring(1) : part)));
        } else if (part.startsWith(",")) {
          stack.pop();
          stack.push(stack.peek().addChildNode(Class.forName(part.substring(1))));
        } else if (part.contains(">")) {
          stack.pop();
        }
      }
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    return stack.pop();
  }

  private static final Map<String, String> primitiveToBoxClass = new HashMap<>();
  static {
    primitiveToBoxClass.put("int", Integer.class.getName());
    primitiveToBoxClass.put("long", Long.class.getName());
    primitiveToBoxClass.put("short", Short.class.getName());
    primitiveToBoxClass.put("byte", Byte.class.getName());
    primitiveToBoxClass.put("float", Float.class.getName());
    primitiveToBoxClass.put("double", Double.class.getName());
    primitiveToBoxClass.put("boolean", Boolean.class.getName());
  }
}
