package combat;

import entity.Entity;
import monster_data.Monster;
import player_manager.Player;
import world.WorldBody;

import java.util.List;

public final class CombatSystem {
    private CombatSystem() {}

    public static boolean isAttacking(CombatComponent component) {
        return component.isAttacking();
    }

    public static boolean isAttackActive(CombatComponent component) {
        return component.isAttackActive();
    }

    public static boolean isAttackHitting(CombatComponent component, WorldBody target) {
        return isAttackActive(component) && target != null && component.attackIntersects(target.getSolidAreaWorld());
    }

    public static boolean tryLandAttackOn(CombatComponent component, WorldBody target) {
        if (!isAttackHitting(component, target) || component.wasHitThisSwing(target)) {
            return false;
        }
        component.markHit(target);
        return true;
    }

    public static boolean canStartAttack(CombatComponent component) {
        return AttackPhaseSystem.canStart(component);
    }

    public static void startAttack(CombatComponent component, CombatContext owner) {
        if (owner == null) return;
        AttackPhaseSystem.start(component, owner);
    }

    public static void update(CombatComponent component, CombatContext owner) {
        AttackPhaseSystem.update(component, owner);
    }

    public static void configureAttackBox(CombatComponent component, int width, int height) {
        component.setAttackBoxSize(width, height);
    }

    public static void configureAttackTiming(CombatComponent component, int windup, int active, int recover, int cooldown) {
        component.setTimingFrames(windup, active, recover, cooldown);
    }

    public static int getKnockbackForce(CombatComponent component) {
        return component.getKnockbackForce();
    }

    public static void setKnockbackForce(CombatComponent component, int force) {
        component.setKnockbackForce(force);
    }

    public static void updateStatus(Entity entity) {
        StatusSystem.update(entity);
    }

    public static void tick(Entity entity) {
        if (entity == null) return;
        entity.tickCombat();
    }

    public static int[] computePlayerAttackKnockback(Player player) {
        return player == null ? new int[]{0, 0} : player.attackKnockbackVector();
    }

    public static int[] computeMonsterAttackKnockback(Monster monster, Player player) {
        return monster == null ? new int[]{0, 0} : monster.attackKnockbackAgainst(player);
    }

    public static void resolvePlayerHits(Player player, List<Entity> monsters) {
        HitResolvePlayer.resolve(player, monsters);
    }

    public static void resolveMonsterHitAgainstPlayer(Monster monster, Player player) {
        HitResolveMonster.resolve(monster, player);
    }

    public static void resolveMonsterHit(Monster monster, Player player) {
        resolveMonsterHitAgainstPlayer(monster, player);
    }
}
