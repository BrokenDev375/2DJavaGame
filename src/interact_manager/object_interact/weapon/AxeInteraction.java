package interact_manager.object_interact.weapon;

import interact_manager.object_interact.IObjectInteraction;
import interact_manager.object_interact.InteractionContext;
import object_data.weapons.WeaponType;


public class AxeInteraction implements IObjectInteraction {

    @Override
    public void interact(InteractionContext context) {
        context.showMessage("press 'F' to pick Leviathan Axe");
        if (context.isPicked()) {
            context.playCoinSound();
            context.removeTouchedObject();
            context.equipWeapon(WeaponType.AXE);
            context.showMessage("U got ur grandpa's axe! Press j to perform a hit");
        }
    }
}
