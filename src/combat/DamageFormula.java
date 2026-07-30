package combat;

public final class DamageFormula {
    private static final int MITIGATION_BASE = 100;
    private static final int DEFENSE_SCALE = 10;

    private DamageFormula() {}

    public static int afterDefense(int rawDamage, int defense) {
        int safeRawDamage = Math.max(1, rawDamage);
        int safeDefense = Math.max(0, defense);
        long scaledDefense = (long) safeDefense * DEFENSE_SCALE;
        double multiplier = (double) MITIGATION_BASE / (MITIGATION_BASE + scaledDefense);
        return Math.max(1, (int) Math.round(safeRawDamage * multiplier));
    }
}
