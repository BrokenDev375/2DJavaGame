package ai.movement;

import entity.Direction;
import entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WanderMovement implements MovementController {
    private final int speed;
    private final int changeEveryFrames;
    private final int minHoldFrames;
    private final boolean bounded;
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;
    private final int fencePadding;

    private int counter = 0;
    private int hold = 0;
    private Direction currentDir = Direction.DOWN;
    private final Random rng = new Random();

    public WanderMovement(int speed, int changeEveryFrames) {
        this(speed, changeEveryFrames, 8);
    }

    public WanderMovement(int speed, int changeEveryFrames, int minHoldFrames) {
        this.speed = speed;
        this.changeEveryFrames = Math.max(1, changeEveryFrames);
        this.minHoldFrames = Math.max(0, minHoldFrames);
        this.bounded = false;
        this.minX = this.minY = this.maxX = this.maxY = this.fencePadding = 0;
    }

    public WanderMovement(int speed, int changeEveryFrames, int minHoldFrames,
                          int minX, int minY, int maxX, int maxY, int fencePadding) {
        this.speed = speed;
        this.changeEveryFrames = Math.max(1, changeEveryFrames);
        this.minHoldFrames = Math.max(0, minHoldFrames);
        this.bounded = true;
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.fencePadding = Math.max(0, fencePadding);
    }

    @Override
    public MovementIntent decide(Entity e) {
        if (hold < minHoldFrames && !(bounded && nearFence(e, currentDir))) {
            hold++;
            return MovementIntent.move(currentDir, speed);
        }

        counter++;
        boolean needChange = counter >= changeEveryFrames
                || (bounded && nearFence(e, currentDir))
                || isBlocked(e);

        if (needChange) {
            counter = 0;
            hold = 0;

            Direction[] candidates = Direction.values();
            Direction[] valid = bounded ? filterValidDirections(e, candidates) : candidates;

            if (valid.length == 0) {
                valid = candidates;
            }

            Direction inward = bounded ? pickInwardIfNearFence(e) : null;
            if (inward != null && contains(valid, inward)) {
                currentDir = inward;
            } else {
                currentDir = valid[rng.nextInt(valid.length)];
            }
        }

        return MovementIntent.move(currentDir, speed);
    }

    private boolean isBlocked(Entity e) {
        return e.wasBlockedByCollision();
    }

    private Direction[] filterValidDirections(Entity e, Direction[] dirs) {
        List<Direction> ok = new ArrayList<>();
        for (Direction d : dirs) {
            if (!wouldExceedBounds(e, d)) {
                ok.add(d);
            }
        }
        return ok.toArray(new Direction[0]);
    }

    private boolean wouldExceedBounds(Entity e, Direction dir) {
        if (!bounded) {
            return false;
        }
        int nx = e.getWorldX() + dir.scaledDx(speed);
        int ny = e.getWorldY() + dir.scaledDy(speed);
        return nx < minX || nx > maxX || ny < minY || ny > maxY;
    }

    private boolean nearFence(Entity e, Direction dir) {
        int nx = e.getWorldX() + dir.scaledDx(speed);
        int ny = e.getWorldY() + dir.scaledDy(speed);
        return nx <= minX + fencePadding || nx >= maxX - fencePadding
                || ny <= minY + fencePadding || ny >= maxY - fencePadding;
    }

    private Direction pickInwardIfNearFence(Entity e) {
        if (!bounded) {
            return null;
        }
        if (e.getWorldY() <= minY + fencePadding) return Direction.DOWN;
        if (e.getWorldY() >= maxY - fencePadding) return Direction.UP;
        if (e.getWorldX() <= minX + fencePadding) return Direction.RIGHT;
        if (e.getWorldX() >= maxX - fencePadding) return Direction.LEFT;
        return null;
    }

    private boolean contains(Direction[] arr, Direction v) {
        for (Direction s : arr) {
            if (s == v) {
                return true;
            }
        }
        return false;
    }
}
