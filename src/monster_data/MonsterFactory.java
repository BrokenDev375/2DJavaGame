package monster_data;

import main.GamePanel;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.DoubleSupplier;

public final class MonsterFactory {
    private final GamePanel gp;
    private final DoubleSupplier slimeVariantRoll;

    public MonsterFactory(GamePanel gp) {
        this(gp, Math::random);
    }

    public MonsterFactory(GamePanel gp, DoubleSupplier slimeVariantRoll) {
        this.gp = Objects.requireNonNull(gp);
        this.slimeVariantRoll = Objects.requireNonNull(slimeVariantRoll);
    }

    public Monster create(MonsterType type, int mapId) {
        Objects.requireNonNull(type);

        return switch (type) {
            case SLIME -> slimeVariantRoll.getAsDouble() < 0.5
                    ? new RedSlimeMonster(gp, mapId)
                    : new SlimeMonster(gp, mapId);
            case BAT -> new BatMonster(gp, mapId);
            case ORC -> new OrcMonster(gp, mapId);
            case BOSS -> new SkeletonLord(gp, mapId);
        };
    }

    public Optional<Monster> createById(String id, int mapId) {
        return MonsterType.fromId(id).map(type -> create(type, mapId));
    }

    public Optional<Monster> createBySavedName(String savedName, int mapId) {
        String identity = savedIdentity(savedName);
        if (identity == null) {
            return Optional.empty();
        }

        return switch (identity) {
            case "green slime" -> Optional.of(new SlimeMonster(gp, mapId));
            case "red slime" -> Optional.of(new RedSlimeMonster(gp, mapId));
            case "bat" -> Optional.of(new BatMonster(gp, mapId));
            case "orc" -> Optional.of(new OrcMonster(gp, mapId));
            case "skeleton lord" -> Optional.of(new SkeletonLord(gp, mapId));
            default -> createById(savedName, mapId);
        };
    }

    public static boolean matchesSavedName(String monsterName, String savedName) {
        String monsterIdentity = savedIdentity(monsterName);
        String savedIdentity = savedIdentity(savedName);
        if (monsterIdentity == null || savedIdentity == null) {
            return false;
        }
        if ("slime".equals(savedIdentity)) {
            return isSlimeIdentity(monsterIdentity);
        }
        return monsterIdentity.equals(savedIdentity);
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim()
                .replace('_', ' ')
                .replace('-', ' ')
                .toLowerCase(Locale.ROOT);
    }

    private static String savedIdentity(String value) {
        String normalized = normalize(value);
        if (normalized == null || normalized.isBlank()) {
            return null;
        }

        return switch (normalized) {
            case "redslime", "red slime" -> "red slime";
            case "green slime" -> "green slime";
            case "slime" -> "slime";
            case "boss", "skeleton lord" -> "skeleton lord";
            default -> normalized;
        };
    }

    private static boolean isSlimeIdentity(String identity) {
        return "slime".equals(identity)
                || "green slime".equals(identity)
                || "red slime".equals(identity);
    }
}
