package entity;

final class KnockbackState {
    private int velocityX = 0;
    private int velocityY = 0;
    private int counter = 0;
    private int durationFrames = 12;

    boolean isActive() {
        return counter > 0;
    }

    boolean isFinished() {
        return counter <= 0;
    }

    void setDurationFrames(int frames) {
        durationFrames = Math.max(1, frames);
    }

    void tickDuration() {
        counter--;
    }

    void start(int velocityX, int velocityY) {
        start(velocityX, velocityY, durationFrames);
    }

    void start(int velocityX, int velocityY, int durationFrames) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.counter = Math.max(0, durationFrames);
    }

    void finish() {
        clearVelocity();
        counter = 0;
    }

    void clearVelocity() {
        velocityX = 0;
        velocityY = 0;
    }

    int velocityX() {
        return velocityX;
    }

    int velocityY() {
        return velocityY;
    }

    boolean hasVelocity() {
        return velocityX != 0 || velocityY != 0;
    }

    void stopVelocityX() {
        velocityX = 0;
    }

    void stopVelocityY() {
        velocityY = 0;
    }
}
