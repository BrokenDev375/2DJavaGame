package combat;

import monster_data.Monster;
import player_manager.Player;

public final class HitResolveMonster {
    private HitResolveMonster() {}

    public static void resolve(Monster m, Player player) {
        if (m == null || player == null || m.isDead() || player.isDead()) return;
        if (!m.tryLandAttackOn(player)) return;

        int rawDamage = m.attackPower();
        int[] kb = CombatSystem.computeMonsterAttackKnockback(m, player);

        DamageProcessor.applyDamage(player, m, rawDamage, kb[0], kb[1]);

    }
}
