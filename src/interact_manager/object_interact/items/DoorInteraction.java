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
import ui.effects.MessageUI;
import ui.effects.FadeUI;

public class DoorInteraction implements IObjectInteraction {

    @Override
    public void interact(GamePanel gp, Player player, InputController input, WorldObject obj) {
        MessageUI msgUI = gp.getUiManager().get(MessageUI.class);
        FadeUI fadeUI = gp.getUiManager().get(FadeUI.class);

        if (msgUI != null) msgUI.showTouchMessage("press 'F' to shopping", obj, gp);
        if (input.isPicked()) {
            if (fadeUI != null) fadeUI.startFade(() -> {
                // Chuyển map
                if ("map0".equals(gp.getChunkManager().getMapPath())) {
                    gp.getChunkManager().loadMap("map3");
                    gp.setCurrentMap(3);
                } else if ("map3".equals(gp.getChunkManager().getMapPath())) {
                    gp.getChunkManager().loadMap("map0");
                    gp.setCurrentMap(0);
                }

                // Tìm cửa ở map đích
                var destList = gp.getObjectManager().getObjects(gp.getCurrentMap());
                WorldObject dest = null;
                for (var wo : destList) {
                    if (wo != null && wo.isNamed("door")) {
                        dest = wo;
                        break;
                    }
                }

                if (dest != null) {
                    gp.getEntityManager().getPlayer().moveTo(dest.getWorldX(), dest.getWorldY());
                    gp.getEntityManager().getPlayer().setMapIndex(gp.getCurrentMap());
                }

                gp.getChunkManager().clearChunks();
                gp.getChunkManager().updateChunks(gp.getEntityManager().getPlayer().getWorldX(), gp.getEntityManager().getPlayer().getWorldY());

                if (msgUI != null) msgUI.showTouchMessage("shop!", obj, gp);
            });
        }
    }
}
