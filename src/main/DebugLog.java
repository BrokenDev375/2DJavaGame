package main;

public final class DebugLog {
    public static final boolean ENABLED = false;

    private DebugLog() {}

    public static boolean isEnabled() {
        return ENABLED;
    }

    public static void info(String message) {
        if (ENABLED) {
            System.out.println(message);
        }
    }

    public static void error(String message, Throwable cause) {
        System.err.println(message);
        if (ENABLED && cause != null) {
            cause.printStackTrace(System.err);
        }
    }
}
