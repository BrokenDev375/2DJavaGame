package entity;

final class EntitySize {
    private int width = 1;
    private int height = 1;

    void set(int width, int height) {
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }
}
