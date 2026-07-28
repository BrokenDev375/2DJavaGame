package monster_data;

import ai.movement.AggroSwitchMovement;
import ai.movement.ChaseMovement;
import ai.movement.WanderMovement;
import entity.Direction;
import entity.Entity;
import main.GamePanel;
import player_manager.Player;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OrcMonster extends Monster {

    private final int wanderSpeed = 1;

    public OrcMonster(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);

        setName("Orc");
        setSize(gp.tileSize, gp.tileSize);
        setHasAttackAnimation(true);

        getImage();

        setCollidable(true);
        setAnimationOn(true);
        useMovementSpeed(wanderSpeed);

        setSolidArea(new Rectangle(6, 20, 36, 28));

        setStats(40, 8, 5);
        this.attackKnockback = 8;
        setExpReward(5);

        configureAttackBox(40, 32);
        configureAttackTiming(12, 10, 16, 82);

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
        int w = getWidth();
        int h = getHeight();

        setMoveSprites(
                Direction.UP,
                setup("/monster/orc_up_1", w, h),
                setup("/monster/orc_up_2", w, h)
        );
        setMoveSprites(
                Direction.DOWN,
                setup("/monster/orc_down_1", w, h),
                setup("/monster/orc_down_2", w, h)
        );
        setMoveSprites(
                Direction.LEFT,
                setup("/monster/orc_left_1", w, h),
                setup("/monster/orc_left_2", w, h)
        );
        setMoveSprites(
                Direction.RIGHT,
                setup("/monster/orc_right_1", w, h),
                setup("/monster/orc_right_2", w, h)
        );

        BufferedImage atkUp1 = setup("/monster/orc_attack_up_1", w, h);
        BufferedImage atkUp2 = setup("/monster/orc_attack_up_2", w, h);
        BufferedImage atkDown1 = setup("/monster/orc_attack_down_1", w, h);
        BufferedImage atkDown2 = setup("/monster/orc_attack_down_2", w, h);
        BufferedImage atkLeft1 = setup("/monster/orc_attack_left_1", w, h);
        BufferedImage atkLeft2 = setup("/monster/orc_attack_left_2", w, h);
        BufferedImage atkRight1 = setup("/monster/orc_attack_right_1", w, h);
        BufferedImage atkRight2 = setup("/monster/orc_attack_right_2", w, h);

        setAttackSprites(
                Direction.UP,
                gp.getUtilityTool().scaleImage(atkUp1, w, h * 2),
                gp.getUtilityTool().scaleImage(atkUp2, w, h * 2)
        );
        setAttackSprites(
                Direction.DOWN,
                gp.getUtilityTool().scaleImage(atkDown1, w, h * 2),
                gp.getUtilityTool().scaleImage(atkDown2, w, h * 2)
        );
        setAttackSprites(
                Direction.LEFT,
                gp.getUtilityTool().scaleImage(atkLeft1, w * 2, h),
                gp.getUtilityTool().scaleImage(atkLeft2, w * 2, h)
        );
        setAttackSprites(
                Direction.RIGHT,
                gp.getUtilityTool().scaleImage(atkRight1, w * 2, h),
                gp.getUtilityTool().scaleImage(atkRight2, w * 2, h)
        );
    }
}
