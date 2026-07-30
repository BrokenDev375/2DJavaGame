package object_data.items;

import main.GamePanel;
import object_data.WorldObject;

import java.awt.Rectangle;

public class ObjectKey extends WorldObject {

    public ObjectKey(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);
        setName("key");
        setSize(gp.tileSize() * 3 / 5, gp.tileSize() * 3 / 5);
        useStaticImage(setup("/object/key", getWidth(), getHeight()));
        setCollidable(false);
        setSolidArea(new Rectangle(
                -gp.tileSize() / 4,
                -gp.tileSize() / 4,
                getWidth() + gp.tileSize() / 2,
                getHeight() + gp.tileSize() / 2
        ));
    }
}
