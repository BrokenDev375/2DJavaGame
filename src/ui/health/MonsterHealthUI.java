package ui.health;

import entity.Entity;
import main.GamePanel;
import monster_data.Monster;
import ui.base.BaseUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class MonsterHealthUI extends BaseUI {

    public MonsterHealthUI(GamePanel gp) {
        super(gp);
    }

    @Override
    public void update() {}

    @Override
    public void draw(Graphics2D g2) {
        for (Entity entity : gp.getEntityManager().getMonsters()) {
            if (!(entity instanceof Monster monster)) continue;
            if (monster.isDead()) continue;

            int max = monster.getMaxHP();
            int current = monster.getHP();

            int screenX = gp.getCamera().screenX(monster, gp.getEntityManager().getPlayer());
            int screenY = gp.getCamera().screenY(monster, gp.getEntityManager().getPlayer());

            int barWidth = gp.tileSize();
            int barHeight = gp.tileSize() / 8;
            int barX = screenX + gp.tileSize() / 2 - barWidth / 2;
            int barY = screenY - gp.tileSize() - (gp.tileSize() / 2 + 6);

            g2.setColor(Color.darkGray);
            g2.fillRect(barX, barY, barWidth, barHeight);

            int healthWidth = (int) ((current / (double) max) * barWidth);
            g2.setColor(new Color(220, 0, 0));
            g2.fillRect(barX, barY, healthWidth, barHeight);

            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(barX, barY, barWidth, barHeight);
        }
    }
}
