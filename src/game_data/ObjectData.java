package game_data;

/**
 * Serializable data for objects and monsters in the world.
 */
public class ObjectData {
    private int mapIndex = -1;
    private String type;
    private int worldX, worldY;
    private int spawnX, spawnY;
    private int health = -1;
    private boolean active;

    private ObjectData() {
        // Used by Gson.
    }

    public ObjectData(String type, int worldX, int worldY, boolean active) {
        this(type, worldX, worldY, active, 0, 0);
    }

    public ObjectData(String type, int worldX, int worldY, boolean active, int spawnX, int spawnY) {
        this(type, worldX, worldY, active, spawnX, spawnY, -1);
    }

    public ObjectData(String type, int worldX, int worldY, boolean active, int spawnX, int spawnY, int mapIndex) {
        this(type, worldX, worldY, active, spawnX, spawnY, mapIndex, -1);
    }

    public ObjectData(
            String type,
            int worldX,
            int worldY,
            boolean active,
            int spawnX,
            int spawnY,
            int mapIndex,
            int health
    ) {
        this.mapIndex = mapIndex;
        this.type = type;
        this.worldX = worldX;
        this.worldY = worldY;
        this.active = active;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.health = health;
    }

    public String getType() {
        return type;
    }

    public int getMapIndex() {
        return hasMapIndex() ? mapIndex : 0;
    }

    public boolean hasMapIndex() {
        return mapIndex >= 0;
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

    public int getHealth() {
        return Math.max(0, health);
    }

    public boolean hasHealth() {
        return health > 0;
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
