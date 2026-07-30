package object_data.items;

import main.GamePanel;
import object_data.WorldObject;

import java.awt.Rectangle;

public class ManaPosion extends WorldObject {

    public ManaPosion(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);
        setName("manaposion");
        setSize(gp.tileSize, gp.tileSize);
        useStaticImage(setup("/object/manaposion", getWidth(), getHeight()));
        setCollidable(false);

        int t = gp.tileSize / 8;
        setSolidArea(new Rectangle(-t / 2, -t / 2, getWidth() + t, getHeight() + t));
    }
}
