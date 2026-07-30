package ui.effects;

import main.GamePanel;
import main.GameState;
import ui.base.BaseUI;

import java.awt.*;

public class MessageUI extends BaseUI {
    private boolean show;
    private String message;
    private int counter;

    public MessageUI(GamePanel gp){ super(gp); }

    public void showTouchMessage(String text, Object obj, GamePanel gp) {
        message = text;
        show = true;
        counter = 0;
    }
    @Override
    public void update(){
        if(show && ++counter > 120) show = false;
    }

    @Override
    public void draw(Graphics2D g2){
        if(show){
            g2.setColor(Color.white);
            g2.setFont(new Font("Arial", Font.PLAIN, 25));
            g2.drawString(message, gp.tileSize(), gp.tileSize()*5);
        }
    }
    @Override
    public boolean shouldRenderIn(GameState state) {
        return true;
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }

    @Override
    public boolean shouldDraw() {
        return show;
    }
    public void hideTouchMessage() {
        show = false;
    }
}
