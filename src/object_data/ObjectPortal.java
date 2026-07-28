package object_data;

import main.GamePanel;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class ObjectPortal extends WorldObject {

    private BufferedImage firstFrame;
    private BufferedImage secondFrame;
    private int animCounter = 0;
    private final int frameDuration = 10;

    private int targetMap;
    private int targetWorldX;
    private int targetWorldY;

    public ObjectPortal(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);
        setName("portal");
        setSize(gp.tileSize * 3 / 2, gp.tileSize * 3 / 2);

        firstFrame = setup("/object/portal1", getWidth(), getHeight());
        secondFrame = setup("/object/portal2", getWidth(), getHeight());

        setCollidable(false);
        setSolidArea(new Rectangle(2, 2, getWidth() - 4, getHeight() - 4));
    }

    public void setTarget(int mapIndex, int worldX, int worldY) {
        this.targetMap = mapIndex;
        this.targetWorldX = worldX;
        this.targetWorldY = worldY;
    }

    public int getTargetMap() {
        return targetMap;
    }

    public int getTargetWorldX() {
        return targetWorldX;
    }

    public int getTargetWorldY() {
        return targetWorldY;
    }

    @Override
    public void update() {
        animCounter++;
    }

    @Override
    public BufferedImage getRenderImage() {
        int idx = (animCounter / frameDuration) % 2;
        if (idx == 0) return firstFrame != null ? firstFrame : secondFrame;
        return secondFrame != null ? secondFrame : firstFrame;
    }
}
