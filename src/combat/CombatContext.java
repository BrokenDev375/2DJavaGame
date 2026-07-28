package combat;

import entity.Direction;

import java.awt.Rectangle;

public interface CombatContext {
    int getWorldX();
    int getWorldY();
    Rectangle getSolidArea();
    Direction getDirection();
    boolean isDead();
}
