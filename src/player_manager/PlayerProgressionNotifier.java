package player_manager;

import main.GamePanel;
import ui.effects.MessageUI;

public final class PlayerProgressionNotifier {
    public void showLevelUp(GamePanel gp, PlayerProgressionResult result) {
        if (gp == null || result == null || !result.leveledUp() || gp.getUiManager() == null) {
            return;
        }

        MessageUI msgUI = gp.getUiManager().get(MessageUI.class);
        if (msgUI != null) {
            msgUI.showTouchMessage(
                    "LEVEL UP!  You reached level " + result.level() + "! Get stronger and stronger",
                    null,
                    gp
            );
        }
    }
}
