package ui.effects;

import main.GamePanel;
import ui.base.BaseUI;

import java.awt.*;

public class FadeUI extends BaseUI {
    private float alpha = 0f;
    private boolean fading = false;
    private int phase = 0; // 1 = fade out, 2 = fade in
    private Runnable onFadeComplete;

    public FadeUI(GamePanel gp){ super(gp); }

    public void startFade(Runnable onComplete){
        fading = true; phase = 1; alpha = 0f;
        onFadeComplete = onComplete;
    }

    @Override
    public void update(){
        if(!fading) return;

        if(phase == 1){
            alpha += 0.05f;
            if(alpha >= 1f){
                if(onFadeComplete != null){ onFadeComplete.run(); onFadeComplete = null; }
                phase = 2;
            }
        } else if(phase == 2){
            alpha -= 0.05f;
            if(alpha <= 0f) fading = false;
        }
    }

    @Override
    public void draw(Graphics2D g2){
        if(fading){
            g2.setColor(new Color(0,0,0,(int)(alpha*255)));
            g2.fillRect(0,0,gp.screenWidth(),gp.screenHeight());
        }
    }
}
