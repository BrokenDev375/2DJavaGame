package combat;

import entity.Entity;
import monster_data.Monster;
import player_manager.Player;

import java.util.List;

public final class HitResolvePlayer {
    private HitResolvePlayer() {}

    public static void resolve(Player player, List<Entity> monsters) {
        if (player == null || player.isDead()) return;
        int rawDamage = Math.max(1, player.getATK());
        int[] knockback = CombatSystem.computePlayerAttackKnockback(player);

        for (Entity e : monsters) {
            if (!(e instanceof Monster)) continue;
            Monster m = (Monster) e;
            if (m.isDead()) continue;

            if (player.tryLandAttackOn(m)) {
                DamageProcessor.applyDamage(m, player, rawDamage, knockback[0], knockback[1]);
            }
        }
    }
}
