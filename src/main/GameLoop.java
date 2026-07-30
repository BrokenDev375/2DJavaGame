package main;

public final class GameLoop implements Runnable {
    private static final int FPS = 60;

    private final GamePanel gp;
    private volatile boolean running = false;

    public GameLoop(GamePanel gp) {
        this.gp = gp;
    }

    public void requestStop() {
        running = false;
    }

    @Override
    public void run() {
        running = true;
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (running) {
            gp.update();
            gp.repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1_000_000.0;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                DebugLog.error("[GameLoop] Game loop interrupted.", e);
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
