package game_data;

/**
 * Serializable data for objects and monsters in the world.
 */
public class ObjectData {
    private String type;
    private int worldX, worldY;
    private int spawnX, spawnY;
    private boolean active;

    private ObjectData() {
        // Used by Gson.
    }

    public ObjectData(String type, int worldX, int worldY, boolean active) {
        this(type, worldX, worldY, active, 0, 0);
    }

    public ObjectData(String type, int worldX, int worldY, boolean active, int spawnX, int spawnY) {
        this.type = type;
        this.worldX = worldX;
        this.worldY = worldY;
        this.active = active;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    public String getType() {
        return type;
    }

    public int getWorldX() {
        return worldX;
    }

    public int getWorldY() {
        return worldY;
    }

    public boolean isActive() {
        return active;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public boolean hasSpawnIdentity() {
        return spawnX != 0 || spawnY != 0;
    }
}
