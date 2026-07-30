package ai.movement;

import entity.Direction;
import entity.Entity;
import player_manager.Player;

import java.util.function.Supplier;

public final class ChaseMovement implements MovementController {
    private final Supplier<Player> targetSup;
    private final int moveSpeed;
    private final int stopRadius;

    private final int retargetEveryFrames = 8;
    private final int minHoldFrames = 8;
    private final int axisBiasPx = 6;

    private int counter = 0;
    private int hold = 0;
    private Direction currentDir = Direction.DOWN;

    public ChaseMovement(Supplier<Player> targetSup, int moveSpeed, int stopRadiusPx) {
        this.targetSup = targetSup;
        this.moveSpeed = moveSpeed;
        this.stopRadius = Math.max(0, stopRadiusPx);
    }

    @Override
    public MovementIntent decide(Entity e) {
        Player target = (targetSup != null) ? targetSup.get() : null;
        if (target == null) {
            return MovementIntent.stop();
        }

        int dx = target.getWorldX() - e.getWorldX();
        int dy = target.getWorldY() - e.getWorldY();

        long r2 = (long) stopRadius * (long) stopRadius;
        long d2 = (long) dx * (long) dx + (long) dy * (long) dy;
        if (d2 <= r2) {
            return MovementIntent.stop();
        }

        if (hold < minHoldFrames) {
            hold++;
            return MovementIntent.move(currentDir, moveSpeed);
        }

        counter++;
        if (counter >= retargetEveryFrames) {
            counter = 0;
            hold = 0;

            if (Math.abs(dx) > Math.abs(dy) + axisBiasPx) {
                currentDir = (dx >= 0) ? Direction.RIGHT : Direction.LEFT;
            } else if (Math.abs(dy) > Math.abs(dx) + axisBiasPx) {
                currentDir = (dy >= 0) ? Direction.DOWN : Direction.UP;
            }
        }

        return MovementIntent.move(currentDir, moveSpeed);
    }
}
