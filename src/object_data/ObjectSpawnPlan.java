package object_data;

import java.util.Objects;
import java.util.Optional;

public final class ObjectSpawnPlan {
    private final WorldObjectType type;
    private final int mapId;
    private final int worldX;
    private final int worldY;
    private final TeleportDestination teleportDestination;

    public ObjectSpawnPlan(WorldObjectType type, int mapId, int worldX, int worldY) {
        this(type, mapId, worldX, worldY, null);
    }

    public ObjectSpawnPlan(
            WorldObjectType type,
            int mapId,
            int worldX,
            int worldY,
            TeleportDestination teleportDestination
    ) {
        this.type = Objects.requireNonNull(type);
        this.mapId = mapId;
        this.worldX = worldX;
        this.worldY = worldY;
        this.teleportDestination = teleportDestination;
    }

    public WorldObjectType type() {
        return type;
    }

    public int mapId() {
        return mapId;
    }

    public int worldX() {
        return worldX;
    }

    public int worldY() {
        return worldY;
    }

    public Optional<TeleportDestination> teleportDestination() {
        return Optional.ofNullable(teleportDestination);
    }
}
