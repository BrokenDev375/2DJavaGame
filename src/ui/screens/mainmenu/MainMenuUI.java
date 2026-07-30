package ui.screens.mainmenu;

import game_data.LoadResult;
import main.GamePanel;
import main.GameState;
import sound_manager.SoundManager;
import ui.base.BaseUI;
import ui.utilz.LoadSave;
import static ui.utilz.Constants.UI.MenuButtons.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MainMenuUI extends BaseUI {

    private BufferedImage bgImg, boardImg, buttonAtlas;
    private int bgX, bgY, bgW, bgH;
    private BufferedImage[][] buttonImgs; // [4 nút][3 trạng thái]
    private int focusIndex = 0;           // 0=Play, 1=Load, 2=Quit, 3=Credits
    private boolean showingCredits = false;
    // ==== Khởi tạo ====
    public MainMenuUI(GamePanel gp) {
        super(gp);
        loadBackground();
        initButtons();
    }
    // Tải background và bảng trung tâm
    private void loadBackground() {
        boardImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
        bgImg    = LoadSave.GetSpriteAtlas(LoadSave.BACKGROUND_MENU);

        float scale = gp.tileSize / 48f;
        bgW = Math.round(boardImg.getWidth() * scale);
        bgH = Math.round(boardImg.getHeight() * scale);
        bgX = gp.screenWidth  / 2 - bgW / 2;
        bgY = gp.screenHeight / 2 - bgH / 2;
    }
    // Load atlas chứa 4 hàng nút, mỗi hàng gồm 3 trạng thái (default, hover, pressed)
    private void initButtons() {
        buttonAtlas = LoadSave.GetSpriteAtlas(LoadSave.BUTTON_ATLAS);
        if (buttonAtlas == null) return;

        int tileW = buttonAtlas.getWidth()  / 3; // 3 trạng thái
        int tileH = buttonAtlas.getHeight() / 4; // 4 nút
        buttonImgs = new BufferedImage[4][3];

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 3; col++) {
                buttonImgs[row][col] = buttonAtlas.getSubimage(
                        col * tileW,
                        row * tileH,
                        tileW,
                        tileH
                );
            }
        }
    }
    // ==== UPDATE & DRAW ====
    @Override
    public void update() {}

    @Override
    public void draw(Graphics2D g2) {
        // Vẽ background
        g2.drawImage(bgImg, 0, 0, gp.screenWidth, gp.screenHeight, null);

        // Màn hình Credits
        if (showingCredits) {
            drawCreditsScreen(g2);
            return;
        }
        // Vẽ bảng menu chính
        g2.drawImage(boardImg, bgX, bgY, bgW, bgH, null);
        // Vẽ các nút
        if (buttonImgs == null) return;

        int centerX = gp.screenWidth / 2;
        int startY  = bgY + Math.round(bgH * 0.3f);
        int spacing = Math.round(bgH * 0.17f);

        for (int i = 0; i < 4; i++) {
            int state = (i == focusIndex) ? 1 : 0; // 1 = highlight
            int btnW = MENU_BUTTON_SIZE_W;
            int btnH = MENU_BUTTON_SIZE_H;
            int x = centerX - btnW / 2;
            int y = startY + i * spacing;

            g2.drawImage(buttonImgs[i][state], x, y, btnW, btnH, null);
        }
    }

    // UI Credits
    private void drawCreditsScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        int centerY = gp.screenHeight / 2;

        g2.drawString("Credits", 80, centerY - 60);

        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.drawString("Vu Hai Duong",         80, centerY - 20);
        g2.drawString("Luu Duc Thanh Dat",   80, centerY + 10);
        g2.drawString("Do Minh Vu",          80, centerY + 40);

        g2.setFont(new Font("Arial", Font.ITALIC, 18));
        g2.drawString("Press ESC to return", 80, centerY + 90);
    }
    // ==== Điều hướng bằng bàn phím ====
    public void moveUp()    { focusIndex = (focusIndex - 1 + 4) % 4; }
    public void moveDown()  { focusIndex = (focusIndex + 1)     % 4; }

    // Xử lý lựa chọn
    public void select() {
        if (showingCredits) {
            showingCredits = false;
            return;
        }
        switch (focusIndex) {
            case 0 -> { // PLAY
                gp.restartGame();
                gp.setGameState(GameState.PLAY);
                SoundManager.getInstance().playMusic(SoundManager.SoundID.MUSIC_THEME);
            }
            case 1 -> { // LOAD GAME
                if (gp.getSaveManager() != null) {
                    LoadResult result = gp.getSaveManager().loadGame(gp);
                    if (result == LoadResult.LOADED) {
                        gp.setGameState(GameState.PLAY);
                        SoundManager.getInstance().playMusic(SoundManager.SoundID.MUSIC_THEME);
                    }
                }
            }
            case 2 -> System.exit(0);      // QUIT
            case 3 -> showingCredits = true; //CREDITS
        }
    }

    // ESC để thoát màn hình Credits
    public void handleKey(int code) {
        if (showingCredits && code == java.awt.event.KeyEvent.VK_ESCAPE)
            showingCredits = false;
    }

    public boolean isShowingCredits() {
        return showingCredits;
    }

    // ==== Điều kiện vẽ UI ====
    @Override
    public boolean shouldRenderIn(GameState state) {
        return gp.getGameState() == GameState.START;
    }

    @Override
    public boolean shouldDraw() {
        return gp.getGameState() == GameState.START;
    }

    @Override
    public boolean shouldUpdate() {
        return gp.getGameState() == GameState.START;
    }
}
