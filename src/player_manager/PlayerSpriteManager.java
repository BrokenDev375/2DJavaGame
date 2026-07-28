package player_manager;

import entity.Direction;
import entity.Entity;
import main.GamePanel;

import java.awt.image.BufferedImage;

public class PlayerSpriteManager {
    private final GamePanel gp;

    public PlayerSpriteManager(GamePanel gp) {
        this.gp = gp;
    }

    public void loadSprites(Entity player) {
        loadMoveSprites(player);
        loadAttackSprites(player, "sword");
    }

    public void loadMoveSprites(Entity player) {
        player.setMoveSprites(
                Direction.UP,
                player.setup("/player/boy_up_1", gp.tileSize, gp.tileSize),
                player.setup("/player/boy_up_2", gp.tileSize, gp.tileSize)
        );

        player.setMoveSprites(
                Direction.DOWN,
                player.setup("/player/boy_down_1", gp.tileSize, gp.tileSize),
                player.setup("/player/boy_down_2", gp.tileSize, gp.tileSize)
        );

        player.setMoveSprites(
                Direction.LEFT,
                player.setup("/player/boy_left_1", gp.tileSize, gp.tileSize),
                player.setup("/player/boy_left_2", gp.tileSize, gp.tileSize)
        );

        player.setMoveSprites(
                Direction.RIGHT,
                player.setup("/player/boy_right_1", gp.tileSize, gp.tileSize),
                player.setup("/player/boy_right_2", gp.tileSize, gp.tileSize)
        );
    }

    public void loadAttackSprites(Entity player, String weaponKey) {
        final String key = (weaponKey == null || weaponKey.isEmpty())
                ? "sword"
                : weaponKey.toLowerCase();

        boolean ok = tryLoadWeaponAttack(player, key);
        if (!ok) {
            tryLoadWeaponAttack(player, "sword");
        }
    }

    private boolean tryLoadWeaponAttack(Entity p, String key) {
        BufferedImage up1 = safeSetup(p, "/player/boy_" + key + "_up_1", gp.tileSize, gp.tileSize * 2);
        BufferedImage up2 = safeSetup(p, "/player/boy_" + key + "_up_2", gp.tileSize, gp.tileSize * 2);

        BufferedImage down1 = safeSetup(p, "/player/boy_" + key + "_down_1", gp.tileSize, gp.tileSize * 2);
        BufferedImage down2 = safeSetup(p, "/player/boy_" + key + "_down_2", gp.tileSize, gp.tileSize * 2);

        BufferedImage left1 = safeSetup(p, "/player/boy_" + key + "_left_1", gp.tileSize * 2, gp.tileSize);
        BufferedImage left2 = safeSetup(p, "/player/boy_" + key + "_left_2", gp.tileSize * 2, gp.tileSize);

        BufferedImage right1 = safeSetup(p, "/player/boy_" + key + "_right_1", gp.tileSize * 2, gp.tileSize);
        BufferedImage right2 = safeSetup(p, "/player/boy_" + key + "_right_2", gp.tileSize * 2, gp.tileSize);
        boolean allLoaded =
                up1 != null && up2 != null &&
                        down1 != null && down2 != null &&
                        left1 != null && left2 != null &&
                        right1 != null && right2 != null;

        if (allLoaded) {
            p.setAttackSprites(Direction.UP, up1, up2);
            p.setAttackSprites(Direction.DOWN, down1, down2);
            p.setAttackSprites(Direction.LEFT, left1, left2);
            p.setAttackSprites(Direction.RIGHT, right1, right2);
        }

        return allLoaded;
    }

    private BufferedImage safeSetup(Entity p, String path, int w, int h) {
        try {
            return p.setup(path, w, h);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
