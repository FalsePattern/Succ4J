package succ.parsinglogic.types;

import falsepattern.Out;
import succ.style.FileStyle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 * Manages SUCC's database of Base Types. https://github.com/JimmyCushnie/SUCC/wiki/Base-Types
 */
public class BaseTypes {
    public static synchronized <T> String serializeBaseType(T thing, FileStyle style) {
        return serializeBaseType(thing, thing.getClass(), style);
    }

    @SuppressWarnings("unchecked")
    public static synchronized <T> String serializeBaseType(T thing, Class<? extends T> type, FileStyle style) {
        if (baseSerializeMethods.containsKey(type)) {
            return ((SerializeMethod<T>)baseSerializeMethods.get(type)).serialize(thing);
        }
        if (baseStyledSerializeMethods.containsKey(type)) {
            return ((StyledSerializeMethod<T>)baseStyledSerializeMethods.get(type)).serialize(thing, style);
        }
        if (type.isEnum()) {
            return serializeEnum((Enum<?>)thing, style);
        }

        throw new RuntimeException("Cannot serialize base type " + type.getName() + " - are you sure it is a base type?");
    }

    public static synchronized void setBaseTypeNode(Node node, Object thing, Class<?> type, FileStyle style) {
        node.capChildCount(0);
        node.childNodeType = NodeChildrenType.none;
        node.value = serializeBaseType(thing, type, style);
    }

    /**
     * Turn some text into data, if that data is of a base type.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T parseBaseType(String text, Class<T> type) {
        try {
            {
                Out<Method> method = new Out<>(Method.class);
                if (baseParseMethods.containsKey(type)) {
                    return (T)method.value.invoke(null, text);
                }
                if (type.isEnum()) {
                    return parseEnum(text, type);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing text as type " + type.getName() + ": " + text, e);
        }

        throw new RuntimeException("Cannot parse base type " + type.getName() + " - are you sure it is a base type?");
    }

    /**
     * Returns true if the type is a base type.
     */
    public static synchronized boolean isBaseType(Class<?> type) {
        return type.isEnum()
                || baseSerializeMethods.containsKey(type)
                || baseStyledSerializeMethods.containsKey(type);
    }

    public interface CustomMethod<T>{}

    @FunctionalInterface
    public interface SerializeMethod<T> extends CustomMethod<T> {
        String serialize(T thing);
    }
    @FunctionalInterface
    public interface ParseMethod<T> extends CustomMethod<T> {
        T parse(String text);
    }
    @FunctionalInterface
    public interface StyledSerializeMethod<T> extends CustomMethod<T>{
        String serialize (T thing, FileStyle style);
    }

    public static <T> void addBaseType(Class<T> type, SerializeMethod<T> serializeMethod, ParseMethod<T> parseMethod) {
        if (isBaseType(type)) {
            throw new RuntimeException("Type " + type.getName() + " is already a supported base type. You cannot re-add it.");
        }

        baseSerializeMethods.put(type, serializeMethod);
        baseParseMethods.put(type, parseMethod);
    }

    private static final BaseTypeConversionHashMap<SerializeMethod<?>> baseSerializeMethods = new BaseTypeConversionHashMap<>();
    private static final BaseTypeConversionHashMap<StyledSerializeMethod<?>> baseStyledSerializeMethods = new BaseTypeConversionHashMap<>();
    private static final BaseTypeConversionHashMap<ParseMethod<?>> baseParseMethods = new BaseTypeConversionHashMap<>();



    private static final StyledSerializeMethod<String> serializeString = (text, style) -> {
        if (text == null || text.length() == 0) {
            return "";
        }

        text = text.replace("\t", "    "); // SUCC files cannot contain tabs. Prevent saving strings with tabs in them.

        if (
                style.alwaysQuoteStrings
                || text.charAt(0) == ' ' || text.charAt(text.length() - 1) == ' '
                || (text.startsWith("\"") && text.endsWith("\""))
                || text.equals(Utilities.nullIndicator)
        ) {
            text = "\"" + text + "\"";
        }
        return text;
    };

