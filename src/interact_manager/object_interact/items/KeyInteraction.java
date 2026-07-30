package interact_manager.object_interact.items;

import interact_manager.object_interact.IObjectInteraction;
import interact_manager.object_interact.InteractionContext;


public class KeyInteraction implements IObjectInteraction {

    @Override
    public void interact(InteractionContext context) {
        boolean pressed = context.isPicked();

        if (pressed) {
            context.playCoinSound();
            context.player().collectKey();
            context.removeTouchedObject();
            context.showMessage("You got a key!");

            context.player().setInteracting(false);
            return;
        }

        if (!context.player().isInteracting()) {
            context.showMessage("Press 'F' to pick the mystery key. You may need it in the future");
            context.player().setInteracting(true);
        }
    }
}
