package player_manager;

import entity.Direction;
import entity.EntitySpriteManager;
import entity.EntitySpriteProfile;
import main.AssetLoadException;
import main.DebugLog;
import main.GameConfig;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Optional;

public class PlayerSpriteManager {
    private final GameConfig config;
    private final EntitySpriteManager spriteLoader;

    public PlayerSpriteManager(GameConfig config) {
        this(config, new EntitySpriteManager());
    }

    public PlayerSpriteManager(GameConfig config, EntitySpriteManager spriteLoader) {
        this.config = config;
        this.spriteLoader = Objects.requireNonNull(spriteLoader, "spriteLoader");
    }

    public EntitySpriteProfile loadSprites() {
        return loadMoveSprites().merge(loadAttackSprites("sword"));
    }

    public EntitySpriteProfile loadMoveSprites() {
        int tileSize = config.tileSize();
        return new EntitySpriteProfile()
                .move(
                        Direction.UP,
                        spriteLoader.loadSprite("/player/boy_up_1", tileSize, tileSize),
                        spriteLoader.loadSprite("/player/boy_up_2", tileSize, tileSize)
                )
                .move(
                        Direction.DOWN,
                        spriteLoader.loadSprite("/player/boy_down_1", tileSize, tileSize),
                        spriteLoader.loadSprite("/player/boy_down_2", tileSize, tileSize)
                )
                .move(
                        Direction.LEFT,
                        spriteLoader.loadSprite("/player/boy_left_1", tileSize, tileSize),
                        spriteLoader.loadSprite("/player/boy_left_2", tileSize, tileSize)
                )
                .move(
                        Direction.RIGHT,
                        spriteLoader.loadSprite("/player/boy_right_1", tileSize, tileSize),
                        spriteLoader.loadSprite("/player/boy_right_2", tileSize, tileSize)
                );
    }

    public EntitySpriteProfile loadAttackSprites(String weaponKey) {
        final String key = (weaponKey == null || weaponKey.isEmpty())
                ? "sword"
                : weaponKey.toLowerCase();

        Optional<EntitySpriteProfile> profile = tryLoadWeaponAttack(key);
        if (profile.isPresent()) {
            return profile.get();
        }
        return tryLoadWeaponAttack("sword").orElseGet(EntitySpriteProfile::new);
    }

    private Optional<EntitySpriteProfile> tryLoadWeaponAttack(String key) {
        int tileSize = config.tileSize();
        Optional<BufferedImage> up1 = safeSetup("/player/boy_" + key + "_up_1", tileSize, tileSize * 2);
        Optional<BufferedImage> up2 = safeSetup("/player/boy_" + key + "_up_2", tileSize, tileSize * 2);

        Optional<BufferedImage> down1 = safeSetup("/player/boy_" + key + "_down_1", tileSize, tileSize * 2);
        Optional<BufferedImage> down2 = safeSetup("/player/boy_" + key + "_down_2", tileSize, tileSize * 2);

        Optional<BufferedImage> left1 = safeSetup("/player/boy_" + key + "_left_1", tileSize * 2, tileSize);
        Optional<BufferedImage> left2 = safeSetup("/player/boy_" + key + "_left_2", tileSize * 2, tileSize);

        Optional<BufferedImage> right1 = safeSetup("/player/boy_" + key + "_right_1", tileSize * 2, tileSize);
        Optional<BufferedImage> right2 = safeSetup("/player/boy_" + key + "_right_2", tileSize * 2, tileSize);
        boolean allLoaded =
                up1.isPresent() && up2.isPresent() &&
                        down1.isPresent() && down2.isPresent() &&
                        left1.isPresent() && left2.isPresent() &&
                        right1.isPresent() && right2.isPresent();

        if (!allLoaded) return Optional.empty();

        return Optional.of(new EntitySpriteProfile()
                .attack(Direction.UP, up1.get(), up2.get())
                .attack(Direction.DOWN, down1.get(), down2.get())
                .attack(Direction.LEFT, left1.get(), left2.get())
                .attack(Direction.RIGHT, right1.get(), right2.get()));
    }

    private Optional<BufferedImage> safeSetup(String path, int w, int h) {
        try {
            return spriteLoader.findSprite(path, w, h);
        } catch (AssetLoadException e) {
            DebugLog.error(e.getMessage(), e);
            return Optional.empty();
        }
    }
}
