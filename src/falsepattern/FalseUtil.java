package falsepattern;

import falsepattern.os.OS;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class FalseUtil {

    static {
        switch (OS.type) {
            case WINDOWS:
                directorySeparatorChar = '\\';
                altDirectorySeparatorChar = '/';
                volumeSeparatorChar = ':';
                break;
            case OSX:
                directorySeparatorChar = ':';
                altDirectorySeparatorChar = '/';
                volumeSeparatorChar = ':';
                break;
            default: // Assuming unix-like paths
                System.err.println("Operating system type not recognized as Windows/OSX/Unix/Solaris. Assuming unix-style paths.");
            case UNIX:
            case SOLARIS:
                directorySeparatorChar = '/';
                altDirectorySeparatorChar = '\\';
                volumeSeparatorChar = '/';
                break;
        }
    }
    public static final char directorySeparatorChar;
    public static final char altDirectorySeparatorChar;
    public static final char volumeSeparatorChar;


    public static String changeExtension(String path, String extension) {
        if (path != null) {
            Paths.get(path);

            String s = path;
            for (int i = path.length(); --i >= 0;) {
                char ch = path.charAt(i);
                if (ch == '.') {
                    s = path.substring(0, i);
                    break;
                }
                if (ch == directorySeparatorChar || ch == altDirectorySeparatorChar || ch == volumeSeparatorChar) break;
            }
            StringBuilder result = new StringBuilder(s);
            if (extension != null && path.length() != 0) {
                if (extension.length() == 0 || extension.charAt(0) != '.') {
                    result.append('.');
                }
                result.append(extension);
            }
            return result.toString();
        }
        return null;
    }

    public static String getFileNameWithoutExtension(String path) {
        if (path != null) {
            Paths.get(path);

            String s = path;
            for (int i = path.length(); --i >= 0;) {
                char ch = path.charAt(i);
                if (ch == '.') {
                    s = path.substring(0, i);
                    break;
                }
                if (ch == directorySeparatorChar || ch == altDirectorySeparatorChar || ch == volumeSeparatorChar) break;
            }

            return s;
        }
        return null;
    }

    public static String trimEnd(String s) {
        char[] chars = s.toCharArray();
        int lastIndex = chars.length - 1;
        for (; lastIndex >= 0; lastIndex--) {
            if (chars[lastIndex] > '\u0020') {
                break;
            }
        }
        if (lastIndex == 0) {
            return "";
        } else if (lastIndex < chars.length - 1) {
            return s.substring(0, lastIndex + 1);
        } else {
            return s;
        }
    }

    public static String trimStart(String s) {
        char[] chars = s.toCharArray();
        int firstIndex = 0;
        for (; firstIndex < chars.length; firstIndex++) {
            if (chars[firstIndex] > '\u0020') {
                break;
            }
        }
        if (firstIndex == 0) {
            return s;
        } else if (firstIndex < chars.length - 1) {
            return s.substring(firstIndex);
        } else {
            return "";
        }
    }

}