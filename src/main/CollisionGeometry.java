package main;

import world.WorldBody;

final class CollisionGeometry {
    private CollisionGeometry() {}

    static boolean overlaps(WorldBody mover, int nextWorldX, int nextWorldY, WorldBody target) {
        return mover.getSolidAreaAt(nextWorldX, nextWorldY).intersects(target.getSolidAreaWorld());
    }
}
