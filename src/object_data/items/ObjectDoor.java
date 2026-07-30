package object_data.items;

import main.GamePanel;
import object_data.TeleportDestination;
import object_data.TeleportTarget;
import object_data.WorldObject;

import java.awt.Rectangle;
import java.util.Optional;

public class ObjectDoor extends WorldObject implements TeleportTarget {
    private TeleportDestination teleportDestination;

    public ObjectDoor(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);
        setName("door");
        setSize(gp.tileSize, gp.tileSize);
        useStaticImage(setup("/object/door", getWidth(), getHeight()));
        setCollidable(false);
        setSolidArea(new Rectangle(2, 2, getWidth() - 4, getHeight() - 4));
    }

    @Override
    public void setTeleportDestination(TeleportDestination destination) {
        this.teleportDestination = destination;
    }

    @Override
    public Optional<TeleportDestination> teleportDestination() {
        return Optional.ofNullable(teleportDestination);
    }
}
