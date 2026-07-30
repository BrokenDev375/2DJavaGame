package interact_manager.object_interact.weapon;

import interact_manager.object_interact.IObjectInteraction;
import interact_manager.object_interact.InteractionContext;
import object_data.weapons.WeaponType;


public class PickInteraction implements IObjectInteraction {

    @Override
    public void interact(InteractionContext context) {
        context.showMessage("press 'F' to hold pick");
        if (context.isPicked()) {
            context.playCoinSound();
            context.removeTouchedObject();
            context.equipWeapon(WeaponType.PICK);
            context.showMessage("Ya got a pick!");
        }
    }
}
