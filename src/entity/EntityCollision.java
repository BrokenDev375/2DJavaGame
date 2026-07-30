package entity;

import java.awt.Rectangle;

final class EntityCollision {
    private Rectangle solidArea;
    private int solidAreaDefaultX;
    private int solidAreaDefaultY;

    private boolean collisionXOn = false;
    private boolean collisionYOn = false;
    private boolean collisionOn = false;

    boolean canMove() {
        return !collisionOn;
    }

    boolean canMoveOnX() {
        return !collisionXOn && !collisionOn;
    }

    boolean canMoveOnY() {
        return !collisionYOn && !collisionOn;
    }

    boolean wasBlockedByCollision() {
        return collisionXOn || collisionYOn || collisionOn;
    }

    void clearCollisionState() {
        collisionOn = false;
        collisionXOn = false;
        collisionYOn = false;
    }

    void clearCollisionXState() {
        collisionOn = false;
        collisionXOn = false;
    }

    void clearCollisionYState() {
        collisionOn = false;
        collisionYOn = false;
    }

    void markCollision() {
        collisionOn = true;
    }

    void markCollisionX() {
        collisionXOn = true;
        markCollision();
    }

    void markCollisionY() {
        collisionYOn = true;
        markCollision();
    }

    void defineSolidArea(Rectangle area) {
        if (area == null) {
            solidArea = null;
            solidAreaDefaultX = 0;
            solidAreaDefaultY = 0;
            return;
        }

        solidArea = new Rectangle(area);
        solidAreaDefaultX = area.x;
        solidAreaDefaultY = area.y;
    }

    Rectangle getSolidArea(int fallbackWidth, int fallbackHeight) {
        if (solidArea == null) {
            return new Rectangle(0, 0, Math.max(1, fallbackWidth), Math.max(1, fallbackHeight));
        }

        return new Rectangle(
                solidAreaDefaultX,
                solidAreaDefaultY,
                solidArea.width,
                solidArea.height
        );
    }

    Rectangle getSolidAreaAt(int worldX, int worldY, int fallbackWidth, int fallbackHeight) {
        Rectangle area = getSolidArea(fallbackWidth, fallbackHeight);
        return new Rectangle(
                worldX + area.x,
                worldY + area.y,
                area.width,
                area.height
        );
    }
}
