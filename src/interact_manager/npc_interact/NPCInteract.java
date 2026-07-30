package interact_manager.npc_interact;

import entity.Entity;
import main.CollisionChecker;
import main.GamePanel;
import main.GameState;
import npc_data.Talkable;
import player_manager.Player;
import input_manager.InputController;
import ui.effects.DialogueUI;
import ui.effects.MessageUI;

public class NPCInteract {

    private final GamePanel gp;
    private final Player player;
    private final InputController input;
    private final MessageUI msgUI;

    public NPCInteract(GamePanel gp, Player player, InputController input) {
        this.gp = gp;
        this.player = player;
        this.input = input;
        this.msgUI = gp.getUiManager().get(MessageUI.class);
    }

    public void updateProximity() {
        int npcIndex = gp.getCollisionChecker().checkEntity(
                player,
                gp.getEntityManager().getNPCs(gp.getCurrentMap()),
                player.getWorldX(),
                player.getWorldY()
        );
        handle(npcIndex);
    }

    public void handle(int index) {
        if (index == CollisionChecker.NO_HIT) {
            player.setInteracting(false);
            return;
        }

        var npcHit = gp.getEntityManager().getNPCAt(gp.getCurrentMap(), index);
        if (npcHit.isEmpty()) return;
        Entity npc = npcHit.get();

        if (gp.getGameState() != GameState.PLAY) return;

        if (!input.isTalkPressed()) {
            if (msgUI != null && "oldman".equalsIgnoreCase(npc.getName()) && !player.isInteracting()) {
                msgUI.showTouchMessage(
                        "Press 'E' to talk to your grandpa.",
                        npc,
                        gp
                );
                player.setInteracting(true);
            }
            return;
        }

        DialogueUI dialogue = gp.getUiManager().get(DialogueUI.class);
        if (dialogue != null && dialogue.isActive()) return;
        if (gp.getGameState() != GameState.PLAY) return;

        if (npc instanceof Talkable talkable) {
            talkable.speak(gp);
            gp.setGameState(GameState.DIALOGUE);
            input.resetTalkKey();
            player.setInteracting(true);
        }
    }
}
