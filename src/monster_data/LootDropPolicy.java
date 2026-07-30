package monster_data;

import object_data.ObjectDropRequest;
import object_data.WorldObjectType;

import java.util.Objects;
import java.util.function.DoubleSupplier;

public final class LootDropPolicy {
    public static final double HEALTH_POTION_DROP_CHANCE = 0.25;

    private final DoubleSupplier random;

    public LootDropPolicy() {
        this(Math::random);
    }

    public LootDropPolicy(DoubleSupplier random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public LootDropResult rollHealthPotion(int mapIndex, int worldX, int worldY) {
        double roll = random.getAsDouble();
        ObjectDropRequest drop = roll < HEALTH_POTION_DROP_CHANCE
                ? ObjectDropRequest.of(WorldObjectType.HEALTH_POSION, mapIndex, worldX, worldY)
                : null;
        return new LootDropResult(roll, drop);
    }

    public ObjectDropRequest guaranteed(WorldObjectType type, int mapIndex, int worldX, int worldY) {
        return ObjectDropRequest.of(type, mapIndex, worldX, worldY);
    }
}
