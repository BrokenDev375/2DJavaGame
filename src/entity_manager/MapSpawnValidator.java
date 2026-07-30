package entity_manager;

import main.GamePanel;

final class MapSpawnValidator {
    private final GamePanel gp;
    private final String originalMapPath;

    MapSpawnValidator(GamePanel gp) {
        this.gp = gp;
        this.originalMapPath = gp.getChunkManager().getMapPath();
    }

    void loadMap(int mapId) {
        gp.getChunkManager().loadMap("map" + mapId);
        gp.getChunkManager().loadAllChunksSync();
    }

    boolean isBlockedTile(int worldX, int worldY) {
        return gp.getTileManager().isCollisionAtWorld(worldX, worldY, gp.getChunkManager());
    }

    void restoreOriginalMap() {
        gp.getChunkManager().loadMap(originalMapPath);
    }
}
