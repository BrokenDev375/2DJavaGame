package player_manager;

public final class PlayerProgressionStats {
    private final int maxHp;
    private final int attack;
    private final int defense;

    PlayerProgressionStats(int maxHp, int attack, int defense) {
        this.maxHp = Math.max(1, maxHp);
        this.attack = Math.max(0, attack);
        this.defense = Math.max(0, defense);
    }

    public int maxHp() {
        return maxHp;
    }

    public int attack() {
        return attack;
    }

    public int defense() {
        return defense;
    }
}
