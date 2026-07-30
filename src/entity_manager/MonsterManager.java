package entity_manager;

import entity.Entity;
import game_data.ObjectData;
import main.DebugLog;
import main.GamePanel;
import monster_data.Monster;
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
    private final boolean useDistanceCheck = true;

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

    public List<ObjectData> snapshotMonsters() {
        List<ObjectData> snapshots = new ArrayList<>();
        for (SpawnSlot slot : spawnSlots) {
            snapshots.add(snapshotSlot(slot));
        }
        return Collections.unmodifiableList(snapshots);
    }

    public void restoreMonsters(List<ObjectData> snapshots, int fallbackMapId) {
        if (snapshots == null) {
            return;
        }

        List<SpawnSlot> restoredSlots = new ArrayList<>();
        for (int i = 0; i < snapshots.size(); i++) {
            ObjectData saved = snapshots.get(i);
            Optional<SpawnSlot> target = findSlotBySnapshot(saved, fallbackMapId, restoredSlots);
            if (target.isEmpty()) {
                target = findSlotByMapIndex(snapshotMapId(saved, fallbackMapId), i, restoredSlots);
            }

            target.ifPresent(slot -> {
                restoreSlot(slot, saved);
                restoredSlots.add(slot);
            });
        }
    }

    public Optional<Entity> monsterAt(int mapId, int index) {
        List<Entity> monsters = monstersByMap.get(mapId);
        if (monsters == null || index < 0 || index >= monsters.size()) return Optional.empty();
        return Optional.of(monsters.get(index));
    }

    private List<Entity> monstersOnMap(int mapId) {
        return monstersByMap.computeIfAbsent(mapId, k -> new ArrayList<>());
    }

    private ObjectData snapshotSlot(SpawnSlot slot) {
        if (slot.current instanceof Monster monster) {
            return new ObjectData(
                    monster.getName(),
                    monster.getWorldX(),
                    monster.getWorldY(),
                    !monster.isDead(),
                    slot.plan.worldX(),
                    slot.plan.worldY(),
                    slot.plan.mapId(),
                    monster.getHP()
            );
        }

        return new ObjectData(
                slot.plan.monsterType().name(),
                slot.plan.worldX(),
                slot.plan.worldY(),
                false,
                slot.plan.worldX(),
                slot.plan.worldY(),
                slot.plan.mapId()
        );
    }

    private Optional<SpawnSlot> findSlotBySnapshot(
            ObjectData saved,
            int fallbackMapId,
            List<SpawnSlot> restoredSlots
    ) {
        if (saved == null || !saved.hasSpawnIdentity()) {
            return Optional.empty();
        }

        int mapId = snapshotMapId(saved, fallbackMapId);
        for (SpawnSlot slot : spawnSlots) {
            if (!restoredSlots.contains(slot)
                    && slot.plan.mapId() == mapId
                    && slot.plan.worldX() == saved.getSpawnX()
                    && slot.plan.worldY() == saved.getSpawnY()) {
                return Optional.of(slot);
            }
        }
        return Optional.empty();
    }

    private Optional<SpawnSlot> findSlotByMapIndex(
            int mapId,
            int index,
            List<SpawnSlot> restoredSlots
    ) {
        int sameMapIndex = 0;
        for (SpawnSlot slot : spawnSlots) {
            if (restoredSlots.contains(slot) || slot.plan.mapId() != mapId) {
                continue;
            }

            if (sameMapIndex == index) {
                return Optional.of(slot);
            }
            sameMapIndex++;
        }
        return Optional.empty();
    }

    private void restoreSlot(SpawnSlot slot, ObjectData saved) {
        if (saved == null) {
            return;
        }

        if (!saved.isActive()) {
            despawn(slot);
            slot.lastDeathTime = System.currentTimeMillis();
            return;
        }

        Monster monster = ensureMonsterFor(slot, saved);
        monster.restorePosition(saved.getWorldX(), saved.getWorldY());
        monster.rememberHomePosition(slot.plan.worldX(), slot.plan.worldY());
        if (saved.hasHealth()) {
            monster.restoreHP(saved.getHealth());
        } else {
            monster.revive();
        }
    }

    private Monster ensureMonsterFor(SpawnSlot slot, ObjectData saved) {
        if (slot.current instanceof Monster monster) {
            if (MonsterFactory.matchesSavedName(monster.getName(), saved.getType())) {
                return monster;
            }
            despawn(slot);
        }

        Monster monster = monsterFactory
                .createBySavedName(saved.getType(), slot.plan.mapId())
                .orElseGet(() -> monsterFactory.create(slot.plan.monsterType(), slot.plan.mapId()));
        monster.spawnAt(slot.plan.worldX(), slot.plan.worldY());
        monster.rememberHomePosition(slot.plan.worldX(), slot.plan.worldY());
        monstersOnMap(slot.plan.mapId()).add(monster);
        slot.current = monster;
        return monster;
    }

    private void despawn(SpawnSlot slot) {
        if (slot.current != null) {
            monstersOnMap(slot.plan.mapId()).remove(slot.current);
            slot.current = null;
        }
    }

    private int snapshotMapId(ObjectData saved, int fallbackMapId) {
        return saved != null && saved.hasMapIndex() ? saved.getMapIndex() : fallbackMapId;
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
