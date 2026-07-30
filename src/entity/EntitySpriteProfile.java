package entity;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

public final class EntitySpriteProfile {
    private final EnumMap<Direction, SpriteFrames> movement = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, SpriteFrames> attack = new EnumMap<>(Direction.class);
    private BufferedImage staticImage;

    public EntitySpriteProfile move(Direction direction, BufferedImage firstFrame, BufferedImage secondFrame) {
        movement.put(direction, new SpriteFrames(firstFrame, secondFrame));
        return this;
    }

    public EntitySpriteProfile attack(Direction direction, BufferedImage firstFrame, BufferedImage secondFrame) {
        attack.put(direction, new SpriteFrames(firstFrame, secondFrame));
        return this;
    }

    public EntitySpriteProfile useMovementForAttack() {
        attack.clear();
        attack.putAll(movement);
        return this;
    }

    public EntitySpriteProfile staticImage(BufferedImage image) {
        staticImage = image;
        return this;
    }

    public EntitySpriteProfile merge(EntitySpriteProfile other) {
        if (other == null) return this;
        movement.putAll(other.movement);
        attack.putAll(other.attack);
        if (other.staticImage != null) {
            staticImage = other.staticImage;
        }
        return this;
    }

    void applyTo(EntitySprites sprites) {
        for (Map.Entry<Direction, SpriteFrames> entry : movement.entrySet()) {
            SpriteFrames frames = entry.getValue();
            sprites.defineMoveSprites(entry.getKey(), frames.first, frames.second);
        }
        for (Map.Entry<Direction, SpriteFrames> entry : attack.entrySet()) {
            SpriteFrames frames = entry.getValue();
            sprites.defineAttackSprites(entry.getKey(), frames.first, frames.second);
        }
        if (staticImage != null) {
            sprites.useStaticImage(staticImage);
        }
    }

    private static final class SpriteFrames {
        private final BufferedImage first;
        private final BufferedImage second;

        private SpriteFrames(BufferedImage first, BufferedImage second) {
            this.first = first;
            this.second = second;
        }
    }
}
