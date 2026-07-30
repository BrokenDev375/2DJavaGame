package interact_manager.object_interact;

import object_data.WorldObject;
import player_manager.Player;
import input_manager.InputController;
import main.CollisionChecker;
import main.GamePanel;
import ui.effects.MessageUI;
import java.util.Optional;

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
        if (index == CollisionChecker.NO_HIT) {
            player.endInteraction();
            return;
        }

        Optional<WorldObject> touchedObject = gp.getObjectManager().objectAt(gp.getCurrentMap(), index);
        if (touchedObject.isEmpty()) {
            player.endInteraction();
            return;
        }

        WorldObject obj = touchedObject.get();
        if (!obj.isOnMap(gp.getCurrentMap())) {
            return;
        }

        Optional<IObjectInteraction> handler = ObjectInteractionFactory.getHandler(obj);
        if (handler.isPresent()) {
            handler.get().interact(new InteractionContext(gp, player, input, obj));
        } else if (msgUI != null) {
            msgUI.showTouchMessage("Unknown object: " + obj.getName(), obj, gp);
        }
    }
}

