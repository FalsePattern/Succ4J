package succ.parsinglogic.types;

import succ.Utilities;
import succ.parsinglogic.nodes.MultiLineStringNode;
import succ.parsinglogic.nodes.Node;
import succ.parsinglogic.nodes.NodeChildrenType;
import succ.style.EnumStyle;
import succ.style.FileStyle;

import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import static succ.parsinglogic.ParsingLogicExtensions.*;

/**
 * Manages SUCC's database of Base Types. https://github.com/JimmyCushnie/SUCC/wiki/Base-Types
 */
public class BaseTypes {

    @SuppressWarnings("unchecked")
    public static String serializeBaseType(Object thing, Class<?> type, FileStyle style) {
        if (baseSerializeMethods.containsKey(type)) {
            return baseSerializeMethods.get(type).serialize(thing);
        }
        if (baseStyledSerializeMethods.containsKey(type)) {
            return baseStyledSerializeMethods.get(type).serialize(thing, style);
        }
        if (type != null && type.isEnum()) {
            return serializeEnum((Enum<?>)thing, style);
        }

        throw new RuntimeException("Cannot serialize base type " + (type != null ? type.getName() : "null") + " - are you sure it is a base type?");
    }

    public static <T> void setBaseTypeNode(Node node, Object thing, Class<?> type, FileStyle style) {
        node.capChildCount(0);
        node.childNodeType = NodeChildrenType.none;
        node.setValue(serializeBaseType(thing, type, style));
    }

    /**
     * Turn some text into data, if that data is of a base type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseBaseType(String text, Class<T> type) {
        try {
            {
                if (baseParseMethods.containsKey(type)) {
                    return type.cast(baseParseMethods.get(type).parse(text));
                }
                if (type != null && type.isEnum()) {
                    return type.cast(parseEnum(text, type));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing text as type " + type.getName() + ": " + text, e);
        }

        throw new RuntimeException("Cannot parse base type " + (type != null ? type.getName() : "null") + " - are you sure it is a base type?");
    }

    /**
     * Returns true if the type is a base type.
     */
    public static <T> boolean isBaseType(Class<T> type) {
        return (type != null && type.isEnum())
                || baseSerializeMethods.containsKey(type)
                || baseStyledSerializeMethods.containsKey(type);
    }

    @FunctionalInterface
    public interface SerializeMethod {
        String serialize(Object thing);
    }
    @FunctionalInterface
    public interface ParseMethod {
        Object parse(String text);
    }
    @FunctionalInterface
    public interface StyledSerializeMethod {
        String serialize (Object thing, FileStyle style);
    }

    public static void addBaseType(Class<?> type, SerializeMethod serializeMethod, ParseMethod parseMethod) {
        if (isBaseType(type)) {
            throw new RuntimeException("Type " + type.getTypeName() + " is already a supported base type. You cannot re-add it.");
        }

        baseSerializeMethods.put(type, serializeMethod);
        baseParseMethods.put(type, parseMethod);
    }

    private static final Map<Class<?>, SerializeMethod> baseSerializeMethods = new HashMap<>();
    private static final Map<Class<?>, StyledSerializeMethod> baseStyledSerializeMethods = new HashMap<>();
    private static final Map<Class<?>, ParseMethod> baseParseMethods = new HashMap<>();



    private static final StyledSerializeMethod serializeString = (value, style) -> {
        String text = (String) value;
        if (text == null || text.length() == 0) {
            return "";
        }

        text = text.replace("\t", "    "); // SUCC files cannot contain tabs. Prevent saving strings with tabs in them.

        if (
                style.alwaysQuoteStrings
                || text.charAt(0) == ' ' || text.charAt(text.length() - 1) == ' '
                || isQuoted(text)
                || text.equals(Utilities.getNullIndicator())
        ) {
            text = quote(text);
        }
        return text;
    };

    private static final ParseMethod parseString = (text) -> isQuoted(text) ? unQuote(text) : text;

