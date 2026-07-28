package combat;

import entity.Entity;
import main.DebugLog;
import monster_data.Monster;
import player_manager.Player;

import java.util.List;

public final class CombatSystem {
    private CombatSystem() {}

    public static boolean isAttacking(CombatComponent component) {
        return component.isAttacking();
    }

    public static int getPhase(CombatComponent component) {
        return component.getAttackPhase();
    }

    public static boolean isAttackActive(CombatComponent component) {
        return component.isAttackActive();
    }

    public static boolean canStartAttack(CombatComponent component) {
        return AttackPhaseSystem.canStart(component);
    }

    public static void startAttack(CombatComponent component, CombatContext owner) {
        if (owner == null) return;

        if (owner instanceof Entity entity) {
            if (entity.getGamePanel() != null) {
                DebugLog.info("[ATTACK START] by=" + entity.getName()
                        + " frame=" + entity.getGamePanel().getFrameCounter()
                        + " phase=" + component.getAttackPhase());
            } else {
                DebugLog.info("[ATTACK START] by=" + entity.getName()
                        + " phase=" + component.getAttackPhase());
            }
        } else {
            DebugLog.info("[ATTACK START] by=" + owner.getClass().getSimpleName()
                    + " phase=" + component.getAttackPhase());
        }

        AttackPhaseSystem.start(component, owner);
    }

    public static void update(CombatComponent component, CombatContext owner) {
        AttackPhaseSystem.update(component, owner);
    }

    public static boolean wasHitThisSwing(CombatComponent component, Object target) {
        return component.wasHitThisSwing(target);
    }

    public static void markHitLanded(CombatComponent component, Object target) {
        component.markHit(target);
    }

    public static void updateStatus(Entity entity) {
        StatusSystem.update(entity);
    }

    public static void tick(Entity entity) {
        if (entity == null) return;
        entity.tickCombat();
    }

    public static int[] computePlayerAttackKnockback(Player player) {
        return KnockbackService.forPlayerAttack(player);
    }

    public static int[] computeMonsterAttackKnockback(Monster monster, Player player) {
        return KnockbackService.forMonsterAttack(monster, player);
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
