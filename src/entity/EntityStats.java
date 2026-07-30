package entity;

import combat.DamageFormula;

final class EntityStats {
    private int hp = 1;
    private int maxHp = 1;
    private int atk = 1;
    private int def = 0;

    void configure(int maxHp, int atk, int def) {
        this.maxHp = Math.max(1, maxHp);
        this.hp = this.maxHp;
        this.atk = Math.max(0, atk);
        this.def = Math.max(0, def);
    }

    int hp() {
        return hp;
    }

    int maxHp() {
        return maxHp;
    }

    int attack() {
        return atk;
    }

    int defense() {
        return def;
    }

    void restoreHp(int value) {
        hp = Math.max(0, Math.min(value, maxHp));
    }

    void refillHp() {
        restoreHp(maxHp);
    }

    void heal(int amount) {
        if (amount <= 0) return;
        restoreHp(hp + amount);
    }

    void healPercent(double percent) {
        if (percent <= 0) return;
        int healAmount = (int) Math.round(maxHp * percent);
        heal(Math.max(1, healAmount));
    }

    void kill() {
        restoreHp(0);
    }

    int reduceHp(int amount) {
        int damage = Math.max(0, amount);
        int oldHp = hp;
        hp = Math.max(0, hp - damage);
        return oldHp;
    }

    int damageAfterDefense(int rawDamage) {
        return DamageFormula.afterDefense(rawDamage, def);
    }

    boolean isDead() {
        return hp <= 0;
    }
}
