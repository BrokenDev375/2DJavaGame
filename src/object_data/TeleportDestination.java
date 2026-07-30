package object_data;

public final class TeleportDestination {
    private final int mapId;
    private final int worldX;
    private final int worldY;

    public TeleportDestination(int mapId, int worldX, int worldY) {
        this.mapId = mapId;
        this.worldX = worldX;
        this.worldY = worldY;
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

    public String mapPath() {
        return "map" + mapId;
    }
}
