package object_data.weapons;

import main.GamePanel;

import java.awt.Rectangle;

public final class Pick extends Weapon {

    public Pick(GamePanel gp, int mapIndex) {
        super(gp, mapIndex);
        setName("Steve's pick");
        setCollidable(false);

        int t = gp.tileSize() / 4;
        setSolidArea(new Rectangle(-t / 2, -t / 2, getWidth() + t, getHeight() + t));
        loadSprite();
    }

    @Override public String spriteKey() { return "pick"; }
    @Override public int atkBoxW() { return 32; }
    @Override public int atkBoxH() { return 32; }
    @Override public int windup() { return 4; }
    @Override public int active() { return 6; }
    @Override public int recover() { return 8; }
    @Override public int cooldown() { return 12; }
    @Override public float atkMultiplier() { return 0.70f; }
    @Override public int atkFlat() { return 0; }
}
