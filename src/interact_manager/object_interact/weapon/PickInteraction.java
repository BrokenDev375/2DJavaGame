package interact_manager.object_interact.weapon;

import main.GamePanel;
import object_data.WorldObject;
import object_data.weapons.*;
import player_manager.Player;
import input_manager.InputController;
import interact_manager.object_interact.IObjectInteraction;
import sound_manager.SoundManager;
import ui.effects.MessageUI;

import java.util.List;

public class PickInteraction implements IObjectInteraction {

    @Override
    public void interact(GamePanel gp, Player player, InputController input, WorldObject obj) {
        MessageUI msgUI = gp.getUiManager().get(MessageUI.class);
        List<WorldObject> objects = gp.getObjectManager().getObjects(gp.getCurrentMap());

        if (msgUI != null) msgUI.showTouchMessage("press 'F' to hold pick", obj, gp);
        if (input.isPicked()) {
            SoundManager.getInstance().playSE(SoundManager.SoundID.COIN);
            objects.remove(obj);
            player.equipWeapon(new Pick(gp, gp.getCurrentMap()));
            if (msgUI != null) msgUI.showTouchMessage("Ya got a pick!", obj, gp);
        }
    }
}
