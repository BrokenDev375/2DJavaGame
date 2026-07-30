package player_manager;

public final class PlayerProgression {
    private static final int DEFAULT_BASE_HP = 15;
    private static final int DEFAULT_BASE_ATTACK = 3;
    private static final int DEFAULT_BASE_DEFENSE = 2;
    private static final int DEFAULT_HP_PER_LEVEL = 3;
    private static final int DEFAULT_ATTACK_PER_LEVEL = 1;
    private static final int DEFAULT_DEFENSE_PER_LEVEL = 1;

    private final int baseHp;
    private final int baseAttack;
    private final int baseDefense;
    private final int hpPerLevel;
    private final int attackPerLevel;
    private final int defensePerLevel;

    private int level = 1;
    private int exp = 0;
    private int expToNext = calcExpToNext(1);

    public PlayerProgression() {
        this(
                DEFAULT_BASE_HP,
                DEFAULT_BASE_ATTACK,
                DEFAULT_BASE_DEFENSE,
                DEFAULT_HP_PER_LEVEL,
                DEFAULT_ATTACK_PER_LEVEL,
                DEFAULT_DEFENSE_PER_LEVEL
        );
    }

    public PlayerProgression(
            int baseHp,
            int baseAttack,
            int baseDefense,
            int hpPerLevel,
            int attackPerLevel,
            int defensePerLevel
    ) {
        this.baseHp = Math.max(1, baseHp);
        this.baseAttack = Math.max(0, baseAttack);
        this.baseDefense = Math.max(0, baseDefense);
        this.hpPerLevel = Math.max(0, hpPerLevel);
        this.attackPerLevel = Math.max(0, attackPerLevel);
        this.defensePerLevel = Math.max(0, defensePerLevel);
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

    public void reset() {
        setLevel(1);
        setExp(0);
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
        this.expToNext = calcExpToNext(this.level);
    }

    public void setExp(int exp) {
        this.exp = Math.max(0, exp);
    }

    public PlayerProgressionStats statsForCurrentLevel() {
        return new PlayerProgressionStats(
                baseHp + (level - 1) * hpPerLevel,
                baseAttack + (level - 1) * attackPerLevel,
                baseDefense + (level - 1) * defensePerLevel
        );
    }

    public PlayerProgressionResult gainExp(int amount) {
        if (amount <= 0) {
            return new PlayerProgressionResult(0, 0, level, exp, expToNext);
        }

        exp += amount;

        int levelsGained = 0;
        while (exp >= expToNext) {
            exp -= expToNext;
            level++;
            expToNext = calcExpToNext(level);
            levelsGained++;
        }

        return new PlayerProgressionResult(amount, levelsGained, level, exp, expToNext);
    }

    public static int calcExpToNext(int level) {
        int safeLevel = Math.max(1, level);
        double base = 10.0;
        return (int) Math.round(base * Math.pow(1.2, safeLevel - 1));
    }
}
