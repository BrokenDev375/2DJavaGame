package monster_data;

import java.util.Locale;
import java.util.Optional;

public enum MonsterType {
    SLIME,
    BAT,
    ORC,
    BOSS;

    public static Optional<MonsterType> fromId(String id) {
        if (id == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(valueOf(id.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
