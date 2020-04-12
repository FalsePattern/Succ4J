package falsepattern;

import falsepattern.os.OS;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FalseUtil {
    public static <T> T getDefaultValue(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null; //TODO
        }
        //return (T)Array.get(Array.newInstance(type, 1), 0);
    }
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
}