package game_data;

import java.util.List;

public class GameData {
    public static final int CURRENT_VERSION = 1;

    private int version = CURRENT_VERSION;
    private PlayerData player;
    private List<ObjectData> objects;
    private int mapIndex;
    private String mapPath;

    private GameData() {
        // Used by Gson.
    }

    public GameData(PlayerData player, List<ObjectData> objects, int mapIndex, String mapPath) {
        this.version = CURRENT_VERSION;
        this.player = player;
        this.objects = objects;
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

    public int getMapIndex() {
        return mapIndex;
    }

    public String getMapPath() {
        return mapPath;
    }
}
