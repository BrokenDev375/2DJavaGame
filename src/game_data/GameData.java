package game_data;

import java.util.Collections;
import java.util.List;

public class GameData {
    public static final int CURRENT_VERSION = 1;

    private int version = CURRENT_VERSION;
    private PlayerData player;
    private List<ObjectData> objects;
    private List<ObjectData> worldObjects;
    private int mapIndex;
    private String mapPath;

    private GameData() {
        // Used by Gson.
    }

    public GameData(PlayerData player, List<ObjectData> objects, int mapIndex, String mapPath) {
        this(player, objects, mapIndex, mapPath, Collections.emptyList());
    }

    public GameData(
            PlayerData player,
            List<ObjectData> objects,
            int mapIndex,
            String mapPath,
            List<ObjectData> worldObjects
    ) {
        this.version = CURRENT_VERSION;
        this.player = player;
        this.objects = objects;
        this.worldObjects = worldObjects;
        this.mapIndex = mapIndex;
        this.mapPath = mapPath;
    }

    public int getVersion() {
        return version <= 0 ? CURRENT_VERSION : version;
    }

    public PlayerData getPlayer() {
        return player;
    }

    public List<ObjectData> getObjects() {
        return objects;
    }

    public List<ObjectData> getWorldObjects() {
        return worldObjects == null ? Collections.emptyList() : worldObjects;
    }

    public boolean hasWorldObjectSnapshot() {
        return worldObjects != null;
    }

    public int getMapIndex() {
        return mapIndex;
    }

    public String getMapPath() {
        return mapPath;
    }
}
