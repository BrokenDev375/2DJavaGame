package player_manager;

import entity.Direction;

public final class PlayerMoveIntent {
    private static final PlayerMoveIntent IDLE = new PlayerMoveIntent(null, 0, 0);

    private final Direction facingDirection;
    private final int deltaX;
    private final int deltaY;

    private PlayerMoveIntent(Direction facingDirection, int deltaX, int deltaY) {
        this.facingDirection = facingDirection;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public static PlayerMoveIntent move(Direction facingDirection, int deltaX, int deltaY) {
        if (deltaX == 0 && deltaY == 0) {
            return idle();
        }
        return new PlayerMoveIntent(facingDirection, deltaX, deltaY);
    }

    public static PlayerMoveIntent idle() {
        return IDLE;
    }

    public boolean isMoving() {
        return deltaX != 0 || deltaY != 0;
    }

    public Direction getFacingDirection() {
        return facingDirection;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }
}
