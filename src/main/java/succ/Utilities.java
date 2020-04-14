package succ;

import falsepattern.Out;
import succ.style.LineEndingStyle;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static succ.style.LineEndingStyle.*;

public class Utilities {
    private static String defaultPath = getDefaultDefaultPath();

    /**
     * The path that DataFile locations will be relative to if you assign them a non-absolute path.
     * By default this is System.getProperty("user.dir"), but you can change it if you like.
     */
    public static String getDefaultPath() {
        return defaultPath;
    }

    /**
     * The path that DataFile locations will be relative to if you assign them a non-absolute path.
     * By default this is System.getProperty("user.dir"), but you can change it if you like.
     */
    public static void setDefaultPath(String value) {
        if (!Paths.get(value).isAbsolute()) {
            throw new IllegalArgumentException("When setting a custom default path, you must set an absolute path. The path " + value + " is not absolute.");
        }
        defaultPath = value;
    }

    private static String getDefaultDefaultPath() {
        return System.getProperty("user.dir");
    }

    public static final String fileExtension = ".succ";

    public static String absolutePath(String relativeOrAbsolutePath) {
        Path path = Paths.get(relativeOrAbsolutePath);
        if (!path.isAbsolute()) {
            if (defaultPath == null) {
                throw new IllegalArgumentException("You can't use relative paths unless you've set a DefaultPath. Path " + relativeOrAbsolutePath + " was not absolute");
            }
            return Paths.get(defaultPath, relativeOrAbsolutePath).toString();
        } else {
            return relativeOrAbsolutePath;
        }
    }

    public static boolean succFileExists(String relativeOrAbsolutePath) {
        String path = absolutePath(relativeOrAbsolutePath);
        return Files.exists(Paths.get(path));
    }

    public static boolean isValidKey(String potentialKey) {
        return isValidKey(potentialKey, null);
    }

    public static boolean isValidKey(String potentialKey, Out<String> whyNot) {
        String reason = null;
        if (potentialKey == null || potentialKey.isEmpty()) {
            reason = "SUCC keys must contain at least one character";
        } else if (potentialKey.charAt(0) == '-') {
            reason = "SUCC keys may not begin with the character '-'";
        } else if (potentialKey.contains(":")) {
            reason = "SUCC keys may not contain the character ':'";
        } else if (potentialKey.contains("#")) {
            reason = "SUCC keys may not contain the character '#'";
        } else if (potentialKey.charAt(0) == ' ' || potentialKey.charAt(potentialKey.length() - 1) == ' ') {
            reason = "SUCC keys may not start or end with a space";
        }

        return whyNot != null ? (whyNot.value = reason) == null : reason == null;
    }

    public static LineEndingStyle lineEndingStyle = PlatformDefault;

    public static String getNewLine() {
        switch (lineEndingStyle) {
            case Unix:
                return "\n";
            case Windows:
                return "\r\n";
            case PlatformDefault: default:
                return System.getProperty("line.separator");
        }
    }

    public static String getNullIndicator() {
        return "null";
    }

}