    private static final ParseMethod<String> parseString = (text) -> {
        if (
                text.length() > 1
                && text.charAt(0) == '"' && text.charAt(text.length() - 1) == '"'
        ) {
            text = text.substring(1, text.length() - 2);
        }

        return text;
    };

    public static void setStringSpecialCase(Node node, String value, FileStyle style) {
        if (value != null && (value.contains("\n") || value.contains("\r"))) {
            node.value = MultiLineStringNode.terminator;
            String[] lines = value.replace("\r\n", "\n").replace("\r", "\n").split("\n");

            node.capChildCount(lines.length + 1);
            for (int i = 0; i < lines.length; i++) {
                var newNode = node.getChildAddressedByStringLineNumber(i);
                newNode.value = BaseTypes.serializeString.serialize(lines[i], style);
            }

            node.getChildAddressedByStringLineNumber(lines.length).makeTerminator();
        }
    }

    public static String parseSpecialStringCase(Node node) {
        String text = "";

        for (int i = 0; i < node.childNodes.count; i++) {
            MultiLineStringNode line = (MultiLineStringNode) node.childNodes[i];

            if (i == node.childNodes.count - 1) {
                if (line.isTerminator) {
                    break;
                } else {
                    throw new IllegalStateException("Error parsing multi line string: the final child was not a terminator. Line so far was " + text);
                }
            }

            text += parseString.parse(line.value);
            if (i != node.childNodes.count - 2) {
                text += Utilities.newLine;
            }
        }

        return text;
    }

    private static final SerializeMethod<?> serializeInt = Object::toString;

    private static final SerializeMethod<Double> serializeDouble = (value) -> {
        if (value.isInfinite()) {
            if (value < 0) {
                return "-infinity";
            } else {
                return "infinity";
            }
        }
        if (value.isNaN()) {
            return "nan";
        }

        NumberFormat format = new DecimalFormat();
        format.setMinimumIntegerDigits(0);
        format.setMinimumFractionDigits(0);
        format.setMaximumIntegerDigits(Integer.MAX_VALUE);
        format.setMaximumFractionDigits(Integer.MAX_VALUE);
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format((double)value);
    };

    private static final SerializeMethod<Float> serializeFloat = (value) -> serializeDouble.serialize((double)value);

    private static final ParseMethod<Long> parseLong = Long::parseLong;
    private static final ParseMethod<Integer> parseInt = Integer::parseInt;
    private static final ParseMethod<Short> parseShort = Short::parseShort;
    private static final ParseMethod<Byte> parseByte = Byte::parseByte;

    private static final ParseMethod<Float> parseFloat = (text) -> (float)parseFloatWithRationalSupport(text);
    private static final ParseMethod<Double> parseDouble = BaseTypes::parseFloatWithRationalSupport;

    private static double parseFloatWithRationalSupport(String text) {
        if (text.contains("/")) {
            double[] numbers = Arrays.stream(text.split("/")).mapToDouble(Double::parseDouble).toArray();
            double result = numbers[0];
            for (int i = 1; i < numbers.length; i++) {
                result /= numbers[i];
            }

            return result;
        }
        text = text.toLowerCase().trim();
        switch (text) {
            case "infinity":
                return Double.POSITIVE_INFINITY;
            case "-infinity":
                return Double.NEGATIVE_INFINITY;
            case "nan":
                return Double.NaN;
            default:
                return Double.parseDouble(text);
        }
    }

    private static final String[] trueStrings = new String[] {"true", "on", "yes", "y"};
    private static final String[] falseStrings = new String[] {"false", "off", "no", "n"};

    private static final StyledSerializeMethod<Boolean> serializeBoolean =
            (value, style) -> value ? trueStrings[style.boolStyle.ordinal()] : falseStrings[style.boolStyle.ordinal()];

