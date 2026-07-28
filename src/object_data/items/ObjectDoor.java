package object_data.items;

import main.GamePanel;
import object_data.WorldObject;

import java.awt.Rectangle;

public class ObjectDoor extends WorldObject {

    public ObjectDoor(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);
        setName("door");
        setSize(gp.tileSize, gp.tileSize);
        setStaticImage(setup("/object/door", getWidth(), getHeight()));
        setCollidable(false);
        setSolidArea(new Rectangle(2, 2, getWidth() - 4, getHeight() - 4));
    }
}
