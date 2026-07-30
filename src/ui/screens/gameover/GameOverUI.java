package ui.screens.gameover;

import main.GamePanel;
import main.GameState;
import ui.base.BaseUI;

import java.awt.*;

public class GameOverUI extends BaseUI {
    // 0 = Restart, 1 = Quit
    private int commandNum = 0;

    public GameOverUI(GamePanel gp) {
        super(gp);
    }
    // Di chuyển lên (chọn Restart)
    public void moveUp() {
        commandNum = (commandNum == 0) ? 1 : 0;
    }
    // Di chuyển xuống (chọn Quit)
    public void moveDown() {
        commandNum = (commandNum == 1) ? 0 : 1;
    }
    // Xử lý lựa chọn
    public void select() {
        if (commandNum == 0) {
            // Restart game
            gp.restartGame();
        } else if (commandNum == 1) {
            // Quay lại màn hình START
            gp.setGameState(GameState.START);
        }
    }
    @Override
    public void update() {
        // UI này không có hiệu ứng update riêng
    }
    @Override
    public void draw(Graphics2D g2) {
        if (gp.getGameState() != GameState.GAME_OVER) return;
        // Nền mờ phủ toàn màn hình
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth(), gp.screenHeight());
        int x;
        int y;
        String text;
        // ===== TIÊU ĐỀ "GAME OVER" =====
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 110F));
        text = "Game Over";

        // Đổ bóng
        g2.setColor(Color.black);
        x = getXforCenteredText(g2, text);
        y = gp.tileSize() * 4;
        g2.drawString(text, x, y);

        // Chữ màu trắng chính
        g2.setColor(Color.white);
        g2.drawString(text, x - 4, y - 4);

        // ===== Tùy chọn Restart =====
        g2.setFont(g2.getFont().deriveFont(50F));
        text = "Restart";
        x = getXforCenteredText(g2, text);
        y += gp.tileSize() * 4;

        g2.drawString(text, x, y);
        if (commandNum == 0) {
            g2.drawString(">", x - 40, y);  // Hiển thị dấu chọn
        }
        // ===== Tùy chọn Quit =====
        text = "Quit";
        x = getXforCenteredText(g2, text);
        y += 55;

        g2.drawString(text, x, y);
        if (commandNum == 1) {
            g2.drawString(">", x - 40, y);  // Hiển thị dấu chọn
        }
    }
    // Trả về tọa độ X để căn giữa văn bản
    public int getXforCenteredText(Graphics2D g2, String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth() / 2 - length / 2;
    }
    @Override
    public boolean shouldRenderIn(GameState state) {
        // Chỉ hiển thị ở trạng thái GAME_OVER
        return state == GameState.GAME_OVER;
    }
}
