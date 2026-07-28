package object_data.items;

import main.GamePanel;
import object_data.WorldObject;

import java.awt.Rectangle;

public class ObjectChest extends WorldObject {

    private boolean opened = false;

    public ObjectChest(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);
        setName("chest");
        setSize(gp.tileSize, gp.tileSize);
        setStaticImage(setup("/object/chest", getWidth(), getHeight()));
        setCollidable(true);
        setSolidArea(new Rectangle(2, 4, getWidth() - 4, getHeight() - 8));
    }

    public void open() {
        if (opened) return;
        opened = true;
        // setStaticImage(setup("/object/chest_open", getWidth(), getHeight()));
    }

    public boolean isOpened() {
        return opened;
    }
}
