package player_manager;

import entity.Direction;
import main.GamePanel;

public class PlayerMovement {
    private final Player player;
    private final PlayerInteractor pi;

    public PlayerMovement(Player player, GamePanel gp) {
        this.player = player;
        this.pi = new PlayerInteractor(player, gp);
    }

    public PlayerMoveIntent calculateMovement() {
        if (!isMoving()) {
            return PlayerMoveIntent.idle();
        }

        int dirX = 0;
        int dirY = 0;
        Direction facingDirection = player.getDirection();

        if (player.isMoveUpPressed()) {
            facingDirection = Direction.UP;
            dirY -= 1;
        }
        if (player.isMoveDownPressed()) {
            facingDirection = Direction.DOWN;
            dirY += 1;
        }
        if (player.isMoveLeftPressed()) {
            facingDirection = Direction.LEFT;
            dirX -= 1;
        }
        if (player.isMoveRightPressed()) {
            facingDirection = Direction.RIGHT;
            dirX += 1;
        }

        int deltaMoveX;
        int deltaMoveY;

        if (dirX != 0 && dirY != 0) {
            int diagonalStep = (int) Math.round(player.getActualSpeed() / Math.sqrt(2.0));
            deltaMoveX = dirX * diagonalStep;
            deltaMoveY = dirY * diagonalStep;
        } else {
            deltaMoveX = dirX * player.getActualSpeed();
            deltaMoveY = dirY * player.getActualSpeed();
        }

        return PlayerMoveIntent.move(facingDirection, deltaMoveX, deltaMoveY);
    }

    public boolean isMoving() {
        return player.isMoveUpPressed() || player.isMoveDownPressed()
                || player.isMoveLeftPressed() || player.isMoveRightPressed();
    }

    public void move(PlayerMoveIntent intent) {
        if (intent == null || !intent.isMoving()) {
            return;
        }

        player.face(intent.getFacingDirection());
        move(intent.getDeltaX(), intent.getDeltaY());
    }

    private void move(int dx, int dy) {
        player.clearCollisionXState();

        int nextX = player.getWorldX() + dx;
        int nextY = player.getWorldY();
        pi.allCheck(nextX, nextY);
        if (player.canMoveOnX()) {
            player.moveBy(dx, 0);
        }

        player.clearCollisionYState();

        nextX = player.getWorldX();
        nextY = player.getWorldY() + dy;
        pi.allCheck(nextX, nextY);
        if (player.canMoveOnY()) {
            player.moveBy(0, dy);
        }
    }
}
