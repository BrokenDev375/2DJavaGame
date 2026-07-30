package object_data.weapons;

import combat.DamageFormula;
import entity.Entity;
import main.GamePanel;
import object_data.WorldObject;
import player_manager.Player;

public abstract class Weapon extends WorldObject {

    public Weapon(GamePanel gp) {
        this(gp, gp.getCurrentMap());
    }

    public Weapon(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);
        setCollidable(true);
        setSize(gp.tileSize(), gp.tileSize());
        setSolidArea(new java.awt.Rectangle(8, 8, gp.tileSize() - 16, gp.tileSize() - 16));
    }

    public abstract String spriteKey();

    public abstract int atkBoxW();
    public abstract int atkBoxH();
    public abstract int windup();
    public abstract int active();
    public abstract int recover();
    public abstract int cooldown();
    public abstract float atkMultiplier();
    public abstract int atkFlat();

    public void loadSprite() {
        useStaticImage(setup("/object/" + spriteKey(), gp.tileSize(), gp.tileSize()));
    }

    public int attackPower(Player p) {
        if (p == null) {
            return Math.max(1, atkFlat());
        }
        return Math.max(1, Math.round(p.getATK() * atkMultiplier()) + atkFlat());
    }

    public int computeDamage(Player p, Entity target) {
        int defense = target == null ? 0 : target.getDEF();
        return DamageFormula.afterDefense(attackPower(p), defense);
    }
}
