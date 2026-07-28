package combat;

import entity.Entity;

public final class StatusSystem {
    private StatusSystem() {}


    public static void update(Entity e) {
        if (e == null) return;

        e.tickInvulnerability();
    }
}
