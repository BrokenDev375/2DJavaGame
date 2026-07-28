package monster_data;

import ai.movement.WanderMovement;
import entity.Direction;
import main.GamePanel;

import java.awt.Rectangle;

public class RedSlimeMonster extends Monster {

    private final int wanderSpeed = 2;

    public RedSlimeMonster(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);

        setName("RedSlime");
        setSize(gp.tileSize, gp.tileSize);
        setHasAttackAnimation(false);

        getImage();

        setCollidable(true);
        setAnimationOn(true);
        useMovementSpeed(wanderSpeed);

        setSolidArea(new Rectangle(10, 18, getWidth() - 20, getHeight() - 22));

        setStats(12, 3, 1);
        attackDamage = 4;
        attackKnockback = 5;

        configureAttackBox(30, 26);
        configureAttackTiming(20, 6, 16, 40);

        setController(new WanderMovement(1, 240));
    }

    private void getImage() {
        setMoveSprites(
                Direction.UP,
                setup("/monster/redslime_down_1", getWidth(), getHeight()),
                setup("/monster/redslime_down_2", getWidth(), getHeight())
        );
        setMoveSprites(
                Direction.DOWN,
                setup("/monster/redslime_down_1", getWidth(), getHeight()),
                setup("/monster/redslime_down_2", getWidth(), getHeight())
        );
        setMoveSprites(
                Direction.LEFT,
                setup("/monster/redslime_down_1", getWidth(), getHeight()),
                setup("/monster/redslime_down_2", getWidth(), getHeight())
        );
        setMoveSprites(
                Direction.RIGHT,
                setup("/monster/redslime_down_1", getWidth(), getHeight()),
                setup("/monster/redslime_down_2", getWidth(), getHeight())
        );
        useMoveSpritesForAttack();
    }
}
