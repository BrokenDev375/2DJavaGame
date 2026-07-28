/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package input_manager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener, InputController{
    private boolean down, left, right, up;
    private boolean pick, attack, talk;

    // default must be public 
    @Override
    public void keyTyped(KeyEvent e){
        
    }
    @Override
    public void keyPressed(KeyEvent e){
        
        int code = e.getKeyCode();
        // play mode
        if( code == KeyEvent.VK_W){up = true;} 
            
        if( code == KeyEvent.VK_S){down = true;} 
            
        if( code == KeyEvent.VK_A){left = true;} 
            
        if( code == KeyEvent.VK_D){right = true;}
            
        if( code == KeyEvent.VK_F){pick = true;}
        if (code == KeyEvent.VK_J){attack = true; }

        if (code == KeyEvent.VK_E) { talk = true; }
    }
    @Override
    public void keyReleased(KeyEvent e){
        int code = e.getKeyCode();
        if( code == KeyEvent.VK_W){up = false;} 
        
        if( code == KeyEvent.VK_S){down = false;} 
        
        if( code == KeyEvent.VK_A){left = false;} 
        
        if( code == KeyEvent.VK_D){right = false;} 
        
        if( code == KeyEvent.VK_F){pick = false;}
        if (code == KeyEvent.VK_J){attack = false;}

        if (code == KeyEvent.VK_E) { talk = false; }

    }
    
    @Override
    public boolean isUpPressed() {return up;}
    @Override
    public boolean isDownPressed() {return down;}
    @Override
    public boolean isLeftPressed() {return left;}
    @Override
    public boolean isRightPressed() {return right;}
    @Override
    public boolean isPicked(){return pick;}
    public boolean isAttackPressed(){return attack;}
    @Override
    public boolean isTalkPressed() { return talk; }
    @Override
    public void resetTalkKey() {
        talk = false;
    }

}
