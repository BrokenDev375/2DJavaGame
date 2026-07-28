// PlayerAnimation.java
package player_manager;

public class PlayerAnimation {
    private final Player p;
    private int frameDelay = 8;

    public PlayerAnimation(Player p) { this.p = p; }
    public void update(boolean moving, boolean attacking, int attackPhase) {
        if (attacking) {
            p.advanceSpriteFrame(frameDelay);
            return;
        }

        if (moving) {
            p.advanceSpriteFrame(frameDelay);
        } else {
            p.resetSpriteFrame();
        }
    }
}
