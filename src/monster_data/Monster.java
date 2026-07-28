package monster_data;

import combat.CombatSystem;
import entity.Direction;
import entity.Entity;
import main.DebugLog;
import main.GamePanel;
import player_manager.Player;
import world.WorldBody;

import java.awt.Rectangle;

public abstract class Monster extends Entity {

    // config combat
    protected int attackDamage         = 1;
    protected int attackKnockback      = 6;
    protected int attackTriggerRadius  = 36;
    protected int faceLockThreshold    = 4;
    protected int atkW, atkH;
    private int homeX, homeY; // toạ độ “nhà” để leash + wander quanh

    // --- EXP config ---
    protected int expReward = 1;   // quái này cho bao nhiêu EXP khi chết

    public Monster(GamePanel gp) {
        super(gp);

        configureAttackBox(gp.tileSize, gp.tileSize * 3 / 2);
        configureAttackTiming(8, 6, 10, 30) ;
        this.attackTriggerRadius = Math.max(gp.tileSize, 48);
        this.faceLockThreshold = 6;
        this.atkW = gp.tileSize;
        this.atkH = gp.tileSize * 3 / 2;
        configureAttackBox(atkW, atkH);
    }
    // Getter/Setter EXP
    public int getExpReward() {
        return expReward;
    }

    public void setExpReward(int expReward) {
        this.expReward = Math.max(0, expReward);
    }

    // (optional) auto tính exp theo chỉ số quái
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
        // 1) Quyết định có bắt đầu attack không (nếu đang attack thì thôi)
        decideAttack();

        // 2) Giữ vị trí nếu đang attack
        int preX = getWorldX(), preY = getWorldY();
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
        // Không cho spam: nếu CombatSystem đang trong 1 đòn thì bỏ qua
        if (isAttacking()) return;

        // 0) Lấy player an toàn
        Player p = (gp.getEntityManager() != null ? gp.getEntityManager().getPlayer() : null);
        if (p == null || p.isDead()) return;

        // 1) Tính body rect của quái & player (world space)
        Rectangle meBody = getSolidAreaWorld();
        Rectangle plBody = getSolidAreaWorld(p);

        // Player “béo” hơn 1 chút để dễ trúng (tránh hụt vì lẻ pixel)
        Rectangle plFat = new Rectangle(plBody);
        plFat.grow(2, 2);

        // 2) Nếu đã chạm thân -> đánh ngay
        if (meBody.intersects(plFat)) {
            tryStartAttackOn(p);
            return;
        }

        // 3) Reach-rectangle: kéo ô meBody theo hướng đang nhìn
        final int rw = (atkW > 0 ? atkW : gp.tileSize);
        final int rh = (atkH > 0 ? atkH : gp.tileSize);

        Rectangle reach = new Rectangle(meBody);
        switch (this.getDirection()) {
            case UP:
                reach.y      -= rh;
                reach.height += rh;
                break;
            case DOWN:
                reach.height += rh;
                break;
            case LEFT:
                reach.x     -= rw;
                reach.width += rw;
                break;
            case RIGHT:
                reach.width += rw;
                break;
        }

        // 4) Nếu player ở trong tầm reach -> bắt đầu đòn
        if (reach.intersects(plFat)) {
            tryStartAttackOn(p);
        }
    }

    protected void tryStartAttackOn(Player p) {
        // 1) CombatSystem phải cho phép (cooldown, state…)
        if (!canStartAttack()) return;

        // 2) Xoay mặt 1 lần về phía player
        faceOnceToward(p);

        // 3) LOCK HƯỚNG ĐÁNH: chụp lại hướng tại thời điểm này
        this.lockAttackDirection();

        // 4) Bắt đầu đòn đánh
        startAttack();
        clearHitThisSwing();
    }

    private void faceOnceToward(Player p) {
        int dx = p.getWorldX() - this.getWorldX();
        int dy = p.getWorldY() - this.getWorldY();
        if (Math.abs(dx) > Math.abs(dy)) {
            this.face((dx >= 0) ? Direction.RIGHT : Direction.LEFT);
        } else {
            this.face((dy >= 0) ? Direction.DOWN : Direction.UP);
        }
    }

    protected static Rectangle getSolidAreaWorld(WorldBody body) {
        return body.getSolidAreaWorld();
    }
    public void onDeath() {
        // 1) EXP
        Player p = (gp != null && gp.getEntityManager() != null) ? gp.getEntityManager().getPlayer() : null;
        if (p != null) {
            int expGain = getExpReward();
            DebugLog.info("[EXP] +" + expGain + " for " + getName());
            p.gainExp(expGain);
        } else {
            DebugLog.info("[EXP] player not found");
        }

        // 2) 25% drop
        double roll = Math.random();
        DebugLog.info("[DROP] roll=" + roll);
        if (roll < 0.25) {
            DebugLog.info("[DROP] health potion");
            spawnHealthPosionDrop();
        } else {
            DebugLog.info("[DROP] none");
        }
    }

    private void spawnHealthPosionDrop() {
        if (gp == null || gp.getObjectManager() == null) return;

        gp.getObjectManager().spawnHealthPosion(this.getMapIndex(), this.getWorldX(), this.getWorldY());
    }

    protected void reduceHP(int amount) {
        boolean wasDead = isDead();
        DebugLog.info("[DMG] " + getName() + " incoming=" + amount + " hp=" + getHP());

        super.reduceHP(amount);

        DebugLog.info("[DMG] " + getName() + " hp=" + getHP());

        if (!wasDead && isDead()) {
            DebugLog.info("[DEATH] " + getName());
            onDeath();
        }
    }
}
