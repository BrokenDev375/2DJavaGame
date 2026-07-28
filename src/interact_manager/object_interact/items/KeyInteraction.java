package interact_manager.object_interact.items;

import main.GamePanel;
import object_data.WorldObject;
import player_manager.Player;
import input_manager.InputController;
import interact_manager.object_interact.IObjectInteraction;
import sound_manager.SoundManager;
import ui.effects.MessageUI;

import java.util.List;

public class KeyInteraction implements IObjectInteraction {

    @Override
    public void interact(GamePanel gp, Player player, InputController input, WorldObject obj) {
        MessageUI msgUI = gp.getUiManager().get(MessageUI.class);
        List<WorldObject> objects = gp.getObjectManager().getObjects(gp.getCurrentMap());

        boolean pressed = input.isPicked();

        // ==== 1) BẤM F → NHẶT KEY ====
        if (pressed) {
            SoundManager.getInstance().playSE(SoundManager.SoundID.COIN);
            player.collectKey();
            objects.remove(obj); // xoá key khỏi map

            if (msgUI != null) {
                msgUI.showTouchMessage("You got a key!", obj, gp);
            }

            // Sau khi nhặt xong, frame này coi như kết thúc tương tác
            player.setInteracting(false);
            return;
        }

        // ==== 2) KHÔNG BẤM F → HIỆN HƯỚNG DẪN (KHÔNG SPAM) ====
        if (!player.isInteracting()) {
            if (msgUI != null) {
                msgUI.showTouchMessage("Press 'F' to pick the mystery key. You may need it in the future", obj, gp);
            }
            player.setInteracting(true);
        }
    }
}
