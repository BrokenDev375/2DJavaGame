package entity;

final class DamageState {
    private boolean invulnerable = false;
    private int invulnFrames = 20;
    private int invulnCounter = 0;
    private Entity lastHitBy = null;

    void configureInvulnerabilityFrames(int frames) {
        invulnFrames = Math.max(0, frames);
    }

    boolean isInvulnerable() {
        return invulnerable;
    }

    void startInvulnerability() {
        invulnerable = true;
        invulnCounter = invulnFrames;
    }

    void tickInvulnerability() {
        if (!invulnerable) return;

        invulnCounter--;
        if (invulnCounter <= 0) {
            invulnCounter = 0;
            invulnerable = false;
        }
    }

    Entity getLastHitBy() {
        return lastHitBy;
    }

    void markHitBy(Entity attacker) {
        lastHitBy = attacker;
    }

    void clearLastHitBy() {
        lastHitBy = null;
    }
}