    public static void setStringSpecialCase(Node node, String value, FileStyle style) {
        if (value != null && (value.contains("\n") || value.contains("\r"))) {
            node.setValue(MultiLineStringNode.terminator);
            String[] lines = splitIntoLines(value);

            node.capChildCount(lines.length + 1);
            for (int i = 0; i < lines.length; i++) {
                MultiLineStringNode newNode = node.getChildAddressedByStringLineNumber(i);
                newNode.setValue(BaseTypes.serializeString.serialize(lines[i], style));
            }

            node.getChildAddressedByStringLineNumber(lines.length).makeTerminator();
        }
    }

    public static String parseSpecialStringCase(Node node) {
        StringBuilder text = new StringBuilder();
        List<Node> childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.size(); i++) {
            MultiLineStringNode line = (MultiLineStringNode) childNodes.get(i);

            if (i == childNodes.size() - 1) {
                if (line.isTerminator()) {
                    break;
                } else {
                    throw new IllegalStateException("Error parsing multi line string: the final child was not a terminator. Line so far was " + text);
                }
            }

            text.append(parseString.parse(line.getValue()));
            if (i != childNodes.size() - 2) {
                text.append(Utilities.getNewLine());
            }
        }

        return text.toString();
    }

    private static final SerializeMethod serializeInt = Object::toString;

    private static final SerializeMethod serializeDouble = (value) -> {
        Double val = (double) value;
        if (val.isInfinite()) {
            if (val < 0) {
                return "-infinity";
            } else {
                return "infinity";
            }
        }
        if (val.isNaN()) {
            return "nan";
        }

        NumberFormat format = new DecimalFormat();
        format.setMinimumIntegerDigits(0);
        format.setMinimumFractionDigits(0);
        format.setMaximumIntegerDigits(Integer.MAX_VALUE);
        format.setMaximumFractionDigits(Integer.MAX_VALUE);
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format((double)val);
    };

    private static final SerializeMethod serializeFloat = (value) -> serializeDouble.serialize((double)value);

    private static final ParseMethod parseLong = Long::parseLong;
    private static final ParseMethod parseInt = Integer::parseInt;
    private static final ParseMethod parseShort = Short::parseShort;
    private static final ParseMethod parseByte = Byte::parseByte;

    private static final ParseMethod parseFloat = (text) -> (float)parseFloatWithRationalSupport(text);
    private static final ParseMethod parseDouble = BaseTypes::parseFloatWithRationalSupport;

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

    private static final StyledSerializeMethod serializeBoolean =
            (value, style) -> (boolean)value ? trueStrings[style.boolStyle.ordinal()] : falseStrings[style.boolStyle.ordinal()];

    private static final ParseMethod parseBoolean = (text) -> {
        text = text.toLowerCase();
        if (Arrays.asList(trueStrings).contains(text)) return true;
        if (Arrays.asList(falseStrings).contains(text)) return false;
        throw new IllegalStateException("Cannot parse text as boolean: " + text);
    };

    private static final SerializeMethod serializeChar = String::valueOf;
    private static final ParseMethod parseChar = (value) -> value.charAt(0);

    private static final SerializeMethod serializeType = (type) -> ((Class<?>)type).getName();
    private static final ParseMethod parseType = (text) -> {
        try {
            return Class.forName(text);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + text, e);
        }
    };

    private static <T extends Enum<?>> String serializeEnum(T value, FileStyle style) {
        if (style.enumStyle == EnumStyle.number) {
            return Integer.toString(value.ordinal());
        } else {
            return value.name();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object parseEnum(String text, Type type) {
        try {
            if (type instanceof Class<?> && ((Class<?>) type).isEnum()) {
                return Enum.valueOf((Class<Enum>) type, text);
            } else {
                throw new IllegalArgumentException("Type " + type.getTypeName() + " is not an enum.");
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Enum " + type.getTypeName() + " has not value with name " + text);
        }
    }

    private static final SerializeMethod serializeTemporal = (value) -> {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
        return formatter.format((TemporalAccessor)value);
    };
    private static final ParseMethod parseTemporal = (text) -> {
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
        baseSerializeMethods.put(Class.class, serializeType);
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
        baseParseMethods.put(Class.class, parseType);

        baseParseMethods.put(Date.class, parseTemporal);
    }
}
