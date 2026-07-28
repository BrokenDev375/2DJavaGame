package monster_data;

import ai.movement.WanderMovement;
import entity.Direction;
import main.GamePanel;

import java.awt.Rectangle;

public class SlimeMonster extends Monster {
    private final int wanderSpeed = 1;

    public SlimeMonster(GamePanel gp, int mapIndex) {
        super(gp);
        setMapIndex(mapIndex);

        setName("Green Slime");
        setSize(gp.tileSize, gp.tileSize);
        setHasAttackAnimation(false);
        getImage();

        setCollidable(true); setAnimationOn(true);
        useMovementSpeed(wanderSpeed);

        setSolidArea(new Rectangle(3, 18, 42, 30));

        // Stats sinh tồn
        setStats(10, 2, 1);

        this.attackDamage = 2;
        this.attackKnockback = 6;
        this.attackTriggerRadius = 28;
        // attackbox and timming
        configureAttackBox(28, 28);
        configureAttackTiming(6, 6, 10, 98);

        // easy movement
        setController(new WanderMovement(/*speed*/wanderSpeed, /*changeEveryFrames*/120));
    }

    private void getImage(){
        setMoveSprites(
                Direction.UP,
                setup("/monster/greenslime_down_1" , getWidth() , getHeight()),
                setup("/monster/greenslime_down_2" , getWidth() , getHeight())
        );
        setMoveSprites(
                Direction.DOWN,
                setup("/monster/greenslime_down_1" , getWidth() , getHeight()),
                setup("/monster/greenslime_down_2" , getWidth() , getHeight())
        );
        setMoveSprites(
                Direction.RIGHT,
                setup("/monster/greenslime_down_1",  getWidth() , getHeight()),
                setup("/monster/greenslime_down_2" , getWidth() , getHeight())
        );
        setMoveSprites(
                Direction.LEFT,
                setup("/monster/greenslime_down_1" , getWidth() , getHeight()),
                setup("/monster/greenslime_down_2" , getWidth() , getHeight())
        );
    }
}
