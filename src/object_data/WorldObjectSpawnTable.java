package object_data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class WorldObjectSpawnTable {
    private WorldObjectSpawnTable() {}

    public static List<ObjectSpawnPlan> defaultPlans(int tileSize) {
        List<ObjectSpawnPlan> plans = new ArrayList<>();

        int map0DoorX = 48 * tileSize - 23;
        int map0DoorY = 18 * tileSize;
        int map3DoorX = 15 * tileSize + 22;
        int map3DoorY = 23 * tileSize;
        int portalX = 47 * tileSize + 12;
        int portalY = 47 * tileSize + 12;

        plans.add(new ObjectSpawnPlan(WorldObjectType.SHOP, 0, 46 * tileSize, 15 * tileSize));
        plans.add(new ObjectSpawnPlan(
                WorldObjectType.DOOR,
                0,
                map0DoorX,
                map0DoorY,
                new TeleportDestination(3, map3DoorX, map3DoorY)
        ));
        plans.add(new ObjectSpawnPlan(
                WorldObjectType.PORTAL,
                0,
                portalX,
                portalY,
                new TeleportDestination(1, portalX, portalY + tileSize)
        ));

        plans.add(new ObjectSpawnPlan(
                WorldObjectType.PORTAL,
                1,
                portalX,
                portalY,
                new TeleportDestination(0, portalX, portalY + tileSize)
        ));

        plans.add(new ObjectSpawnPlan(
                WorldObjectType.DOOR,
                3,
                map3DoorX,
                map3DoorY,
                new TeleportDestination(0, map0DoorX, map0DoorY)
        ));
        plans.add(new ObjectSpawnPlan(WorldObjectType.KEY, 3, 10 * tileSize, 18 * tileSize + 5));
        plans.add(new ObjectSpawnPlan(WorldObjectType.AXE, 3, 16 * tileSize, 18 * tileSize));

        return Collections.unmodifiableList(plans);
    }
}
