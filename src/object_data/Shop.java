package object_data;

import main.GamePanel;

import java.awt.Rectangle;

public class Shop extends WorldObject {

    public Shop(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);
        setName("shop");
        setSize(gp.tileSize * 4, gp.tileSize * 4);
        useStaticImage(setup("/object/shop", getWidth(), getHeight()));
        setCollidable(false);
        setSolidArea(new Rectangle(0, 0, getWidth(), getHeight()));
    }
}
