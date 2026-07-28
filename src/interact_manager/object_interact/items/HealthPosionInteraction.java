/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interact_manager.object_interact.items;

import main.GamePanel;
import object_data.WorldObject;
import player_manager.Player;
import input_manager.InputController;
import interact_manager.object_interact.IObjectInteraction;
import sound_manager.SoundManager;
import ui.effects.MessageUI;
import java.util.List;

public class HealthPosionInteraction implements IObjectInteraction {

    @Override
    public void interact(GamePanel gp, Player player, InputController input, WorldObject obj) {
        MessageUI msgUI = gp.getUiManager().get(MessageUI.class);
        List<WorldObject> objects = gp.getObjectManager().getObjects(gp.getCurrentMap());

        if (msgUI != null) {
            msgUI.showTouchMessage("press 'F' to heal health", obj, gp);
        }

        if (input.isPicked()) {
            SoundManager.getInstance().playSE(SoundManager.SoundID.COIN);
            objects.remove(obj);

            // Hồi 10% máu
            player.healPercent(0.10);

            if (msgUI != null) {
                msgUI.showTouchMessage("Healed 10% HP", obj, gp);
            }
        }
    }
}

