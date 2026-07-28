package entity;

import combat.CombatSystem;
import main.GamePanel;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class EntityDraw {
    private final GamePanel gp;

    public EntityDraw(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2, Entity e) {
        if (gp.getEntityManager() == null || gp.getEntityManager().getPlayer() == null) {
            return;
        }

        var player = gp.getEntityManager().getPlayer();

        final int screenX = gp.getCamera().screenX(e, player);
        final int screenY = gp.getCamera().screenY(e, player);

        if (!gp.getCamera().isVisible(e, player, gp.tileSize)) return;

        boolean skipSprite = false;
        if (e.isInvulnerable()) {
            int fc = gp.getFrameCounter();
            skipSprite = ((fc / 6) % 2 != 0);
        }

        boolean attacking = e.isAttacking();
        Direction dirForAnim = e.getDirection();
        if (attacking && e.getAttackDirection() != null) {
            dirForAnim = e.getAttackDirection();
        }

        BufferedImage image = selectSprite(e, attacking, dirForAnim);
        if (image == null) {
            image = nz(e.getMoveSprite(Direction.DOWN), e.getStaticImage());
        }
        if (image == null || skipSprite) return;

        int tempX = screenX;
        int tempY = screenY;

        final int offUp15 = (2 * gp.tileSize) / 3;
        final int offLeft = gp.tileSize;

        if (attacking && e.hasAttackAnimation()) {
            if (dirForAnim == Direction.UP)   tempY -= offUp15;
            if (dirForAnim == Direction.LEFT) tempX -= offLeft;
        }

        final boolean useImageSize = attacking && e.hasAttackAnimation();
        int drawW = useImageSize ? image.getWidth()  : e.getWidth();
        int drawH = useImageSize ? image.getHeight() : e.getHeight();

        g2.drawImage(image, tempX, tempY, drawW, drawH, null);
    }

    private BufferedImage selectSprite(Entity e, boolean attacking, Direction direction) {
        if (!e.isAnimationOn()) {
            return e.getStaticImage();
        }

        if (attacking) {
            return nz(e.getAttackSprite(direction), e.getMoveSprite(direction));
        }

        return e.getMoveSprite(direction);
    }

    private static BufferedImage nz(BufferedImage a, BufferedImage b) {
        return a != null ? a : b;
    }
}
