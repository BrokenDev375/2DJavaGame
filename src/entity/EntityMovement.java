package entity;

import main.GamePanel;
import object_data.WorldObject;
import player_manager.Player;

import java.util.List;

public class EntityMovement {
    private final GamePanel gp;

    public EntityMovement(GamePanel gp) {
        this.gp = gp;
    }

    public void moveByDirection(Entity entity) {
        Direction direction = entity.getDirection();
        int speed = entity.getActualSpeed();
        moveWithDelta(entity, direction.scaledDx(speed), direction.scaledDy(speed));
    }

    public void moveWithDelta(Entity entity, int dx, int dy) {
        entity.clearCollisionState();

        int nextX = entity.getWorldX() + dx;
        int nextY = entity.getWorldY() + dy;

        gp.getCollisionChecker().checkTile(entity, nextX, nextY);

        List<WorldObject> objects = gp.getObjectManager().getObjects(gp.getCurrentMap());
        int objectIndex = gp.getCollisionChecker().checkWorldObject(entity, objects, dx, dy);
        if (objectIndex != 999) {
            WorldObject object = objects.get(objectIndex);
            if (object != null && object.isCollidable()) entity.markCollision();
        }

        gp.getCollisionChecker().checkPlayer(entity, nextX, nextY);

        if (entity.canMove()) {
            entity.moveBy(dx, dy);
        }
    }

    public void applyKnockback(Entity entity) {
        int velocityX = clamp(entity.knockbackVelocityX(), -24, 24);
        int velocityY = clamp(entity.knockbackVelocityY(), -24, 24);

        moveOnePixelSteps(entity, velocityX, 0);
        moveOnePixelSteps(entity, 0, velocityY);

        entity.tickKnockbackDuration();
        if (entity.isKnockbackFinished() || !entity.hasKnockbackVelocity()) {
            entity.finishKnockback();
        }
    }

    private void moveOnePixelSteps(Entity entity, int velocityX, int velocityY) {
        if (velocityX == 0 && velocityY == 0) return;

        int stepX = Integer.compare(velocityX, 0);
        int stepY = Integer.compare(velocityY, 0);
        int steps = Math.max(Math.abs(velocityX), Math.abs(velocityY));

        for (int i = 0; i < steps; i++) {
            if (willCollide(entity, stepX, stepY)) {
                if (stepX != 0) entity.stopKnockbackVelocityX();
                if (stepY != 0) entity.stopKnockbackVelocityY();
                return;
            }
            entity.moveBy(stepX, stepY);
        }
    }

    private boolean willCollide(Entity entity, int dx, int dy) {
        entity.clearCollisionState();

        int nextX = entity.getWorldX() + dx;
        int nextY = entity.getWorldY() + dy;

        gp.getCollisionChecker().checkTile(entity, nextX, nextY);

        List<WorldObject> objects = gp.getObjectManager().getObjects(gp.getCurrentMap());
        int objectIndex = gp.getCollisionChecker().checkWorldObject(entity, objects, dx, dy);
        if (objectIndex != 999) {
            WorldObject object = objects.get(objectIndex);
            if (object != null && object.isCollidable()) entity.markCollision();
        }

        if (!(entity instanceof Player)) {
            gp.getCollisionChecker().checkPlayer(entity, nextX, nextY);
        }

        return !entity.canMove();
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
