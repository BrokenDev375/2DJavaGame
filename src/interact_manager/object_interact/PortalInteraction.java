package interact_manager.object_interact;

import object_data.TeleportTarget;

public class PortalInteraction implements IObjectInteraction {

    @Override
    public void interact(InteractionContext context) {
        context.showMessage("press 'F' to tele");
        if (context.isPicked() && context.object() instanceof TeleportTarget target) {
            target.teleportDestination().ifPresent(destination ->
                    context.transitionTo(destination, "Teleported!")
            );
        }
    }
}

