package combat;

import monster_data.Monster;
import player_manager.Player;

public final class HitResolveMonster {
    private HitResolveMonster() {}

    public static void resolve(Monster m, Player player) {
        if (m == null || player == null || m.isDead() || player.isDead()) return;
        if (!m.isAttackActive()) return;

        if (!m.isAttackHitting(player)) return;

        if (m.wasHitThisSwing(player)) return;

        int rawDamage = Math.max(1, m.getATK());
        int[] kb = CombatSystem.computeMonsterAttackKnockback(m, player);

        DamageProcessor.applyDamage(player, m, rawDamage, kb[0], kb[1]);

        m.markHitLanded(player);
    }
}
