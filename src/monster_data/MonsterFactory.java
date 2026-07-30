package monster_data;

import main.GamePanel;

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
}
