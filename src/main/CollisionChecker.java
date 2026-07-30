package main;

import entity.Direction;
import entity.Entity;
import object_data.WorldObject;
import object_data.WorldObjectType;
import tile.Chunk;
import world.WorldBody;

import java.awt.Rectangle;
import java.util.List;

public class CollisionChecker {

    public static final int NO_HIT = -1;

    private final WorldQuery world;
    private final GameConfig config;

    public CollisionChecker(WorldQuery world){
        this.world = world;
        this.config = world.getConfig();
    }
    
    //get tile number from chunk.tmx file 
    private int getTileNumAt(int worldX, int worldY){
        int tileCol = worldX / config.tileSize();
        int tileRow = worldY / config.tileSize();

        int chunkX = tileCol / config.chunkSize();
        int chunkY = tileRow / config.chunkSize();

        int localCol = tileCol % config.chunkSize();
        int localRow = tileRow % config.chunkSize();

        for(Chunk c : world.activeChunks()){
            if(c.getChunkX() == chunkX && c.getChunkY() == chunkY){
                if(localRow >= 0 && localRow < c.getSize() &&
                   localCol >= 0 && localCol < c.getSize()){
                    return c.getTileNum(localRow, localCol);
                }
            }
        }
        return 0; // default tile
    }

    private boolean overlaps(WorldBody mover, int nextWorldX, int nextWorldY, WorldBody target) {
        return CollisionGeometry.overlaps(mover, nextWorldX, nextWorldY, target);
    }

    public void checkTile(Entity entity,int nextX, int nextY){
        Rectangle entityBody = entity.getSolidAreaAt(nextX, nextY);
        int entityLeftWorldX  = entityBody.x;
        int entityRightWorldX = entityBody.x + entityBody.width - 1;
        int entityTopWorldY   = entityBody.y;
        int entityBotWorldY   = entityBody.y + entityBody.height - 1;
        
        int tileNum1, tileNum2;

        // Check X
        tileNum1 = getTileNumAt(entityLeftWorldX, entityTopWorldY);
        tileNum2 = getTileNumAt(entityLeftWorldX, entityBotWorldY);
        if (world.isTileCollidable(tileNum1) || world.isTileCollidable(tileNum2)) {
            entity.markCollisionX();
        }

        tileNum1 = getTileNumAt(entityRightWorldX, entityTopWorldY);
        tileNum2 = getTileNumAt(entityRightWorldX, entityBotWorldY);
        if (world.isTileCollidable(tileNum1) || world.isTileCollidable(tileNum2)) {
            entity.markCollisionX();
        }

        // Check Y
        tileNum1 = getTileNumAt(entityLeftWorldX, entityTopWorldY);
        tileNum2 = getTileNumAt(entityRightWorldX, entityTopWorldY);
        if (world.isTileCollidable(tileNum1) || world.isTileCollidable(tileNum2)) {
            entity.markCollisionY();
        }

        tileNum1 = getTileNumAt(entityLeftWorldX, entityBotWorldY);
        tileNum2 = getTileNumAt(entityRightWorldX, entityBotWorldY);
        if (world.isTileCollidable(tileNum1) || world.isTileCollidable(tileNum2)) {
            entity.markCollisionY();
        }
    }
    // this check player -> entity 
    // this check player -> entity (NPC / Monster)
    public int checkEntity(Entity entity , List<Entity> targets, int nextX, int nextY){
        int index = NO_HIT;

        // === tính offset theo hướng nhìn để va chạm dễ bắt hơn ===
        int offsetX = 0, offsetY = 0;
        Direction direction = entity.getDirection();
        offsetX = direction.dx() * entity.getActualSpeed();
        offsetY = direction.dy() * entity.getActualSpeed();

        for (int i = 0; i < targets.size(); i++) {
            Entity target = targets.get(i);
            if (target != null && target.isOnMap(entity.getMapIndex())) {
                if (overlaps(entity, nextX + offsetX, nextY + offsetY, target)) {
                    if (target.isCollidable()) entity.markCollision();
                    index = i; // trả về index va chạm
                }
            }
        }

        return index;
    }


    // this check entity -> player 
    public void checkPlayer(Entity entity , int nextX ,int nextY){
        Entity player = world.player();
        if (player != null && overlaps(entity, nextX, nextY, player)) {
            entity.markCollision();
        }
    }
    // return object touched
    public int checkWorldObject(Entity mover, List<WorldObject> objects, int nextDX, int nextDY) {
        if (mover == null || objects == null || objects.isEmpty()) return NO_HIT;

        int nextWorldX = mover.getWorldX() + nextDX;
        int nextWorldY = mover.getWorldY() + nextDY;

        int interactedIndex = NO_HIT;

        for (int i = 0; i < objects.size(); i++) {
            WorldObject obj = objects.get(i);
            if (obj == null || !obj.isOnMap(mover.getMapIndex())) continue;

            if (overlaps(mover, nextWorldX, nextWorldY, obj)) {
                if (obj.isCollidable()) mover.markCollision();

                // Ưu tiên door hơn các object khác
                if (interactedIndex == NO_HIT) {
                    interactedIndex = i;
                } else {
                    WorldObject current = objects.get(interactedIndex);
                    if (!current.isType(WorldObjectType.DOOR) && obj.isType(WorldObjectType.DOOR)) {
                        interactedIndex = i; // ưu tiên door
                    }
                }
            }
        }
    return interactedIndex;
    }
}

