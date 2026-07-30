package monster_data;

import entity.Direction;
import entity.Entity;
import main.DebugLog;
import main.GamePanel;
import player_manager.Player;

import java.util.Objects;

public abstract class Monster extends Entity {
    protected int attackDamage = 1;
    protected int attackKnockback = 6;
    protected int attackTriggerRadius = 36;
    protected int faceLockThreshold = 4;
    protected int atkW, atkH;
    protected int expReward = 1;

    private final MonsterAttackPlanner attackPlanner = new MonsterAttackPlanner();
    private LootDropPolicy lootDropPolicy = new LootDropPolicy();
    private int homeX, homeY;

    public Monster(GamePanel gp) {
        super(gp);

        configureAttackBox(gp.tileSize, gp.tileSize * 3 / 2);
        configureAttackTiming(8, 6, 10, 30);
        this.attackTriggerRadius = Math.max(gp.tileSize, 48);
        this.faceLockThreshold = 6;
        this.atkW = gp.tileSize;
        this.atkH = gp.tileSize * 3 / 2;
        configureAttackBox(atkW, atkH);
    }

    public int getExpReward() {
        return expReward;
    }

    public void setExpReward(int expReward) {
        this.expReward = Math.max(0, expReward);
    }

    public void setLootDropPolicy(LootDropPolicy lootDropPolicy) {
        this.lootDropPolicy = Objects.requireNonNull(lootDropPolicy, "lootDropPolicy");
    }

    protected void initExpFromStats() {
        int base = (int) (
                getMaxHP() * 0.1 +
                        getATK() * 1.5 +
                        getDEF() * 0.5
        );
        this.expReward = Math.max(1, base);
    }

    @Override
    public void update() {
        decideAttack();

        int preX = getWorldX();
        int preY = getWorldY();
        boolean holdPos = isAttacking();

        super.update();

        if (holdPos) {
            restorePosition(preX, preY);
        }
    }

    public void setHome(int x, int y) {
        this.homeX = x;
        this.homeY = y;
    }

    public int getHomeX() {
        return homeX;
    }

    public int getHomeY() {
        return homeY;
    }

    protected void decideAttack() {
        if (isAttacking()) return;

        Player player = gp.getEntityManager() != null ? gp.getEntityManager().getPlayer() : null;
        if (player == null || player.isDead()) return;

        int reachWidth = atkW > 0 ? atkW : gp.tileSize;
        int reachHeight = atkH > 0 ? atkH : gp.tileSize;
        if (attackPlanner.canReachTarget(this, getDirection(), player, reachWidth, reachHeight)) {
            tryStartAttackOn(player);
        }
    }

    protected void tryStartAttackOn(Player player) {
        if (!canStartAttack()) return;

        faceOnceToward(player);
        lockAttackDirection();
        startAttack();
    }

    public int[] attackKnockbackAgainst(Player player) {
        final int defaultForce = 3;
        final int maxForce = 3;
        if (player == null || player.isDead() || isDead()) return new int[]{0, 0};

        int force = getAttackKnockbackForce() > 0 ? getAttackKnockbackForce() : defaultForce;
        int dx = player.getWorldX() - getWorldX();
        int dy = player.getWorldY() - getWorldY();
        double length = Math.hypot(dx, dy);

        int knockbackX = length == 0 ? 0 : (int) Math.round(force * dx / length);
        int knockbackY = length == 0 ? 0 : (int) Math.round(force * dy / length);

        if (knockbackX == 0 && dx != 0) knockbackX = dx > 0 ? 1 : -1;
        if (knockbackY == 0 && dy != 0) knockbackY = dy > 0 ? 1 : -1;

        return new int[]{
                clamp(knockbackX, -maxForce, maxForce),
                clamp(knockbackY, -maxForce, maxForce)
        };
    }

    public MonsterDeathResult onDeath() {
        return createDeathResult();
    }

    protected MonsterDeathResult createDeathResult() {
        return MonsterDeathResult.of(
                getName(),
                getExpReward(),
                lootDropPolicy.rollHealthPotion(getMapIndex(), getWorldX(), getWorldY())
        );
    }

    @Override
    protected void reduceHP(int amount) {
        boolean wasDead = isDead();
        DebugLog.info("[DMG] " + getName() + " incoming=" + amount + " hp=" + getHP());

        super.reduceHP(amount);

        DebugLog.info("[DMG] " + getName() + " hp=" + getHP());

        if (!wasDead && isDead()) {
            MonsterDeathHandler.apply(gp, onDeath());
        }
    }

    private void faceOnceToward(Player player) {
        Direction direction = attackPlanner.directionToward(this, player);
        face(direction);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
