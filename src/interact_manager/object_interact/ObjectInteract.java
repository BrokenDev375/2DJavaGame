/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interact_manager.object_interact;

import object_data.WorldObject;
import player_manager.Player;
import input_manager.InputController;
import main.GamePanel;
import ui.effects.MessageUI;
import java.util.List;

public class ObjectInteract {

    private final GamePanel gp;
    private final Player player;
    private final InputController input;
    private final MessageUI msgUI;

    public ObjectInteract(GamePanel gp, Player player, InputController input) {
        this.gp = gp;
        this.player = player;
        this.input = input;
        this.msgUI = gp.getUiManager().get(MessageUI.class);
    }

    public void handle(int index) {
        if (index != 999) {
            List<WorldObject> objects = gp.getObjectManager().getObjects(gp.getCurrentMap());
            if (index >= 0 && index < objects.size()) {
                WorldObject obj = objects.get(index);
                if (obj != null && obj.isOnMap(gp.getCurrentMap())) {
                    IObjectInteraction handler = ObjectInteractionFactory.getHandler(obj.getName());
                    if (handler != null) {
                        handler.interact(gp, player, input, obj);
                    } else if (msgUI != null) {
                        msgUI.showTouchMessage("Unknown object: " + obj.getName(), obj, gp);
                    }
                }
            }
        } else {

            player.setInteracting(false);

        }
    }
}

