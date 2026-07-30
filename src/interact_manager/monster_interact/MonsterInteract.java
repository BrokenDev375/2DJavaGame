package interact_manager.monster_interact;

import main.GamePanel;
import player_manager.Player;
import input_manager.InputController;

public class MonsterInteract {
    private final GamePanel gp;
    private final Player player;
    private final InputController input;

    public MonsterInteract(GamePanel gp, Player player, InputController input) {
        this.gp = gp;
        this.player = player;
        this.input = input;
    }

    public void handle(int index) {
    }
}

