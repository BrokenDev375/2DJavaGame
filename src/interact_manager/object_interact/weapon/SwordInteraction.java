package interact_manager.object_interact.weapon;

import interact_manager.object_interact.IObjectInteraction;
import interact_manager.object_interact.InteractionContext;
import object_data.weapons.WeaponType;


public class SwordInteraction implements IObjectInteraction {

    @Override
    public void interact(InteractionContext context) {
        context.showMessage("It's the argonaut hero's sword! Press F to pick");
        if (context.isPicked()) {
            context.playCoinSound();
            context.removeTouchedObject();
            context.equipWeapon(WeaponType.SWORD);
            context.showMessage("Ya got the legendary sword!");
        }
    }
}
