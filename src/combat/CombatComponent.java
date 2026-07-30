package combat;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

public class CombatComponent {
    private int windupFrames = 6;
    private int activeFrames = 6;
    private int recoverFrames = 10;
    private int cooldownFrames = 12;

    private int knockbackForce = 0;

    private int attackWidth = 36;
    private int attackHeight = 36;

    private boolean attacking = false;
    private int attackPhase = 0;
    private int phaseTimerFrames = 0;
    private int cooldownCounterFrames = 0;

    private final Rectangle attackBox = new Rectangle();
    private final Set<Object> hitThisSwing = new HashSet<>();

    void setAttackBoxSize(int width, int height) {
        this.attackWidth = Math.max(1, width);
        this.attackHeight = Math.max(1, height);
        this.attackBox.setSize(this.attackWidth, this.attackHeight);
    }

    boolean wasHitThisSwing(Object target) {
        return hitThisSwing.contains(target);
    }

    void markHit(Object target) {
        if (target != null) hitThisSwing.add(target);
    }

    void clearHitThisSwing() {
        hitThisSwing.clear();
    }

    void setTimingFrames(int windup, int active, int recover, int cooldown) {
        this.windupFrames = Math.max(0, windup);
        this.activeFrames = Math.max(0, active);
        this.recoverFrames = Math.max(0, recover);
        this.cooldownFrames = Math.max(0, cooldown);
    }

    int getKnockbackForce() {
        return knockbackForce;
    }

    void setKnockbackForce(int force) {
        this.knockbackForce = Math.max(0, force);
    }

    boolean isAttacking() {
        return attacking;
    }

    boolean isAttackActive() {
        return attacking && attackPhase == 2;
    }

    int attackPhase() {
        return attackPhase;
    }

    boolean isAttackBoxActive() {
        return attackBox.width > 0 && attackBox.height > 0;
    }

    boolean attackIntersects(Rectangle targetBody) {
        return targetBody != null && isAttackBoxActive() && attackBox.intersects(targetBody);
    }

    int getWindupFrames() {
        return windupFrames;
    }

    int getActiveFrames() {
        return activeFrames;
    }

    int getRecoverFrames() {
        return recoverFrames;
    }

    int getCooldownFrames() {
        return cooldownFrames;
    }

    int getAttackWidth() {
        return attackWidth;
    }

    int getAttackHeight() {
        return attackHeight;
    }

    void placeAttackBox(int x, int y) {
        attackBox.setBounds(x, y, attackWidth, attackHeight);
    }

    void clearAttackBox() {
        attackBox.setBounds(0, 0, 0, 0);
    }

    void setIsAttacking(boolean value) {
        attacking = value;
    }

    void setAttackPhaseInternal(int value) {
        attackPhase = value;
    }

    int getPhaseTimerFrames() {
        return phaseTimerFrames;
    }

    void setPhaseTimerFrames(int value) {
        phaseTimerFrames = value;
    }

    int getCooldownCounterFrames() {
        return cooldownCounterFrames;
    }

    void setCooldownCounterFrames(int value) {
        cooldownCounterFrames = value;
    }

    static void setCooldown(CombatComponent component, int frames) {
        component.setCooldownCounterFrames(Math.max(0, frames));
    }

    static int getCooldown(CombatComponent component) {
        return component.getCooldownCounterFrames();
    }

    static int attackWidth(CombatComponent component) {
        return component.getAttackWidth();
    }

    static int attackHeight(CombatComponent component) {
        return component.getAttackHeight();
    }
}
