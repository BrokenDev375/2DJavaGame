package entity_manager;

import entity.Entity;
import main.DebugLog;
import main.GamePanel;
import monster_data.MonsterFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MonsterManager {
    private final GamePanel gp;
    private final MonsterFactory monsterFactory;
    private final Map<Integer, List<Entity>> monstersByMap = new HashMap<>();
    private final List<SpawnSlot> spawnSlots = new ArrayList<>();
    private final boolean useDistanceCheck = false;

    private static class SpawnSlot {
        final MonsterSpawnPlan plan;

        Entity current;
        long lastDeathTime = 0L;

        SpawnSlot(MonsterSpawnPlan plan) {
            this.plan = plan;
        }
    }

    public MonsterManager(GamePanel gp) {
        this(gp, new MonsterFactory(gp), new MonsterSpawnTable(gp).defaultPlans());
    }

    public MonsterManager(GamePanel gp, MonsterFactory monsterFactory, List<MonsterSpawnPlan> spawnPlans) {
        this.gp = gp;
        this.monsterFactory = monsterFactory;
        for (MonsterSpawnPlan plan : spawnPlans) {
            spawnSlots.add(new SpawnSlot(plan));
        }
        initialSpawn();
    }

    private void initialSpawn() {
        for (SpawnSlot slot : spawnSlots) {
            spawnNow(slot);
        }
    }

    private void spawnNow(SpawnSlot slot) {
        if (slot.current != null) return;

        Entity monster = monsterFactory.create(slot.plan.monsterType(), slot.plan.mapId());

        monster.spawnAt(slot.plan.worldX(), slot.plan.worldY());

        if (monster instanceof monster_data.Monster typedMonster) {
            typedMonster.rememberHomePosition(slot.plan.worldX(), slot.plan.worldY());
        }

        monstersOnMap(slot.plan.mapId()).add(monster);
        slot.current = monster;
    }

    public List<Entity> getMonsters(int mapId) {
        return Collections.unmodifiableList(monstersByMap.getOrDefault(mapId, Collections.emptyList()));
    }

    public Optional<Entity> monsterAt(int mapId, int index) {
        List<Entity> monsters = monstersByMap.get(mapId);
        if (monsters == null || index < 0 || index >= monsters.size()) return Optional.empty();
        return Optional.of(monsters.get(index));
    }

    private List<Entity> monstersOnMap(int mapId) {
        return monstersByMap.computeIfAbsent(mapId, k -> new ArrayList<>());
    }

    public void update(int mapId, int playerX, int playerY) {
        List<Entity> list = monstersByMap.get(mapId);
        if (list != null) {
            Iterator<Entity> it = list.iterator();
            while (it.hasNext()) {
                Entity e = it.next();
                e.update();

                if (e.isDead()) {
                    if (DebugLog.isEnabled()) {
                        DebugLog.info("[MonsterManager] Dead: " + e.getName()
                                + " map=" + mapId
                                + " x=" + e.getWorldX()
                                + " y=" + e.getWorldY());
                    }
                    registerDeath(e);
                    it.remove();
                }
            }
        }

        handleRespawn(mapId, playerX, playerY);
    }

    public void draw(java.awt.Graphics2D g2, int mapId) {
        for (Entity m : monstersByMap.getOrDefault(mapId, Collections.emptyList())) {
            m.draw(g2);
        }
    }

    private void registerDeath(Entity e) {
        long now = System.currentTimeMillis();
        for (SpawnSlot slot : spawnSlots) {
            if (slot.current == e) {
                slot.current = null;
                slot.lastDeathTime = now;
                break;
            }
        }
    }

    private void handleRespawn(int currentMapId, int playerX, int playerY) {
        long now = System.currentTimeMillis();

        for (SpawnSlot slot : spawnSlots) {
            if (slot.plan.mapId() != currentMapId) continue;
            if (slot.current != null) continue;
            if (slot.lastDeathTime == 0L) continue;

            long waited = now - slot.lastDeathTime;
            if (waited < slot.plan.respawnDelayMs()) {
                continue;
            }

            if (useDistanceCheck && !isFarFromPlayer(slot.plan.worldX(), slot.plan.worldY(), playerX, playerY)) {
                continue;
            }

            if (DebugLog.isEnabled()) {
                DebugLog.info("[MonsterManager] Respawn: " + slot.plan.monsterType());
            }
            spawnNow(slot);
        }
    }

    private boolean isFarFromPlayer(int x, int y, int playerX, int playerY) {
        int dx = x - playerX;
        int dy = y - playerY;
        int safeRadius = gp.tileSize() * 5;
        return dx * dx + dy * dy > safeRadius * safeRadius;
    }

}
