package entity;

import world.WorldBody;

import java.awt.Rectangle;

public final class EntityCoreSmokeTest {
    private EntityCoreSmokeTest() {}

    public static void main(String[] args) {
        directionDeltaAndOpposite();
        mapPlacementTracksPositionAndMap();
        entitySizeClampsToPositiveValues();
        damageStateTracksInvulnerabilityAndAttacker();
        knockbackStateTracksVelocityAndDuration();
        animationFrameAdvancesAndResets();
        collisionAreaUsesDefensiveCopies();
        worldBodyProjectsSolidAreaIntoWorldSpace();
    }

    private static void directionDeltaAndOpposite() {
        assertEquals(0, Direction.UP.dx(), "UP dx");
        assertEquals(-1, Direction.UP.dy(), "UP dy");
        assertEquals(-12, Direction.LEFT.scaledDx(12), "LEFT scaled dx");
        assertEquals(12, Direction.DOWN.scaledDy(12), "DOWN scaled dy");
        assertSame(Direction.RIGHT, Direction.LEFT.opposite(), "LEFT opposite");
        assertSame(Direction.UP, Direction.DOWN.opposite(), "DOWN opposite");
    }

    private static void animationFrameAdvancesAndResets() {
        EntityAnimationState state = new EntityAnimationState();

        assertTrue(state.isFirstFrame(), "starts on first frame");
        state.advanceFrame(1);
        assertTrue(state.isFirstFrame(), "delay keeps first frame");
        state.advanceFrame(1);
        assertFalse(state.isFirstFrame(), "frame toggles after delay");

        state.resetFrame();
        assertTrue(state.isFirstFrame(), "reset returns to first frame");
    }

    private static void mapPlacementTracksPositionAndMap() {
        MapPlacement placement = new MapPlacement();

        placement.moveTo(10, 20);
        placement.moveBy(3, -4);
        assertEquals(13, placement.getWorldX(), "world x");
        assertEquals(16, placement.getWorldY(), "world y");

        assertTrue(placement.isOnMap(0), "default map");
        placement.setMapIndex(2);
        assertFalse(placement.isOnMap(0), "old map");
        assertTrue(placement.isOnMap(2), "new map");
    }

    private static void damageStateTracksInvulnerabilityAndAttacker() {
        DamageState damage = new DamageState();

        assertFalse(damage.isInvulnerable(), "starts vulnerable");
        damage.setInvulnFrames(2);
        damage.startInvulnerability();
        assertTrue(damage.isInvulnerable(), "starts invulnerability");

        damage.tickInvulnerability();
        assertTrue(damage.isInvulnerable(), "keeps invulnerability before counter ends");
        damage.tickInvulnerability();
        assertFalse(damage.isInvulnerable(), "ends invulnerability");

        damage.markHitBy(null);
        assertSame(null, damage.getLastHitBy(), "tracks null attacker");
        damage.clearLastHitBy();
        assertSame(null, damage.getLastHitBy(), "clears attacker");
    }

    private static void knockbackStateTracksVelocityAndDuration() {
        KnockbackState knockback = new KnockbackState();

        assertFalse(knockback.isActive(), "starts inactive");
        knockback.setDurationFrames(2);
        knockback.start(3, -4);

        assertTrue(knockback.isActive(), "starts knockback");
        assertTrue(knockback.hasVelocity(), "tracks velocity");
        assertEquals(3, knockback.velocityX(), "velocity x");
        assertEquals(-4, knockback.velocityY(), "velocity y");

        knockback.stopVelocityX();
        assertEquals(0, knockback.velocityX(), "stops x velocity");
        assertEquals(-4, knockback.velocityY(), "keeps y velocity");

        knockback.tickDuration();
        assertTrue(knockback.isActive(), "still active before duration ends");
        knockback.tickDuration();
        assertTrue(knockback.isFinished(), "duration ends");

        knockback.finish();
        assertFalse(knockback.hasVelocity(), "finish clears velocity");
    }

    private static void entitySizeClampsToPositiveValues() {
        EntitySize size = new EntitySize();

        assertEquals(1, size.getWidth(), "default width");
        assertEquals(1, size.getHeight(), "default height");

        size.set(0, -4);
        assertEquals(1, size.getWidth(), "clamped width");
        assertEquals(1, size.getHeight(), "clamped height");

        size.set(32, 48);
        assertEquals(32, size.getWidth(), "custom width");
        assertEquals(48, size.getHeight(), "custom height");
    }

    private static void collisionAreaUsesDefensiveCopies() {
        EntityCollision collision = new EntityCollision();
        collision.setSolidArea(new Rectangle(4, 5, 10, 11));

        Rectangle local = collision.getSolidArea(99, 99);
        local.x = 100;

        assertRect(collision.getSolidArea(99, 99), 4, 5, 10, 11, "local solid area copy");
        assertRect(collision.getSolidAreaAt(20, 30, 99, 99), 24, 35, 10, 11, "world solid area");

        assertTrue(collision.canMove(), "default can move");
        collision.markCollisionX();
        assertFalse(collision.canMove(), "x collision blocks movement");
        collision.clearCollisionXState();
        assertTrue(collision.canMove(), "clearing x restores movement");
    }

    private static void worldBodyProjectsSolidAreaIntoWorldSpace() {
        WorldBody body = new FakeBody(100, 200, new Rectangle(3, 4, 5, 6));

        assertRect(body.getSolidAreaAt(7, 8), 10, 12, 5, 6, "projected custom point");
        assertRect(body.getSolidAreaWorld(), 103, 204, 5, 6, "projected world position");
    }

    private static void assertRect(Rectangle actual, int x, int y, int width, int height, String label) {
        if (actual.x != x || actual.y != y || actual.width != width || actual.height != height) {
            throw new AssertionError(label + " expected [" + x + "," + y + "," + width + "," + height
                    + "] but was [" + actual.x + "," + actual.y + "," + actual.width + "," + actual.height + "]");
        }
    }

    private static void assertSame(Object expected, Object actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected same instance");
        }
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label);
        }
    }

    private static void assertFalse(boolean condition, String label) {
        if (condition) {
            throw new AssertionError(label);
        }
    }

    private static final class FakeBody implements WorldBody {
        private final int worldX;
        private final int worldY;
        private final Rectangle solidArea;

        private FakeBody(int worldX, int worldY, Rectangle solidArea) {
            this.worldX = worldX;
            this.worldY = worldY;
            this.solidArea = new Rectangle(solidArea);
        }

        @Override
        public int getWorldX() {
            return worldX;
        }

        @Override
        public int getWorldY() {
            return worldY;
        }

        @Override
        public int getWidth() {
            return solidArea.width;
        }

        @Override
        public int getHeight() {
            return solidArea.height;
        }

        @Override
        public Rectangle getSolidArea() {
            return new Rectangle(solidArea);
        }
    }
}
