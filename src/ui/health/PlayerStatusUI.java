package ui.health;   // hoặc ui.effects tuỳ bạn

import main.GamePanel;
import main.GameState;
import player_manager.Player;
import ui.base.BaseUI;

import java.awt.*;

public class PlayerStatusUI extends BaseUI {

    public PlayerStatusUI(GamePanel gp) {
        super(gp);
    }

    @Override
    public void update() {}
    @Override
    public void draw(Graphics2D g2) {
        if (gp == null || gp.getEntityManager() == null) return;
        Player p = gp.getEntityManager().getPlayer();
        if (p == null) return;

        // Chỉ số người chơi
        int level     = p.getLevel();
        int exp       = p.getExp();
        int expToNext = p.getExpToNext();

        // ==== Vị trí panel ngay dưới thanh máu ====
        int panelX = gp.tileSize() / 2;
        int panelY = gp.tileSize() / 2 + gp.tileSize() + 4;
        int panelW = gp.tileSize() * 5;
        int panelH = gp.tileSize();

        Color oldColor = g2.getColor();
        Font oldFont   = g2.getFont();

        // Nền mờ
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 10, 10);

        // Viền
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 10, 10);

        // ==== Text hiển thị level và EXP ====
        int textX = panelX + 10;
        int textY = panelY + 20;

        // Level
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Lv " + level, textX, textY);

        // EXP ngay dưới level
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("EXP " + exp + "/" + expToNext, textX, textY + 14);

        // ==== Thanh EXP nhỏ nằm cuối panel ====
        int barX = panelX + 10;
        int barW = panelW - 20;
        int barH = 5;
        int barY = panelY + panelH - barH - 6;

        double ratio = expToNext > 0 ? (double) exp / expToNext : 0;
        ratio = Math.max(0, Math.min(1, ratio));  // ép về [0, 1]

        // Nền thanh EXP
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barW, barH);

        // Phần EXP đã đạt
        g2.setColor(new Color(0, 200, 255));
        g2.fillRect(barX, barY, (int) (barW * ratio), barH);

        // Viền thanh
        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barW, barH);

        // Khôi phục lại màu và font cũ
        g2.setColor(oldColor);
        g2.setFont(oldFont);
    }

    @Override
    public boolean shouldRenderIn(GameState state) {
        // Hiển thị trong lúc chơi và khi pause
        return state == GameState.PLAY || state == GameState.PAUSE;
    }
    @Override
    public boolean shouldUpdate() {
        return true;
    }

    @Override
    public boolean shouldDraw() {
        return true;
    }
}
