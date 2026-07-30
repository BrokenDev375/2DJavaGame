package main;

public class GameLoop implements Runnable {
    private final GamePanel gp;
    private final int FPS = 60;

    public GameLoop(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (true) {
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
