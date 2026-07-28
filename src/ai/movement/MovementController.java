package ai.movement;

import entity.Entity;

public interface MovementController {
    MovementIntent decide(Entity e);
}
