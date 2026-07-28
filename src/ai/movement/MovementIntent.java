package ai.movement;

import entity.Direction;

public final class MovementIntent {
    private static final MovementIntent STOP = new MovementIntent(null, 0);

    private final Direction direction;
    private final int speed;

    private MovementIntent(Direction direction, int speed) {
        this.direction = direction;
        this.speed = Math.max(0, speed);
    }

    public static MovementIntent move(Direction direction, int speed) {
        if (direction == null || speed <= 0) {
            return stop();
        }
        return new MovementIntent(direction, speed);
    }

    public static MovementIntent stop() {
        return STOP;
    }

    public boolean isMoving() {
        return direction != null && speed > 0;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getSpeed() {
        return speed;
    }
}
