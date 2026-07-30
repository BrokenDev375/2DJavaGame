package monster_data;

import ai.movement.WanderMovement;
import entity.Direction;
import main.GamePanel;

import java.awt.Rectangle;

public class SlimeMonster extends Monster {
    private static final int WANDER_SPEED = 1;

    public SlimeMonster(GamePanel gp, int mapIndex) {
        super(gp, new WanderMovement(WANDER_SPEED, 120));
        placeOnMap(mapIndex);

        identifyAs("Green Slime");
        resizeTo(gp.tileSize(), gp.tileSize());
        getImage();

        enableCollision();
        enableAnimation();
        useMovementSpeed(WANDER_SPEED);

        defineSolidArea(new Rectangle(3, 18, 42, 30));

        // Stats sinh tồn
        configureStats(10, 2, 1);

        configureAttackDamage(2);
        this.attackKnockback = 6;
        this.attackTriggerRadius = 28;
        // attackbox and timming
        configureAttackBox(28, 28);
        configureAttackTiming(12, 6, 10, 92);

    }

    private void getImage(){
        defineMoveSprites(
                Direction.UP,
                setup("/monster/greenslime_down_1" , getWidth() , getHeight()),
                setup("/monster/greenslime_down_2" , getWidth() , getHeight())
        );
        defineMoveSprites(
                Direction.DOWN,
                setup("/monster/greenslime_down_1" , getWidth() , getHeight()),
                setup("/monster/greenslime_down_2" , getWidth() , getHeight())
        );
        defineMoveSprites(
                Direction.RIGHT,
                setup("/monster/greenslime_down_1",  getWidth() , getHeight()),
                setup("/monster/greenslime_down_2" , getWidth() , getHeight())
        );
        defineMoveSprites(
                Direction.LEFT,
                setup("/monster/greenslime_down_1" , getWidth() , getHeight()),
                setup("/monster/greenslime_down_2" , getWidth() , getHeight())
        );
    }
}
