package game_data;

import java.util.List;

public class GameData {
    private PlayerData player;
    private List<ObjectData> objects;
    private int mapIndex;
    private String mapPath;

    private GameData() {
        // Used by Gson.
    }

    public GameData(PlayerData player, List<ObjectData> objects, int mapIndex, String mapPath) {
        this.player = player;
        this.objects = objects;
        this.mapIndex = mapIndex;
        this.mapPath = mapPath;
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
