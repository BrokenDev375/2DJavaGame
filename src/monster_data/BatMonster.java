package monster_data;

import ai.movement.AggroSwitchMovement;
import ai.movement.ChaseMovement;
import ai.movement.MovementController;
import ai.movement.WanderMovement;
import entity.Direction;
import entity.Entity;
import main.GamePanel;
import player_manager.Player;

import java.awt.Rectangle;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BatMonster extends Monster {

    private static final int WANDER_SPEED = 2;

    public BatMonster(GamePanel gp, int mapIndex) {
        super(gp, createMovementController(gp));
        placeOnMap(mapIndex);

        identifyAs("Bat");
        resizeTo(gp.tileSize(), gp.tileSize());

        getImage();

        enableCollision();
        enableAnimation();
        useMovementSpeed(WANDER_SPEED);

        defineSolidArea(new Rectangle(12, 12, getWidth() - 24, getHeight() - 20));

        configureStats(20, 5, 3);
        configureExpReward(5);
        configureAttackDamage(3);
        attackKnockback = 6;

        configureAttackBox(28, 24);
        configureAttackTiming(10, 14, 32, 36);

    }

    private static MovementController createMovementController(GamePanel gp) {
        var wander = new WanderMovement(2, 240);
        Supplier<Player> playerSup = () -> (gp.getEntityManager() != null ? gp.getEntityManager().getPlayer() : null);
        var chase = new ChaseMovement(playerSup, 2, gp.tileSize());

        Predicate<Entity> aggroCond = me -> {
            Player p = playerSup.get();
            if (p == null || p.isDead()) return false;
            long dx = (long) p.getWorldX() - me.getWorldX();
            long dy = (long) p.getWorldY() - me.getWorldY();
            long dist2 = dx * dx + dy * dy;
            long r = 1L * gp.tileSize() * 6;
            return dist2 < r * r;
        };

        return new AggroSwitchMovement(wander, chase, aggroCond);
    }

    private void getImage() {
        defineMoveSprites(
                Direction.UP,
                setup("/monster/bat_down_1", getWidth(), getHeight()),
                setup("/monster/bat_down_2", getWidth(), getHeight())
        );
        defineMoveSprites(
                Direction.DOWN,
                setup("/monster/bat_down_1", getWidth(), getHeight()),
                setup("/monster/bat_down_2", getWidth(), getHeight())
        );
        defineMoveSprites(
                Direction.LEFT,
                setup("/monster/bat_down_1", getWidth(), getHeight()),
                setup("/monster/bat_down_2", getWidth(), getHeight())
        );
        defineMoveSprites(
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
            useMovementSpeed(WANDER_SPEED);
        }
        super.update();
    }
}
