package object_data;

import main.GamePanel;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class ObjectPortal extends WorldObject implements TeleportTarget {

    private BufferedImage firstFrame;
    private BufferedImage secondFrame;
    private int animCounter = 0;
    private final int frameDuration = 10;

    private TeleportDestination teleportDestination;

    public ObjectPortal(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);
        setName("portal");
        setSize(gp.tileSize() * 3 / 2, gp.tileSize() * 3 / 2);

        firstFrame = setup("/object/portal1", getWidth(), getHeight());
        secondFrame = setup("/object/portal2", getWidth(), getHeight());

        setCollidable(false);
        setSolidArea(new Rectangle(2, 2, getWidth() - 4, getHeight() - 4));
    }

    public void setTarget(int mapIndex, int worldX, int worldY) {
        setTeleportDestination(new TeleportDestination(mapIndex, worldX, worldY));
    }

    public int getTargetMap() {
        return teleportDestination == null ? 0 : teleportDestination.mapId();
    }

    public int getTargetWorldX() {
        return teleportDestination == null ? 0 : teleportDestination.worldX();
    }

    public int getTargetWorldY() {
        return teleportDestination == null ? 0 : teleportDestination.worldY();
    }

    @Override
    public void setTeleportDestination(TeleportDestination destination) {
        this.teleportDestination = destination;
    }

    @Override
    public Optional<TeleportDestination> teleportDestination() {
        return Optional.ofNullable(teleportDestination);
    }

    @Override
    public void update() {
        animCounter++;
    }

    @Override
    protected BufferedImage getRenderImage() {
        int idx = (animCounter / frameDuration) % 2;
        if (idx == 0) return firstFrame != null ? firstFrame : secondFrame;
        return secondFrame != null ? secondFrame : firstFrame;
    }
}
