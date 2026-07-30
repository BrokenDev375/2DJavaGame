package main;

import world.WorldBody;

import java.awt.Rectangle;

public final class CollisionGeometrySmokeTest {
    private CollisionGeometrySmokeTest() {}

    public static void main(String[] args) {
        reportsHitAndMissUsingProjectedSolidAreas();
        worldBodyFixtureKeepsDefensiveCopies();
    }

    private static void reportsHitAndMissUsingProjectedSolidAreas() {
        WorldBody mover = new FakeBody(10, 10, new Rectangle(2, 2, 10, 10));
        WorldBody target = new FakeBody(25, 12, new Rectangle(0, 0, 8, 8));

        assertFalse(CollisionGeometry.overlaps(mover, 10, 10, target), "current position misses target");
        assertTrue(CollisionGeometry.overlaps(mover, 16, 10, target), "projected position hits target");
        assertFalse(CollisionGeometry.overlaps(mover, 60, 10, target), "far projection misses target");
    }

    private static void worldBodyFixtureKeepsDefensiveCopies() {
        FakeBody body = new FakeBody(100, 200, new Rectangle(3, 4, 5, 6));

        Rectangle copy = body.getSolidArea();
        copy.x = 99;

        Rectangle local = body.getSolidArea();
        assertEquals(3, local.x, "solid area x copy");
        assertEquals(4, local.y, "solid area y copy");
        assertEquals(5, local.width, "solid area width");
        assertEquals(6, local.height, "solid area height");

        Rectangle world = body.getSolidAreaWorld();
        assertEquals(103, world.x, "solid area world x");
        assertEquals(204, world.y, "solid area world y");
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
