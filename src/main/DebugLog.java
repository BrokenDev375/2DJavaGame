package main;

public final class DebugLog {
    public static final boolean ENABLED = false;

    private DebugLog() {}

    public static void info(String message) {
        if (ENABLED) {
            System.out.println(message);
        }
    }
}
