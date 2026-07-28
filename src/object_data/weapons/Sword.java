package object_data.weapons;

import main.GamePanel;

import java.awt.Rectangle;

public final class Sword extends Weapon {

    public Sword(GamePanel gp, int mapIndex) {
        super(gp, mapIndex);
        setName("Argonaut hero's sword");
        setCollidable(false);

        int t = gp.tileSize / 4;
        setSolidArea(new Rectangle(-t / 2, -t / 2, getWidth() + t, getHeight() + t));
        loadSprite();
    }

    @Override public String spriteKey() { return "sword"; }
    @Override public int atkBoxW() { return 36; }
    @Override public int atkBoxH() { return 36; }
    @Override public int windup() { return 5; }
    @Override public int active() { return 8; }
    @Override public int recover() { return 10; }
    @Override public int cooldown() { return 14; }
    @Override public float atkMultiplier() { return 1.0f; }
    @Override public int atkFlat() { return 0; }
}
