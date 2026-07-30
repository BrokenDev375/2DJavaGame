package monster_data;

import ai.movement.WanderMovement;
import entity.Direction;
import main.GamePanel;

import java.awt.Rectangle;

public class RedSlimeMonster extends Monster {

    private static final int WANDER_SPEED = 2;

    public RedSlimeMonster(GamePanel gp, int mapIndex) {
        super(gp, new WanderMovement(1, 240));
        placeOnMap(mapIndex);

        identifyAs("RedSlime");
        resizeTo(gp.tileSize(), gp.tileSize());

        getImage();

        enableCollision();
        enableAnimation();
        useMovementSpeed(WANDER_SPEED);

        defineSolidArea(new Rectangle(10, 18, getWidth() - 20, getHeight() - 22));

        configureStats(12, 3, 1);
        configureAttackDamage(4);
        attackKnockback = 5;

        configureAttackBox(30, 26);
        configureAttackTiming(20, 6, 16, 40);

    }

    private void getImage() {
        defineMoveSprites(
                Direction.UP,
                setup("/monster/redslime_down_1", getWidth(), getHeight()),
                setup("/monster/redslime_down_2", getWidth(), getHeight())
        );
        defineMoveSprites(
                Direction.DOWN,
                setup("/monster/redslime_down_1", getWidth(), getHeight()),
                setup("/monster/redslime_down_2", getWidth(), getHeight())
        );
        defineMoveSprites(
                Direction.LEFT,
                setup("/monster/redslime_down_1", getWidth(), getHeight()),
                setup("/monster/redslime_down_2", getWidth(), getHeight())
        );
        defineMoveSprites(
                Direction.RIGHT,
                setup("/monster/redslime_down_1", getWidth(), getHeight()),
                setup("/monster/redslime_down_2", getWidth(), getHeight())
        );
        useMoveSpritesForAttack();
    }
}
