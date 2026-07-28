package combat;

import entity.Direction;
import entity.Entity;
import monster_data.Monster;

import java.awt.Rectangle;

public final class AttackPhaseSystem {
    private AttackPhaseSystem() {}

    public static boolean canStart(CombatComponent component) {
        return !component.isAttacking() && component.getCooldownCounterFrames() == 0;
    }

    public static void start(CombatComponent component, CombatContext owner) {
        if (owner == null || owner.isDead()) return;
        if (component.getAttackWidth() <= 0 || component.getAttackHeight() <= 0) return;

        component.setIsAttacking(true);
        component.setAttackPhaseInternal(1);
        component.setPhaseTimerFrames(component.getWindupFrames());
        alignAttackBox(component, owner);
        component.clearHitThisSwing();
    }

    public static void update(CombatComponent component, CombatContext owner) {
        if (owner == null || owner.isDead()) {
            stopImmediately(component);
            return;
        }

        tickCooldown(component);

        if (!component.isAttacking()) return;

        if (component.getAttackPhase() == 2) {
            alignAttackBox(component, owner);
        }

        int timer = Math.max(0, component.getPhaseTimerFrames() - 1);
        component.setPhaseTimerFrames(timer);
        if (timer > 0) return;

        advancePhase(component, owner);
    }

    private static void stopImmediately(CombatComponent component) {
        component.setIsAttacking(false);
        component.setAttackPhaseInternal(0);
        component.setCooldownCounterFrames(0);
        component.clearAttackBox();
        component.clearHitThisSwing();
    }

    private static void tickCooldown(CombatComponent component) {
        int cooldown = component.getCooldownCounterFrames();
        if (cooldown > 0) {
            component.setCooldownCounterFrames(Math.max(0, cooldown - 1));
        }
    }

    private static void advancePhase(CombatComponent component, CombatContext owner) {
        int phase = component.getAttackPhase();
        if (phase == 1) {
            component.setAttackPhaseInternal(2);
            component.setPhaseTimerFrames(component.getActiveFrames());
            alignAttackBox(component, owner);
            component.clearHitThisSwing();
            return;
        }

        if (phase == 2) {
            component.setAttackPhaseInternal(3);
            component.setPhaseTimerFrames(component.getRecoverFrames());
            component.clearAttackBox();
            component.clearHitThisSwing();
            return;
        }

        component.setIsAttacking(false);
        component.setAttackPhaseInternal(0);
        component.setCooldownCounterFrames(component.getCooldownFrames());
        component.clearAttackBox();
        component.clearHitThisSwing();
    }

    private static void alignAttackBox(CombatComponent component, CombatContext owner) {
        Rectangle body = owner.getSolidArea();
        int bodyX = owner.getWorldX() + body.x;
        int bodyY = owner.getWorldY() + body.y;
        int bodyWidth = body.width;
        int bodyHeight = body.height;

        Direction direction = resolveAttackDirection(owner);

        int attackX;
        int attackY;
        switch (direction) {
            case UP:
                attackX = bodyX + (bodyWidth - component.getAttackWidth()) / 2;
                attackY = bodyY - component.getAttackHeight();
                break;
            case DOWN:
                attackX = bodyX + (bodyWidth - component.getAttackWidth()) / 2;
                attackY = bodyY + bodyHeight;
                break;
            case LEFT:
                attackX = bodyX - component.getAttackWidth();
                attackY = bodyY + (bodyHeight - component.getAttackHeight()) / 2;
                break;
            case RIGHT:
            default:
                attackX = bodyX + bodyWidth;
                attackY = bodyY + (bodyHeight - component.getAttackHeight()) / 2;
                break;
        }

        component.placeAttackBox(attackX, attackY);
    }

    private static Direction resolveAttackDirection(CombatContext owner) {
        Direction direction = owner.getDirection();
        if (owner instanceof Entity entity
                && entity instanceof Monster
                && entity.isAttacking()
                && entity.getAttackDirection() != null) {
            direction = entity.getAttackDirection();
        }

        return direction == null ? Direction.RIGHT : direction;
    }
}
