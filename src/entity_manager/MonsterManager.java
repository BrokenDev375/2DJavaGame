package entity_manager;

import entity.Entity;
import main.DebugLog;
import main.GamePanel;
import monster_data.*;

import java.util.*;

/**
 * Quản lý quái + respawn theo điểm spawn cố định.
 * - Không dùng gp.player, nhận toạ độ player từ outside.
 * - Mỗi SpawnSlot = 1 chỗ spawn 1 con (respawn lại đúng chỗ đó).
 */
public class MonsterManager {
    private final GamePanel gp;

    private final Map<Integer, List<Entity>> monstersByMap = new HashMap<>();

    private final List<SpawnSlot> spawnSlots = new ArrayList<>();

    // Bật/tắt check “spawn xa player”
    private final boolean useDistanceCheck = false;

    private static final boolean DEBUG_MONSTER = true;

    // ----------- Cấu trúc 1 slot spawn -----------
    private static class SpawnSlot {
        final int mapId;
        final int worldX, worldY;
        final String monsterId;       // "SLIME", "BAT", "ORC", "BOSS"...
        final long respawnDelayMs;    // thời gian hồi sinh

        Entity current;               // quái đang sống (null nếu slot trống)
        long lastDeathTime = 0L;      // lần cuối con này chết

        SpawnSlot(int mapId, int worldX, int worldY,
                  String monsterId, long respawnDelayMs) {
            this.mapId = mapId;
            this.worldX = worldX;
            this.worldY = worldY;
            this.monsterId = monsterId;
            this.respawnDelayMs = respawnDelayMs;
        }
    }

    public MonsterManager(GamePanel gp) {
        this.gp = gp;

        setupSpawnSlots();
        initialSpawn();
    }

    private void setupSpawnSlots() {
        int t = gp.tileSize;

        // Lưu lại map hiện tại của ChunkManager để restore sau
        String oldPath = gp.getChunkManager().getMapPath();

        // ================= MAP 0: Slime field =================
        gp.getChunkManager().loadMap("map0");
        gp.getChunkManager().loadAllChunksSync();     // load toàn bộ chunk map0 để check collision

        for (int ty = 18; ty <= 72; ty += 9) {
            for (int tx = 18; tx <= 72; tx += 9) {
                int wx = tx * t;
                int wy = ty * t;

                if (isBlockedTile(0, wx, wy)) {
                    continue;
                }
                addSpawn(0, wx, wy, "SLIME", 15_000L);
            }
        }

        // ================= MAP 1: Orc + Bat + Boss =================
        gp.getChunkManager().loadMap("map1");
        gp.getChunkManager().loadAllChunksSync();     // load toàn bộ chunk map1 để check collision

        // Vùng quái thường: khoảng 30 con (5 hàng x 6 cột = 30 vị trí)
        int mobCount = 0;
        for (int ty = 18; ty <= 54; ty += 9) {      // 18,27,36,45,54  -> 5 hàng
            for (int tx = 18; tx <= 63; tx += 9) {  // 18..63           -> 6 cột
                int wx = tx * t;
                int wy = ty * t;

                if (isBlockedTile(1, wx, wy)) {
                    continue;
                }

                // Xen kẽ ORC / BAT cho vui
                String id = (mobCount % 2 == 0) ? "ORC" : "BAT";
                long respawn = id.equals("ORC") ? 35_000L : 25_000L;

                addSpawn(1, wx, wy, id, respawn);
                mobCount++;
            }
        }

        addSpawn(1, 50* t, 75 * t, "BOSS", 600_000L); // ~10 phút respawn

        gp.getChunkManager().loadMap(oldPath);
    }

    private void addSpawn(int mapId, int wx, int wy,
                          String monsterId, long respawnDelayMs) {
        SpawnSlot slot = new SpawnSlot(mapId, wx, wy, monsterId, respawnDelayMs);
        spawnSlots.add(slot);
    }

    // 2. SPAWN LÚC BẮT ĐẦU GAME + RESPAWN

    private void initialSpawn() {
        for (SpawnSlot slot : spawnSlots) {
            spawnNow(slot);
        }
    }

    private void spawnNow(SpawnSlot slot) {
        if (slot.current != null) return;

        Entity m = createMonster(slot.monsterId, slot.mapId);
        if (m == null) {
            return;
        }

        m.spawnAt(slot.worldX, slot.worldY);

        if (m instanceof monster_data.Monster monster) {
            monster.setHome(slot.worldX, slot.worldY);
        }

        monstersByMap
                .computeIfAbsent(slot.mapId, k -> new ArrayList<>())
                .add(m);

        slot.current = m;
    }

    private Entity createMonster(String id, int mapId) {
        return switch (id) {
            case "SLIME" -> {
                double r = Math.random();      // 0.0 -> 1.0

                if (r < 0.5) {
                    yield new RedSlimeMonster(gp, mapId);
                } else {
                    yield new SlimeMonster(gp, mapId);
                }
            }
            case "BAT"   -> new BatMonster(gp, mapId);
            case "ORC"   -> new OrcMonster(gp, mapId);
            case "BOSS"  -> new SkeletonLord(gp, mapId);
            default      -> null;
        };
    }

    // 3. API PUBLIC
    public List<Entity> getMonsters(int mapId) {
        return monstersByMap.getOrDefault(mapId, Collections.emptyList());
    }

    public void update(int mapId, int playerX, int playerY) {
        // 3.1 Update quái đang sống
        List<Entity> list = monstersByMap.get(mapId);
        if (list != null) {
            Iterator<Entity> it = list.iterator();
            while (it.hasNext()) {
                Entity e = it.next();
                e.update();

                if (isDead(e)) {
                    if (DEBUG_MONSTER) {
                        DebugLog.info("[DEAD] " + e.getName() + " @map=" + mapId +
                                " x=" + e.getWorldX() + " y=" + e.getWorldY());
                    }
                    registerDeath(e);
                    it.remove();
                }
            }
        }

        // Respawn quái cho map hiện tại
        handleRespawn(mapId, playerX, playerY);
    }

    public void draw(java.awt.Graphics2D g2, int mapId) {
        for (Entity m : getMonsters(mapId)) {
            m.draw(g2);
        }
    }

    // 4. CHẾT + RESPAWN

    private boolean isDead(Entity e) {
        boolean dead = e.isDead();   // dùng hàm trong Entity (hp <= 0)

        return dead;
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
            if (slot.mapId != currentMapId) continue;    // chỉ xử lý map hiện tại
            if (slot.current != null) continue;          // slot đang có quái
            if (slot.lastDeathTime == 0L) continue;      // chưa từng chết ⇒ skip

            long waited = now - slot.lastDeathTime;
            if (waited < slot.respawnDelayMs) {
                continue;
            }

            if (useDistanceCheck &&
                    !isFarFromPlayer(slot.worldX, slot.worldY, playerX, playerY)) {
                continue;
            }

            if (DEBUG_MONSTER) {
                DebugLog.info("[RESPAWN_OK] " + slot.monsterId + " -> spawnNow");
            }
            spawnNow(slot);
        }
    }

    private boolean isFarFromPlayer(int x, int y, int playerX, int playerY) {
        int dx = x - playerX;
        int dy = y - playerY;
        int safeRadius = gp.tileSize * 5;
        return dx * dx + dy * dy > safeRadius * safeRadius;
    }

    private boolean isBlockedTile(int mapId, int worldX, int worldY) {
        // mapId hiện vẫn chưa cần dùng, vì ChunkManager đang trỏ
        return gp.getTileManager().isCollisionAtWorld(worldX, worldY, gp.getChunkManager());
    }
}
