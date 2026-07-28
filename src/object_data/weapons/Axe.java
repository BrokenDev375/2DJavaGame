package object_data.weapons;

import main.GamePanel;

import java.awt.Rectangle;

public final class Axe extends Weapon {

    public Axe(GamePanel gp, int mapIndex) {
        super(gp, mapIndex);
        setName("Leviathan Axe");
        setCollidable(false);

        int t = gp.tileSize / 4;
        setSolidArea(new Rectangle(-t / 2, -t / 2, getWidth() + t, getHeight() + t));
        loadSprite();
    }

    @Override public String spriteKey() { return "axe"; }
    @Override public int atkBoxW() { return 40; }
    @Override public int atkBoxH() { return 40; }
    @Override public int windup() { return 10; }
    @Override public int active() { return 10; }
    @Override public int recover() { return 14; }
    @Override public int cooldown() { return 18; }
    @Override public float atkMultiplier() { return 1.4f; }
    @Override public int atkFlat() { return 0; }
}
