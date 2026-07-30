package monster_data;

import entity.Direction;
import world.WorldBody;

import java.awt.Rectangle;

public final class MonsterAttackPlanner {
    public boolean canReachTarget(
            WorldBody attacker,
            Direction facing,
            WorldBody target,
            int reachWidth,
            int reachHeight
    ) {
        if (attacker == null || target == null) {
            return false;
        }

        Rectangle attackerBody = attacker.getSolidAreaWorld();
        Rectangle targetBody = target.getSolidAreaWorld();
        targetBody.grow(2, 2);
        if (attackerBody.intersects(targetBody)) {
            return true;
        }

        Rectangle reach = reachFrom(attackerBody, facing, reachWidth, reachHeight);
        return reach.intersects(targetBody);
    }

    public Direction directionToward(WorldBody attacker, WorldBody target) {
        if (attacker == null || target == null) {
            return Direction.DOWN;
        }

        int dx = target.getWorldX() - attacker.getWorldX();
        int dy = target.getWorldY() - attacker.getWorldY();
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx >= 0 ? Direction.RIGHT : Direction.LEFT;
        }
        return dy >= 0 ? Direction.DOWN : Direction.UP;
    }

    private Rectangle reachFrom(Rectangle body, Direction facing, int reachWidth, int reachHeight) {
        Rectangle reach = new Rectangle(body);
        int width = Math.max(1, reachWidth);
        int height = Math.max(1, reachHeight);
        Direction direction = facing == null ? Direction.RIGHT : facing;

        switch (direction) {
            case UP -> {
                reach.y -= height;
                reach.height += height;
            }
            case DOWN -> reach.height += height;
            case LEFT -> {
                reach.x -= width;
                reach.width += width;
            }
            case RIGHT -> reach.width += width;
        }
        return reach;
    }
}
