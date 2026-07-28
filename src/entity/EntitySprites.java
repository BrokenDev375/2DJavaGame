package entity;

import java.awt.image.BufferedImage;
import java.util.EnumMap;

final class EntitySprites {
    private final EnumMap<Direction, SpriteFrames> movement = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, SpriteFrames> attack = new EnumMap<>(Direction.class);
    private BufferedImage staticImage;

    void setMoveSprites(Direction direction, BufferedImage firstFrame, BufferedImage secondFrame) {
        movement.put(direction, new SpriteFrames(firstFrame, secondFrame));
    }

    void setAttackSprites(Direction direction, BufferedImage firstFrame, BufferedImage secondFrame) {
        attack.put(direction, new SpriteFrames(firstFrame, secondFrame));
    }

    void useMoveSpritesForAttack() {
        attack.clear();
        attack.putAll(movement);
    }

    BufferedImage getMoveSprite(Direction direction, boolean firstFrame) {
        return getSprite(movement, direction, firstFrame);
    }

    BufferedImage getAttackSprite(Direction direction, boolean firstFrame) {
        return getSprite(attack, direction, firstFrame);
    }

    void setStaticImage(BufferedImage image) {
        staticImage = image;
    }

    BufferedImage getStaticImage() {
        return staticImage;
    }

    private BufferedImage getSprite(EnumMap<Direction, SpriteFrames> source,
                                    Direction direction,
                                    boolean firstFrame) {
        if (direction == null) return null;
        SpriteFrames frames = source.get(direction);
        return frames == null ? null : frames.select(firstFrame);
    }

    private static final class SpriteFrames {
        private final BufferedImage first;
        private final BufferedImage second;

        private SpriteFrames(BufferedImage first, BufferedImage second) {
            this.first = first;
            this.second = second;
        }

        private BufferedImage select(boolean firstFrame) {
            return firstFrame ? first : second;
        }
    }
}
