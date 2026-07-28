package game_data;

/**
 * Serializable data for objects and monsters in the world.
 */
public class ObjectData {
    private String type;
    private int worldX, worldY;
    private boolean active;

    private ObjectData() {
        // Used by Gson.
    }

    public ObjectData(String type, int worldX, int worldY, boolean active) {
        this.type = type;
        this.worldX = worldX;
        this.worldY = worldY;
        this.active = active;
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
}
