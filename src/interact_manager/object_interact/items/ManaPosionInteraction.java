package interact_manager.object_interact.items;

import interact_manager.object_interact.IObjectInteraction;
import interact_manager.object_interact.InteractionContext;

public class ManaPosionInteraction implements IObjectInteraction {

    @Override
    public void interact(InteractionContext context) {
        context.showMessage("press 'F' to heal mana");
        if (context.isPicked()) {
            context.playCoinSound();
            context.removeTouchedObject();
            context.showMessage("full fuel!");
        }
    }
}

