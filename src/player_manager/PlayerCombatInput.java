package player_manager;

public final class PlayerCombatInput {
    private final int lockFrames;
    private int attackButtonLock;

    public PlayerCombatInput() {
        this(6);
    }

    public PlayerCombatInput(int lockFrames) {
        this.lockFrames = Math.max(1, lockFrames);
    }

    public boolean shouldStartAttack(boolean attackPressed, boolean canStartAttack) {
        if (attackButtonLock > 0) {
            attackButtonLock--;
        }

        if (!attackPressed || attackButtonLock > 0) {
            return false;
        }

        attackButtonLock = lockFrames;
        return canStartAttack;
    }

    public int attackButtonLock() {
        return attackButtonLock;
    }

    public void reset() {
        attackButtonLock = 0;
    }
}
