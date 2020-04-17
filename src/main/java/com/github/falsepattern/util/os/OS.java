package com.github.falsepattern.util.os;

public class OS {
    static {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            type = OSType.WINDOWS;
        } else if (os.contains("mac")) {
            type = OSType.OSX;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            type = OSType.UNIX;
        } else if (os.contains("sunos")) {
            type = OSType.SOLARIS;
        } else {
            type = OSType.UNKNOWN;
        }
    }
    public static final OSType type;

    public static boolean isWindows() {
        return type == OSType.WINDOWS;
    }

    public static boolean isMac() {
        return type == OSType.OSX;
    }

    public static boolean isUnix() {
        return type == OSType.UNIX;
    }

    public static boolean isSolaris() {
        return type == OSType.SOLARIS;
    }

    public static boolean isUnknown() {
        return type == OSType.UNKNOWN;
    }

    public enum OSType {
        WINDOWS, OSX, UNIX, SOLARIS, UNKNOWN
    }
}
