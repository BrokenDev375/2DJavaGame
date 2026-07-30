package object_data;

import java.util.Objects;

public final class ObjectDropRequest {
    private final WorldObjectType type;
    private final int mapIndex;
    private final int worldX;
    private final int worldY;

    private ObjectDropRequest(WorldObjectType type, int mapIndex, int worldX, int worldY) {
        this.type = Objects.requireNonNull(type, "type");
        this.mapIndex = mapIndex;
        this.worldX = worldX;
        this.worldY = worldY;
    }

    public static ObjectDropRequest of(WorldObjectType type, int mapIndex, int worldX, int worldY) {
        return new ObjectDropRequest(type, mapIndex, worldX, worldY);
    }

    public WorldObjectType type() {
        return type;
    }

    public int mapIndex() {
        return mapIndex;
    }

    public int worldX() {
        return worldX;
    }

    public int worldY() {
        return worldY;
    }
}
