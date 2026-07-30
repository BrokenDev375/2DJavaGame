package player_manager;

public final class PlayerProgressionResult {
    private final int expGained;
    private final int levelsGained;
    private final int level;
    private final int exp;
    private final int expToNext;

    PlayerProgressionResult(int expGained, int levelsGained, int level, int exp, int expToNext) {
        this.expGained = Math.max(0, expGained);
        this.levelsGained = Math.max(0, levelsGained);
        this.level = Math.max(1, level);
        this.exp = Math.max(0, exp);
        this.expToNext = Math.max(1, expToNext);
    }

    public int expGained() {
        return expGained;
    }

    public int levelsGained() {
        return levelsGained;
    }

    public boolean leveledUp() {
        return levelsGained > 0;
    }

    public int level() {
        return level;
    }

    public int exp() {
        return exp;
    }

    public int expToNext() {
        return expToNext;
    }
}
