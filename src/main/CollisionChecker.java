package main;

import entity.Direction;
import entity.Entity;
import object_data.WorldObject;
import tile.Chunk;
import world.WorldBody;

import java.awt.Rectangle;
import java.util.List;

public class CollisionChecker {

    GamePanel gp;
    public CollisionChecker(GamePanel gp){
        this.gp = gp;
    }
    
    //get tile number from chunk.tmx file 
    private int getTileNumAt(int worldX, int worldY){
        int tileCol = worldX / gp.tileSize;
        int tileRow = worldY / gp.tileSize;

        int chunkX = tileCol / gp.chunkSize;
        int chunkY = tileRow / gp.chunkSize;

        int localCol = tileCol % gp.chunkSize;
        int localRow = tileRow % gp.chunkSize;

        for(Chunk c : gp.getChunkManager().getActiveChunks()){
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
        return mover.getSolidAreaAt(nextWorldX, nextWorldY).intersects(target.getSolidAreaWorld());
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
        if (gp.getTileManager().isTileCollidable(tileNum1) || gp.getTileManager().isTileCollidable(tileNum2)) {
            entity.markCollisionX();
        }

        tileNum1 = getTileNumAt(entityRightWorldX, entityTopWorldY);
        tileNum2 = getTileNumAt(entityRightWorldX, entityBotWorldY);
        if (gp.getTileManager().isTileCollidable(tileNum1) || gp.getTileManager().isTileCollidable(tileNum2)) {
            entity.markCollisionX();
        }

        // Check Y
        tileNum1 = getTileNumAt(entityLeftWorldX, entityTopWorldY);
        tileNum2 = getTileNumAt(entityRightWorldX, entityTopWorldY);
        if (gp.getTileManager().isTileCollidable(tileNum1) || gp.getTileManager().isTileCollidable(tileNum2)) {
            entity.markCollisionY();
        }

        tileNum1 = getTileNumAt(entityLeftWorldX, entityBotWorldY);
        tileNum2 = getTileNumAt(entityRightWorldX, entityBotWorldY);
        if (gp.getTileManager().isTileCollidable(tileNum1) || gp.getTileManager().isTileCollidable(tileNum2)) {
            entity.markCollisionY();
        }
    }
    // this check player -> entity 
    // this check player -> entity (NPC / Monster)
    public int checkEntity(Entity entity , List<Entity> targets, int nextX, int nextY){
        int index = 999;

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
        if (overlaps(entity, nextX, nextY, gp.getEntityManager().getPlayer())) {
            entity.markCollision();
        }
    }
    // return object touched
    public int checkWorldObject(Entity mover, List<WorldObject> objects, int nextDX, int nextDY) {
        if (mover == null || objects == null || objects.isEmpty()) return 999;

        int nextWorldX = mover.getWorldX() + nextDX;
        int nextWorldY = mover.getWorldY() + nextDY;

        int interactedIndex = 999;

        for (int i = 0; i < objects.size(); i++) {
            WorldObject obj = objects.get(i);
            if (obj == null || !obj.isOnMap(mover.getMapIndex())) continue;

            if (overlaps(mover, nextWorldX, nextWorldY, obj)) {
                if (obj.isCollidable()) mover.markCollision();

                // Ưu tiên door hơn các object khác
                if (interactedIndex == 999) {
                    interactedIndex = i;
                } else {
                    WorldObject current = objects.get(interactedIndex);
                    if (!current.isNamed("door") && obj.isNamed("door")) {
                        interactedIndex = i; // ưu tiên door
                    }
                }
            }
        }
    return interactedIndex;
    }
}

