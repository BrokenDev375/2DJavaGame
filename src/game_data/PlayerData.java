package game_data;

public class PlayerData {
    private int worldX;
    private int worldY;
    private int health;
    private int maxHealth;
    private String weaponName;
    private int mapIndex;
    private int exp;
    private int level;
    private int keyCount;

    private PlayerData() {
        // Used by Gson.
    }

    public PlayerData(int worldX, int worldY, int health, int maxHealth,
                      String weaponName, int mapIndex, int exp , int level) {
        this(worldX, worldY, health, maxHealth, weaponName, mapIndex, exp, level, 0);
    }

    public PlayerData(int worldX, int worldY, int health, int maxHealth,
                      String weaponName, int mapIndex, int exp, int level, int keyCount) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.health = health;
        this.maxHealth = maxHealth;
        this.weaponName = weaponName;
        this.mapIndex = mapIndex;
        this.exp = exp;
        this.level = level;
        this.keyCount = Math.max(0, keyCount);
    }

    public int getWorldX() {
        return worldX;
    }

    public int getWorldY() {
        return worldY;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public String getWeaponName() {
        return weaponName;
    }

    public int getMapIndex() {
        return mapIndex;
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public int getKeyCount() {
        return Math.max(0, keyCount);
    }
}
