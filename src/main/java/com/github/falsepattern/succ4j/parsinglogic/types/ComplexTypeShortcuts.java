package com.github.falsepattern.succ4j.parsinglogic.types;

import com.github.falsepattern.util.Out;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ComplexTypeShortcuts {

    public static <T> T getFromShortcut(String shortcut, Class<T> type) {
        Out<T> result = new Out<>(type);
        if (tryConstructorShortcut(shortcut, type, result)) return result.value;
        if (tryMethodShortcut(shortcut, type, result)) return result.value;
        if (tryCustomShortcut(shortcut, type, result)) return result.value;

        throw new IllegalArgumentException(shortcut + " is not a valid shortcut for type " + type.getName());
    }

    @SuppressWarnings("unchecked")
    private static <T> boolean tryConstructorShortcut(String shortcut, Class<T> type, Out<T> result) {
        result.value = null;
        try {
            if (shortcut.startsWith("(") && shortcut.endsWith(")")) {
                String text = shortcut.substring(1, shortcut.length() - 1); // remove the ( and )
                String[] paramStrings = text.split(",");

                Constructor<T>[] constructors = (Constructor<T>[]) type.getConstructors();
                Constructor<T> matchingConstructor = null;
                if (constructors.length > 1) {
                    outer:
                    for (Constructor<T> c: constructors) {
                        if (c.getParameterCount() == paramStrings.length) {
                            Class<?>[] paramTypes = c.getParameterTypes();
                            for (int i = 0; i < paramTypes.length; i++) {
                                Class<?> paramType = paramTypes[i];
                                String paramString = paramStrings[i];
                                if (BaseTypes.isBaseType(paramType)) {
                                    try {
                                        BaseTypes.parseBaseType(paramString, paramType);
                                    } catch (ClassCastException e) {
                                        continue outer;
                                    }
                                } else {
                                    continue outer;
                                }
                            }
                            matchingConstructor = c;
                            break;
                        }
                    }
                }
                if (matchingConstructor != null) {
                    Class<?>[] paramTypes = matchingConstructor.getParameterTypes();
                    Object[] params = new Object[paramStrings.length];
                    for (int i = 0; i < params.length; i++) {
                        params[i] = BaseTypes.parseBaseType(paramStrings[i], paramTypes[i]);
                    }
                    result.value = matchingConstructor.newInstance(params);
                    return true;
                }
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException ignored) { }
        result.value = null;
        return false;
    }

    private static <T> boolean tryMethodShortcut(String shortcut, Class<T> type, Out<T> result) {
        try {
            if (shortcut.contains("(") && shortcut.contains(")")) {
                String methodName = shortcut.substring(0, shortcut.indexOf('('));
                String[] paramStrings = shortcut.substring(shortcut.indexOf('(') + 1, shortcut.indexOf(')')).split(",");
                Method[] allMethods = type.getMethods();
                List<Method> matchingMethods = new ArrayList<>();
                for (Method m: allMethods) {
                    int mod = m.getModifiers();
                    if (m.getName().equals(methodName)
                            && Modifier.isPublic(mod)
                            && Modifier.isStatic(mod)
                            && m.getReturnType().equals(type)
                            && m.getParameterCount() == paramStrings.length) {
                        matchingMethods.add(m);
                    }
                }

                outer:
                for (Method m: matchingMethods) {
                    Class<?>[] paramTypes = m.getParameterTypes();
                    Object[] params = new Object[paramTypes.length];
                    for (int i = 0; i < paramTypes.length; i++) {
                        Class<?> paramType = paramTypes[i];
                        String paramString = paramStrings[i];
                        Object param;
                        if (BaseTypes.isBaseType(paramType)) {
                            try {
                                param = BaseTypes.parseBaseType(paramString, paramType);
                            } catch (ClassCastException e) {
                                continue outer;
                            }
                            params[i] = param;
                        } else {
                            continue outer;
                        }
                    }
                    result.value = type.cast(m.invoke(null, params));
                    return true;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
        result.value = null;
        return false;
    }

    private static <T> boolean tryCustomShortcut(String shortcut, Class<T> type, Out<T> result) {
        try {
            Method m = type.getMethod("Shortcut", String.class);
            if (m.getReturnType().equals(type)) {
                result.value = type.cast(m.invoke(null, shortcut));
                return true;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}

        result.value = null;
        return false;
    }
}
