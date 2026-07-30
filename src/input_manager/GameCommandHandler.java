package input_manager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.GamePanel;
import sound_manager.SoundManager;
import main.GameState;
import ui.screens.mainmenu.MainMenuUI;
import ui.effects.MessageUI;
import ui.effects.DialogueUI;
import ui.screens.pause.PauseOverlay;


public class GameCommandHandler implements KeyListener {

    private final GamePanel gp;

    public GameCommandHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // ======= MAIN MENU (START STATE) ===========
        if (gp.getGameState() == GameState.START) {
            MainMenuUI menu = gp.getUiManager().get(MainMenuUI.class);
            if (menu == null) return;
            menu.handleKey(code);

            // Ignore key inputs when credits showing
            if (menu.isShowingCredits()) return;

            // Move selection up
            if (code == KeyEvent.VK_UP) {
                menu.moveUp();
            }

            // Move selection down
            if (code == KeyEvent.VK_DOWN) {
                menu.moveDown();
            }

            // Select option
            if (code == KeyEvent.VK_ENTER) {
                menu.select();
            }
        }


        // =========== PLAY STATE ====================
        else if (gp.getGameState() == GameState.PLAY) {
            // === DIALOGUE
            DialogueUI dialogue = gp.getUiManager().get(DialogueUI.class);
            if (dialogue != null && dialogue.isActive()) {
                return;
            }
            // === SAVE GAME ===
            if (code == KeyEvent.VK_F5) {
                boolean saved = gp.getSaveManager().saveGame(gp);
                var msgUI = gp.getUiManager().get(MessageUI.class);
                if (msgUI != null) msgUI.showTouchMessage(saved ? "Game Saved!" : "Save Failed!", null, gp);
                return;
            }

            // Press ESC to open the pause menu
            if (code == KeyEvent.VK_ESCAPE) {
                gp.setGameState(GameState.PAUSE);
                SoundManager.getInstance().stopMusic();
                return;
            }

        }

        // ============ PAUSE STATE ==================
        else if (gp.getGameState() == GameState.PAUSE) {
            var pause = gp.getUiManager().get(PauseOverlay.class);
            if (pause == null) return;

            switch (code) {
                case KeyEvent.VK_LEFT -> pause.moveLeft();
                case KeyEvent.VK_RIGHT -> pause.moveRight();
                case KeyEvent.VK_ENTER -> pause.select();
            }
        }
        // ============ GAME OVER STATE ==================
        else if (gp.getGameState() == GameState.GAME_OVER) {
            var gameOver = gp.getUiManager().get(ui.screens.gameover.GameOverUI.class);
            if (gameOver == null) return;

            switch (code) {
                case KeyEvent.VK_UP -> gameOver.moveUp();
                case KeyEvent.VK_DOWN -> gameOver.moveDown();
                case KeyEvent.VK_ENTER -> gameOver.select();
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used in global command handler
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used in global command handler
    }
}
