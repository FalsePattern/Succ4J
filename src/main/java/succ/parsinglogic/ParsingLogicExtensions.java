package succ.parsinglogic;

import falsepattern.reflectionhelper.ClassTree;

import java.lang.reflect.InvocationTargetException;

public class ParsingLogicExtensions {
    public static String quote(String s) {
        return '"' + s + '"';
    }

    public static boolean isQuoted(String s) {
        return s.length() > 1 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"';
    }

    public static String unQuote(String s) {
        return isQuoted(s) ? s.substring(1, s.length() - 1) : s;
    }

    public static int getIndentationLevel(String s) {
        if (s == null) {
            return 0;
        }
        char[] chars = s.toCharArray();
        int i = 0;
        for (; i < chars.length; i++) {
            if (chars[i] != ' ') {
                break;
            }
        }
        return i;
    }

    public static String addSpaces(String s, int count) {
        StringBuilder result = new StringBuilder(s);
        for (int i = 0; i < count; i++) {
            result.append(' ');
        }
        return result.toString();
    }

    public static String addIndent(String s, int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(' ');
        }
        return result.append(s).toString();
    }

    public static String[] splitIntoLines(String s) {
        return s.replace("\r\n", "\n") //windows line endings
                .replace("\r", "\n") //classic macOS line endings
                .split("\n", -1);
    }

    public static boolean containsNewLine(String s) {
        return s.contains("\n") || s.contains("\r");
    }

    public static boolean isWhitespace(String s) {
        return s.trim().length() == 0;
    }

    public static <T> T getDefaultValue(ClassTree<T> type) {
        try {
            return type.type.getConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            return null;
        }
    }
}