    private static final ParseMethod<Boolean> parseBoolean = (text) -> {
        text = text.toLowerCase();
        if (Arrays.asList(trueStrings).contains(text)) return true;
        if (Arrays.asList(falseStrings).contains(text)) return false;
        throw new IllegalStateException("Cannot parse text as boolean: " + text);
    };

    private static final SerializeMethod<Character> serializeChar = String::valueOf;
    private static final ParseMethod<Character> parseChar = (value) -> value.charAt(0);

    private static final SerializeMethod<Class<?>> serializeClass = Class::getName;
    private static final ParseMethod<Class<?>> parseClass = (text) -> {
        try {
            return Class.forName(text);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot parse text as java.lang.Class: " + text, e);
        }
    };

    private static <T extends Enum<?>> String serializeEnum(T value, FileStyle style) {
        switch (style.enumStyle) {
            default:
                return value.name();
            case number:
                return Integer.toString(value.ordinal());
        }
    }

    private static <T extends Enum<T>> T parseEnum(String text, Class<T> type) {
        return Enum.valueOf(type, text);
    }

    private static final SerializeMethod<TemporalAccessor> serializeTemporal = (value) -> {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
        return formatter.format(value);
    };
    private static final ParseMethod<TemporalAccessor> parseTemporal = (text) -> {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
        return formatter.parse(text);
    };

    static {


        // Integer types
        baseSerializeMethods.put(long.class, serializeInt);
        baseSerializeMethods.put(int.class, serializeInt);
        baseSerializeMethods.put(short.class, serializeInt);
        baseSerializeMethods.put(byte.class, serializeInt);
        baseSerializeMethods.put(Long.class, serializeInt);
        baseSerializeMethods.put(Integer.class, serializeInt);
        baseSerializeMethods.put(Short.class, serializeInt);
        baseSerializeMethods.put(Byte.class, serializeInt);

        // Floating point types
        baseSerializeMethods.put(float.class, serializeFloat);
        baseSerializeMethods.put(double.class, serializeDouble);
        baseSerializeMethods.put(Float.class, serializeFloat);
        baseSerializeMethods.put(Double.class, serializeDouble);

        // Misc
        baseSerializeMethods.put(char.class, serializeChar);
        baseSerializeMethods.put(Character.class, serializeChar);
        baseSerializeMethods.put(Class.class, serializeClass);
        baseSerializeMethods.put(Date.class, serializeTemporal);

        baseStyledSerializeMethods.put(String.class, serializeString);
        baseStyledSerializeMethods.put(boolean.class, serializeBoolean);
        baseStyledSerializeMethods.put(Boolean.class, serializeBoolean);

        baseParseMethods.put(String.class, parseString);

        // Integer types
        baseParseMethods.put(long.class, parseLong);
        baseParseMethods.put(int.class, parseInt);
        baseParseMethods.put(short.class, parseShort);
        baseParseMethods.put(byte.class, parseByte);
        baseParseMethods.put(Long.class, parseLong);
        baseParseMethods.put(Integer.class, parseInt);
        baseParseMethods.put(Short.class, parseShort);
        baseParseMethods.put(Byte.class, parseByte);

        // Floating point types
        baseParseMethods.put(float.class, parseFloat);
        baseParseMethods.put(double.class, parseDouble);
        baseParseMethods.put(Float.class, parseFloat);
        baseParseMethods.put(Double.class, parseDouble);

        // Misc
        baseParseMethods.put(boolean.class, parseBoolean);
        baseParseMethods.put(char.class, parseChar);
        baseParseMethods.put(Boolean.class, parseBoolean);
        baseParseMethods.put(Character.class, parseChar);
        baseParseMethods.put(Class.class, parseClass);

        baseParseMethods.put(Date.class, parseTemporal);
    }
}
