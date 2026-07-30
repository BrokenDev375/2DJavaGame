package interact_manager.object_interact.items;

import interact_manager.object_interact.IObjectInteraction;
import interact_manager.object_interact.InteractionContext;

public class HealthPosionInteraction implements IObjectInteraction {

    @Override
    public void interact(InteractionContext context) {
        context.showMessage("press 'F' to heal health");

        if (context.isPicked()) {
            context.playCoinSound();
            context.removeTouchedObject();

            context.player().healPercent(0.10);

            context.showMessage("Healed 10% HP");
        }
    }
}

