package object_data.items;

import main.GamePanel;
import object_data.WorldObject;

import java.awt.Rectangle;

public class HealthPosion extends WorldObject {

    public HealthPosion(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);
        setName("healthposion");
        setSize(gp.tileSize, gp.tileSize);
        useStaticImage(setup("/object/healthposion", getWidth(), getHeight()));
        setCollidable(false);

        int t = gp.tileSize / 8;
        setSolidArea(new Rectangle(-t / 2, -t / 2, getWidth() + t, getHeight() + t));
    }
}
