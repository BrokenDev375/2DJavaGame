package entity;

final class MapPlacement {
    private int worldX;
    private int worldY;
    private int mapIndex = 0;

    void moveBy(int dx, int dy) {
        worldX += dx;
        worldY += dy;
    }

    void moveTo(int x, int y) {
        worldX = x;
        worldY = y;
    }

    int getWorldX() {
        return worldX;
    }

    int getWorldY() {
        return worldY;
    }

    int getMapIndex() {
        return mapIndex;
    }

    void placeOnMap(int mapIndex) {
        this.mapIndex = mapIndex;
    }

    boolean isOnMap(int mapIndex) {
        return this.mapIndex == mapIndex;
    }
}
