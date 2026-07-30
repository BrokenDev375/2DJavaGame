package input_manager;

import java.awt.Component;
import  main.GamePanel;

public class InputManager {
    private final KeyHandler keyHandler;
    private final GameCommandHandler keyCommander;
    
    //component là kiểu cha chung để nhận GamePanel vào mà không cần ghi rõ GamePanel trong InputManager.
    public InputManager(Component component, GamePanel gp) {
        keyHandler = new KeyHandler();
        keyCommander = new GameCommandHandler(gp);
         // gắn KeyHandler
        component.addKeyListener(keyHandler);
        component.addKeyListener(keyCommander);
        // cho phép nhận focus
        component.setFocusable(true);
        // yêu cầu focus ngay
        component.requestFocusInWindow();
    }

    public InputController getKeyController() {
        return keyHandler;
    }
    public GameCommandHandler getKeyCommander(){
        return keyCommander;
    }
}

