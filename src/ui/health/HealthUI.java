package ui.health;

import main.GamePanel;
import main.GameState;
import ui.base.BaseUI;

import java.awt.*;

public class HealthUI extends BaseUI {
    public HealthUI(GamePanel gp){ super(gp); }

    @Override
    public void update(){}

    @Override
    public void draw(Graphics2D g2){
        // Lấy chỉ số máu hiện tại và tối đa
        int max = gp.getEntityManager().getPlayer().getMaxHP();
        int cur = gp.getEntityManager().getPlayer().getHP();

        // Kích thước và vị trí của thanh máu
        int barWidth = gp.tileSize() * 4;
        int barHeight = gp.tileSize() / 2;
        int x = gp.tileSize() / 2, y = gp.tileSize() / 2;

        // Nền thanh máu
        g2.setColor(Color.gray);
        g2.fillRect(x, y, barWidth, barHeight);

        // Phần đỏ biểu thị lượng máu hiện tại
        int healthWidth = (int)((cur / (double)max) * barWidth);
        g2.setColor(Color.red);
        g2.fillRect(x, y, healthWidth, barHeight);

        // Viền trắng
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, barWidth, barHeight);
    }

    @Override
    public boolean shouldRenderIn(GameState state) {
        // Chỉ hiển thị trong trạng thái PLAY
        return state == GameState.PLAY;
    }

    @Override
    public boolean shouldUpdate() {
        // Không cần update vì thanh máu lấy trực tiếp từ Player
        return false;
    }
}
