package entity_manager;

import monster_data.MonsterType;

import java.util.Objects;

public final class MonsterSpawnPlan {
    private final int mapId;
    private final int worldX;
    private final int worldY;
    private final MonsterType monsterType;
    private final long respawnDelayMs;

    public MonsterSpawnPlan(int mapId, int worldX, int worldY, MonsterType monsterType, long respawnDelayMs) {
        this.mapId = mapId;
        this.worldX = worldX;
        this.worldY = worldY;
        this.monsterType = Objects.requireNonNull(monsterType);
        this.respawnDelayMs = respawnDelayMs;
    }

    public int mapId() {
        return mapId;
    }

    public int worldX() {
        return worldX;
    }

    public int worldY() {
        return worldY;
    }

    public MonsterType monsterType() {
        return monsterType;
    }

    public long respawnDelayMs() {
        return respawnDelayMs;
    }
}
