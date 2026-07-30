package interact_manager.object_interact.items;

import interact_manager.object_interact.IObjectInteraction;
import interact_manager.object_interact.InteractionContext;
import object_data.TeleportTarget;

public class DoorInteraction implements IObjectInteraction {

    @Override
    public void interact(InteractionContext context) {
        context.showMessage("press 'F' to shopping");
        if (context.isPicked() && context.object() instanceof TeleportTarget target) {
            target.teleportDestination().ifPresent(destination ->
                    context.transitionTo(destination, "shop!")
            );
        }
    }
}
