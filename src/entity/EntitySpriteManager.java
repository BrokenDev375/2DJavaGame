package entity;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Optional;
import main.AssetLoadException;
import main.AssetLoader;
import main.DebugLog;

public class EntitySpriteManager {
    private final AssetLoader assetLoader;

    public EntitySpriteManager() {
        this(AssetLoader.defaultLoader());
    }

    public EntitySpriteManager(AssetLoader assetLoader) {
        this.assetLoader = Objects.requireNonNull(assetLoader, "assetLoader");
    }

    public void updateSprite(Entity e) {
        e.advanceSpriteFrame(8);
    }

    public Optional<BufferedImage> findSprite(String imagePath, int width, int height) throws AssetLoadException {
        return assetLoader.findScaledImage(imagePath, width, height, "EntitySpriteManager");
    }

    public BufferedImage loadSprite(String imagePath, int width, int height) {
        try {
            return assetLoader.requireScaledImage(imagePath, width, height, "EntitySpriteManager");
        } catch (AssetLoadException e) {
            DebugLog.error(e.getMessage(), e);
            return AssetLoader.placeholderImage(width, height);
        }
    }
}
