package ui.screens.pause;

import main.GamePanel;
import main.GameState;
import sound_manager.SoundManager;
import ui.base.BaseUI;
import ui.utilz.LoadSave;
import ui.utilz.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;
import static ui.utilz.Constants.UI.PauseButtons.SOUND_SIZE;
import static ui.utilz.Constants.UI.URMButtons.*;

public class PauseOverlay extends BaseUI {

    private BufferedImage bgImg;
    private int bgX, bgY, bgW, bgH;

    // Nút âm thanh và 3 nút URM
    private SoundButton musicB, sfxB;
    private UrmButton menuB, replayB, resumeB;

    // 0 = Music, 1 = Menu, 2 = Replay, 3 = Resume
    private int focusIndex = 4; // mặc định chọn Resume

    public PauseOverlay(GamePanel gp) {
        super(gp);
        loadBackground();
        initButtons();
    }
    // Tải background của Pause Menu
    private void loadBackground() {
        bgImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);

        float scale = gp.tileSize / 48f;
        bgW = Math.round(bgImg.getWidth() * scale);
        bgH = Math.round(bgImg.getHeight() * scale);
        bgX = gp.screenWidth  / 2 - bgW / 2;
        bgY = gp.screenHeight / 2 - bgH / 2;
    }
    // Khởi tạo vị trí của các nút
    private void initButtons() {
        int centerX = bgX + bgW / 2;
        // ==== Nút âm thanh ====
        final float REF_W = 258f;
        final float REF_H = 389f;
        final float MUSIC_RIGHT_X  = 208f;
        final float MUSIC_CENTER_Y = 111f;

        int soundX = bgX + Math.round((MUSIC_RIGHT_X / REF_W) * bgW) - 200;
        int soundY = bgY + Math.round((MUSIC_CENTER_Y / REF_H) * bgH) - SOUND_SIZE / 2 + 220;
        musicB = new SoundButton(soundX, soundY);

        // ==== Nhóm 3 nút Menu / Replay / Resume ====
        int groupW = URM_SIZE * 3 + Constants.UI.PauseButtons.URM_GAP * 2;
        int baseY  = bgY + (int) (bgH * (Constants.UI.PauseButtons.GROUP_Y - 0.215f));
        int startX = centerX - groupW / 2 + (int) (bgW * -0.01f);

        menuB   = new UrmButton(startX, baseY, 2);
        replayB = new UrmButton(startX + URM_SIZE + Constants.UI.PauseButtons.URM_GAP, baseY, 1);
        resumeB = new UrmButton(startX + (URM_SIZE + Constants.UI.PauseButtons.URM_GAP) * 2, baseY, 0);
    }

    @Override
    public void update() {
        // Chỉ cần update nút âm thanh
        musicB.update();
    }

    @Override
    public void draw(Graphics2D g2) {
        // Vẽ background
        g2.drawImage(bgImg, bgX, bgY, bgW, bgH, null);
        // Vẽ các nút
        musicB.draw(g2);
        menuB.draw(g2);
        replayB.draw(g2);
        resumeB.draw(g2);

        // Bôi sáng nút đang chọn
        highlightFocusedButton(g2);
    }
    // Điều kiện vẽ trong Pause state
    @Override
    public boolean shouldRenderIn(GameState state) {
        return state == GameState.PAUSE;
    }
    @Override
    public boolean shouldDraw() {
        return gp.getGameState() == GameState.PAUSE;
    }
    @Override
    public boolean shouldUpdate() {
        return gp.getGameState() == GameState.PAUSE;
    }
    // Bôi sáng nút đang được focus
    private void highlightFocusedButton(Graphics2D g2) {

        Rectangle r = switch (focusIndex) {
            case 0 -> musicB.getBounds();
            case 1 -> menuB.getBounds();
            case 2 -> replayB.getBounds();
            case 3 -> resumeB.getBounds();
            default -> null;
        };

        if (r != null) {
            g2.setColor(new Color(255, 255, 255, 80));
            g2.fillRoundRect(r.x - 4, r.y - 4, r.width + 8, r.height + 8, 10, 10);
        }
    }

    // ==== Điều khiển bằng phím ====
    public void moveLeft()  { focusIndex = (focusIndex - 1 + 5) % 5; }
    public void moveRight() { focusIndex = (focusIndex + 1)     % 5; }

    public void select() {
        switch (focusIndex) {
            case 0 -> toggleMusic();
            case 1 -> goMenu();
            case 2 -> restart();
            case 3 -> resume();
        }
    }

    // ==== Các hành động ====
    private void toggleMusic() {
        musicB.setMuted(!musicB.isMuted());

        if (musicB.isMuted()) {
            SoundManager.getInstance().stopMusic();
        } else {
            SoundManager.getInstance().playMusic(SoundManager.SoundID.MUSIC_THEME);
        }
    }

    // Về Menu chính
    private void goMenu() {
        gp.setGameState(GameState.START);
    }

    // Restart game
    private void restart() {
        gp.restartGame();
        if (!musicB.isMuted()) {
            SoundManager.getInstance().playMusic(SoundManager.SoundID.MUSIC_THEME);
        }
    }

    // Tiếp tục chơi
    private void resume() {
        gp.setGameState(GameState.PLAY);
        if (!musicB.isMuted()) {
            SoundManager.getInstance().playMusic(SoundManager.SoundID.MUSIC_THEME);
        }
    }
}
