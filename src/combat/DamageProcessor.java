package combat;

import entity.Entity;

public final class DamageProcessor {
    private DamageProcessor(){}

    public static void applyDamage(Entity target, int rawDamage, int knockbackX, int knockbackY) {
        applyDamage(target, null, rawDamage, knockbackX, knockbackY);
    }

    public static void applyDamage(Entity target, Entity attacker, int rawDamage, int knockbackX, int knockbackY) {
        if (target == null) return;
        target.takeDamage(attacker, rawDamage, knockbackX, knockbackY);
    }
}
