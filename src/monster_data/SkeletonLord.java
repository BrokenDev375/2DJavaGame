package monster_data;

import ai.movement.WanderMovement;
import combat.CombatSystem;
import entity.Direction;
import main.GamePanel;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class SkeletonLord extends Monster {

    private static final String SPRITE_DIR = "/monster/";

    private static final int BASE_SPEED = 2;
    private static final int ENRAGE_SPEED = 3;
    private static final int ENRAGE_THRESHOLD_PCT = 50;

    private boolean enraged = false;

    private BufferedImage p2Up1, p2Up2, p2Down1, p2Down2, p2Left1, p2Left2, p2Right1, p2Right2;
    private BufferedImage p2AtkUp1, p2AtkUp2, p2AtkDown1, p2AtkDown2, p2AtkLeft1, p2AtkLeft2, p2AtkRight1, p2AtkRight2;

    public SkeletonLord(GamePanel gp, int mapIndex) {
        super(gp, new WanderMovement(BASE_SPEED, 120));
        placeOnMap(mapIndex);

        identifyAs("Skeleton Lord");
        resizeTo(gp.tileSize() * 2, gp.tileSize() * 2);
        enableAttackAnimation();

        enableCollision();
        enableAnimation();

        configureDefaultMovementSpeed(BASE_SPEED);
        resetMovementSpeed();

        defineSolidArea(new Rectangle(28, 32, 36, 60));

        configureStats(220, 15, 10);
        attackKnockback = 8;
        configureExpReward(100);

        atkW = gp.tileSize() * 2;
        atkH = gp.tileSize() * 2;
        configureAttackBox(atkW, atkH);
        configureAttackTiming(3, 18, 39, 30);

        attackTriggerRadius = Math.max(gp.tileSize() * 6, 48);
        faceLockThreshold = 6;

        face(Direction.DOWN);

        loadPhaseSprites();
    }

    private void loadPhaseSprites() {
        int w = getWidth();
        int h = getHeight();

        defineMoveSprites(
                Direction.UP,
                setup(SPRITE_DIR + "skeletonlord_up_1", w, h),
                setup(SPRITE_DIR + "skeletonlord_up_2", w, h)
        );
        defineMoveSprites(
                Direction.DOWN,
                setup(SPRITE_DIR + "skeletonlord_down_1", w, h),
                setup(SPRITE_DIR + "skeletonlord_down_2", w, h)
        );
        defineMoveSprites(
                Direction.LEFT,
                setup(SPRITE_DIR + "skeletonlord_left_1", w, h),
                setup(SPRITE_DIR + "skeletonlord_left_2", w, h)
        );
        defineMoveSprites(
                Direction.RIGHT,
                setup(SPRITE_DIR + "skeletonlord_right_1", w, h),
                setup(SPRITE_DIR + "skeletonlord_right_2", w, h)
        );

        defineAttackSprites(
                Direction.UP,
                setup(SPRITE_DIR + "skeletonlord_attack_up_1", w, h * 2),
                setup(SPRITE_DIR + "skeletonlord_attack_up_2", w, h * 2)
        );
        defineAttackSprites(
                Direction.DOWN,
                setup(SPRITE_DIR + "skeletonlord_attack_down_1", w, h * 2),
                setup(SPRITE_DIR + "skeletonlord_attack_down_2", w, h * 2)
        );
        defineAttackSprites(
                Direction.LEFT,
                setup(SPRITE_DIR + "skeletonlord_attack_left_1", w * 2, h),
                setup(SPRITE_DIR + "skeletonlord_attack_left_2", w * 2, h)
        );
        defineAttackSprites(
                Direction.RIGHT,
                setup(SPRITE_DIR + "skeletonlord_attack_right_1", w * 2, h),
                setup(SPRITE_DIR + "skeletonlord_attack_right_2", w * 2, h)
        );

        p2Up1 = setup(SPRITE_DIR + "skeletonlord_phase2_up_1", w, h);
        p2Up2 = setup(SPRITE_DIR + "skeletonlord_phase2_up_2", w, h);
        p2Down1 = setup(SPRITE_DIR + "skeletonlord_phase2_down_1", w, h);
        p2Down2 = setup(SPRITE_DIR + "skeletonlord_phase2_down_2", w, h);
        p2Left1 = setup(SPRITE_DIR + "skeletonlord_phase2_left_1", w, h);
        p2Left2 = setup(SPRITE_DIR + "skeletonlord_phase2_left_2", w, h);
        p2Right1 = setup(SPRITE_DIR + "skeletonlord_phase2_right_1", w, h);
        p2Right2 = setup(SPRITE_DIR + "skeletonlord_phase2_right_2", w, h);

        p2AtkUp1 = setup(SPRITE_DIR + "skeletonlord_phase2_attack_up_1", w, h * 2);
        p2AtkUp2 = setup(SPRITE_DIR + "skeletonlord_phase2_attack_up_2", w, h * 2);
        p2AtkDown1 = setup(SPRITE_DIR + "skeletonlord_phase2_attack_down_1", w, h * 2);
        p2AtkDown2 = setup(SPRITE_DIR + "skeletonlord_phase2_attack_down_2", w, h * 2);
        p2AtkLeft1 = setup(SPRITE_DIR + "skeletonlord_phase2_attack_left_1", w * 2, h);
        p2AtkLeft2 = setup(SPRITE_DIR + "skeletonlord_phase2_attack_left_2", w * 2, h);
        p2AtkRight1 = setup(SPRITE_DIR + "skeletonlord_phase2_attack_right_1", w * 2, h);
        p2AtkRight2 = setup(SPRITE_DIR + "skeletonlord_phase2_attack_right_2", w * 2, h);
    }

    private void switchToPhase2Sprites() {
        defineMoveSprites(Direction.UP, p2Up1, p2Up2);
        defineMoveSprites(Direction.DOWN, p2Down1, p2Down2);
        defineMoveSprites(Direction.LEFT, p2Left1, p2Left2);
        defineMoveSprites(Direction.RIGHT, p2Right1, p2Right2);

        defineAttackSprites(Direction.UP, p2AtkUp1, p2AtkUp2);
        defineAttackSprites(Direction.DOWN, p2AtkDown1, p2AtkDown2);
        defineAttackSprites(Direction.LEFT, p2AtkLeft1, p2AtkLeft2);
        defineAttackSprites(Direction.RIGHT, p2AtkRight1, p2AtkRight2);
    }

    @Override
    public void update() {
        updatePhase();

        if (isAttackActive()) {
            useAtLeastMovementSpeed(enraged ? ENRAGE_SPEED + 1 : BASE_SPEED + 1);
        } else {
            useMovementSpeed(enraged ? ENRAGE_SPEED : BASE_SPEED);
        }

        super.update();
    }

    private void updatePhase() {
        int hpPct = (int) Math.round(getHP() * 100.0 / Math.max(1, getMaxHP()));
        if (!enraged && hpPct <= ENRAGE_THRESHOLD_PCT) {
            enraged = true;

            configureDefaultMovementSpeed(ENRAGE_SPEED);
            resetMovementSpeed();
            atkW = gp.tileSize() * 3;
            atkH = gp.tileSize() * 2;
            configureAttackBox(atkW, atkH);
            configureAttackTiming(6, 8, 10, 36);
            switchToPhase2Sprites();
        }
    }

    @Override
    protected MonsterDeathResult createDeathResult() {
        return super.createDeathResult()
                .withDrop(object_data.ObjectDropRequest.of(
                        object_data.WorldObjectType.SWORD,
                        getMapIndex(),
                        getWorldX(),
                        getWorldY()
                ));
    }
}
