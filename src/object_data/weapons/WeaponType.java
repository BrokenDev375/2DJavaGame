package object_data.weapons;

import java.util.Locale;
import java.util.Optional;

public enum WeaponType {
    SWORD("Argonaut hero's sword"),
    AXE("Leviathan Axe"),
    PICK("Steve's pick", "Steve Pick");

    private final String displayName;
    private final String[] aliases;

    WeaponType(String displayName, String... aliases) {
        this.displayName = displayName;
        this.aliases = aliases;
    }

    public String displayName() {
        return displayName;
    }

    public boolean matches(String name) {
        String normalized = normalize(name);
        if (normalized == null) {
            return false;
        }

        if (normalized.equals(normalize(displayName))) {
            return true;
        }

        for (String alias : aliases) {
            if (normalized.equals(normalize(alias))) {
                return true;
            }
        }
        return false;
    }

    public static Optional<WeaponType> fromName(String name) {
        for (WeaponType type : values()) {
            if (type.matches(name)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    private static String normalize(String name) {
        if (name == null) {
            return null;
        }
        return name.trim().toLowerCase(Locale.ROOT);
    }
}
