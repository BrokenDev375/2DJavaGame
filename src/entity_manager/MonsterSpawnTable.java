package entity_manager;

import main.GamePanel;
import monster_data.MonsterType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MonsterSpawnTable {
    private final GamePanel gp;

    public MonsterSpawnTable(GamePanel gp) {
        this.gp = gp;
    }

    public List<MonsterSpawnPlan> defaultPlans() {
        List<MonsterSpawnPlan> plans = new ArrayList<>();
        int tileSize = gp.tileSize;
        MapSpawnValidator validator = new MapSpawnValidator(gp);

        try {
            validator.loadMap(0);
            for (int ty = 18; ty <= 72; ty += 9) {
                for (int tx = 18; tx <= 72; tx += 9) {
                    int wx = tx * tileSize;
                    int wy = ty * tileSize;

                    if (!validator.isBlockedTile(wx, wy)) {
                        plans.add(new MonsterSpawnPlan(0, wx, wy, MonsterType.SLIME, 15_000L));
                    }
                }
            }

            validator.loadMap(1);
            int mobCount = 0;
            for (int ty = 18; ty <= 54; ty += 9) {
                for (int tx = 18; tx <= 63; tx += 9) {
                    int wx = tx * tileSize;
                    int wy = ty * tileSize;

                    if (validator.isBlockedTile(wx, wy)) {
                        continue;
                    }

                    MonsterType type = (mobCount % 2 == 0) ? MonsterType.ORC : MonsterType.BAT;
                    long respawn = type == MonsterType.ORC ? 35_000L : 25_000L;

                    plans.add(new MonsterSpawnPlan(1, wx, wy, type, respawn));
                    mobCount++;
                }
            }

            plans.add(new MonsterSpawnPlan(1, 50 * tileSize, 75 * tileSize, MonsterType.BOSS, 600_000L));
        } finally {
            validator.restoreOriginalMap();
        }

        return Collections.unmodifiableList(plans);
    }
}
