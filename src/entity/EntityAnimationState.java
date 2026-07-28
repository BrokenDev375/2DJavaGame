package entity;

final class EntityAnimationState {
    private int frameCounter = 0;
    private int frameNumber = 1;

    void advanceFrame(int frameDelay) {
        frameCounter++;
        if (frameCounter > Math.max(0, frameDelay)) {
            frameNumber = (frameNumber == 1) ? 2 : 1;
            frameCounter = 0;
        }
    }

    void resetFrame() {
        frameNumber = 1;
        frameCounter = 0;
    }

    boolean isFirstFrame() {
        return frameNumber == 1;
    }
}
