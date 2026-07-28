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

public class SwordInteraction implements IObjectInteraction {

    @Override
    public void interact(GamePanel gp, Player player, InputController input, WorldObject obj) {
        MessageUI msgUI = gp.getUiManager().get(MessageUI.class);
        List<WorldObject> objects = gp.getObjectManager().getObjects(gp.getCurrentMap());

        if (msgUI != null) msgUI.showTouchMessage("It's the argonaut hero's sword! Press F to pick", obj, gp);
        if (input.isPicked()) {
            SoundManager.getInstance().playSE(SoundManager.SoundID.COIN);
            objects.remove(obj);
            player.equipWeapon(new Sword(gp, gp.getCurrentMap()));
            if (msgUI != null) msgUI.showTouchMessage("Ya got the legendary sword!", obj, gp);
        }
    }
}
