package main;

import entity.Entity;
import object_data.WorldObject;
import tile.Chunk;

import java.util.List;

public interface WorldQuery {
    GameConfig getConfig();

    int currentMap();

    CollisionChecker collisionChecker();

    List<WorldObject> objectsOnMap(int mapId);

    Entity player();

    Iterable<Chunk> activeChunks();

    boolean isTileCollidable(int tileNum);
}
