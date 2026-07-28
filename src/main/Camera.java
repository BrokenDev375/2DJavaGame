package main;

import entity.Entity;
import world.WorldBody;

public final class Camera {
    private final GamePanel gp;

    public Camera(GamePanel gp) {
        this.gp = gp;
    }

    public int anchorX() {
        return gp.screenWidth / 2 - gp.tileSize / 2;
    }

    public int anchorY() {
        return gp.screenHeight / 2 - gp.tileSize / 2;
    }

    public int originX(Entity focus) {
        return focus.getWorldX() - anchorX();
    }

    public int originY(Entity focus) {
        return focus.getWorldY() - anchorY();
    }

    public int screenX(int worldX, Entity focus) {
        return worldX - originX(focus);
    }

    public int screenY(int worldY, Entity focus) {
        return worldY - originY(focus);
    }

    public int screenX(WorldBody body, Entity focus) {
        return screenX(body.getWorldX(), focus);
    }

    public int screenY(WorldBody body, Entity focus) {
        return screenY(body.getWorldY(), focus);
    }

    public int visibleLeft(Entity focus, int buffer) {
        return originX(focus) - buffer;
    }

    public int visibleRight(Entity focus, int buffer) {
        return originX(focus) + gp.screenWidth + buffer;
    }

    public int visibleTop(Entity focus, int buffer) {
        return originY(focus) - buffer;
    }

    public int visibleBottom(Entity focus, int buffer) {
        return originY(focus) + gp.screenHeight + buffer;
    }

    public boolean isVisible(WorldBody body, Entity focus, int buffer) {
        int right = body.getWorldX() + body.getWidth();
        int bottom = body.getWorldY() + body.getHeight();
        return right >= visibleLeft(focus, buffer)
                && body.getWorldX() <= visibleRight(focus, buffer)
                && bottom >= visibleTop(focus, buffer)
                && body.getWorldY() <= visibleBottom(focus, buffer);
    }
}
