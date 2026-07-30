package entity;

import combat.CombatSystem;
import main.RenderContext;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class EntityDraw {
    private final RenderContext render;

    public EntityDraw(RenderContext render) {
        this.render = render;
    }

    public void draw(Graphics2D g2, Entity e) {
        var player = render.player();
        if (player == null) {
            return;
        }

        final int screenX = render.camera().screenX(e, player);
        final int screenY = render.camera().screenY(e, player);
        final int tileSize = render.getConfig().tileSize();

        if (!render.camera().isVisible(e, player, tileSize)) return;

        boolean skipSprite = false;
        if (e.isInvulnerable()) {
            int fc = render.frameCounter();
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

        final int offUp15 = (2 * tileSize) / 3;
        final int offLeft = tileSize;

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
