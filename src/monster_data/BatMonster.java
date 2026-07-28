package monster_data;

import ai.movement.AggroSwitchMovement;
import ai.movement.ChaseMovement;
import ai.movement.WanderMovement;
import combat.CombatSystem;
import entity.Direction;
import entity.Entity;
import main.GamePanel;
import player_manager.Player;

import java.awt.Rectangle;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BatMonster extends Monster {

    private final int wanderSpeed = 2;

    public BatMonster(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);

        setName("Bat");
        setSize(gp.tileSize, gp.tileSize);
        setHasAttackAnimation(false);

        getImage();

        setCollidable(true);
        setAnimationOn(true);
        useMovementSpeed(wanderSpeed);

        setSolidArea(new Rectangle(12, 12, getWidth() - 24, getHeight() - 20));

        setStats(20, 5, 3);
        setExpReward(5);
        attackDamage = 3;
        attackKnockback = 6;

        configureAttackBox(28, 24);
        configureAttackTiming(3, 18, 42, 30);

        var wander = new WanderMovement(2, 240);
        Supplier<Player> playerSup = () -> (gp.getEntityManager() != null ? gp.getEntityManager().getPlayer() : null);
        var chase = new ChaseMovement(playerSup, 2, gp.tileSize);

        Predicate<Entity> aggroCond = me -> {
            Player p = playerSup.get();
            if (p == null || p.isDead()) return false;
            long dx = (long) p.getWorldX() - me.getWorldX();
            long dy = (long) p.getWorldY() - me.getWorldY();
            long dist2 = dx * dx + dy * dy;
            long r = 1L * gp.tileSize * 6;
            return dist2 < r * r;
        };

        setController(new AggroSwitchMovement(wander, chase, aggroCond));
    }

    private void getImage() {
        setMoveSprites(
                Direction.UP,
                setup("/monster/bat_down_1", getWidth(), getHeight()),
                setup("/monster/bat_down_2", getWidth(), getHeight())
        );
        setMoveSprites(
                Direction.DOWN,
                setup("/monster/bat_down_1", getWidth(), getHeight()),
                setup("/monster/bat_down_2", getWidth(), getHeight())
        );
        setMoveSprites(
                Direction.LEFT,
                setup("/monster/bat_down_1", getWidth(), getHeight()),
                setup("/monster/bat_down_2", getWidth(), getHeight())
        );
        setMoveSprites(
                Direction.RIGHT,
                setup("/monster/bat_down_1", getWidth(), getHeight()),
                setup("/monster/bat_down_2", getWidth(), getHeight())
        );
        useMoveSpritesForAttack();
    }

    @Override
    public void update() {
        if (isAttackActive()) {
            useMovementSpeed(3);
        } else {
            useMovementSpeed(wanderSpeed);
        }
        super.update();
    }
}
