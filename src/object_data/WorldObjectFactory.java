package object_data;

import main.GamePanel;
import object_data.items.HealthPosion;
import object_data.items.ManaPosion;
import object_data.items.ObjectChest;
import object_data.items.ObjectDoor;
import object_data.items.ObjectKey;
import object_data.weapons.WeaponFactory;
import object_data.weapons.WeaponType;

import java.util.Objects;

public final class WorldObjectFactory {
    private final GamePanel gp;
    private final WeaponFactory weaponFactory;

    public WorldObjectFactory(GamePanel gp, WeaponFactory weaponFactory) {
        this.gp = Objects.requireNonNull(gp);
        this.weaponFactory = Objects.requireNonNull(weaponFactory);
    }

    public WorldObject create(WorldObjectType type, int mapIndex) {
        Objects.requireNonNull(type);

        WorldObject object = switch (type) {
            case SHOP -> new Shop(gp, mapIndex);
            case DOOR -> new ObjectDoor(gp, mapIndex);
            case PORTAL -> new ObjectPortal(gp, mapIndex);
            case KEY -> new ObjectKey(gp, mapIndex);
            case HEALTH_POSION -> new HealthPosion(gp, mapIndex);
            case MANA_POSION -> new ManaPosion(gp, mapIndex);
            case CHEST -> new ObjectChest(gp, mapIndex);
            case SWORD -> weaponFactory.create(WeaponType.SWORD, mapIndex);
            case AXE -> weaponFactory.create(WeaponType.AXE, mapIndex);
            case PICK -> weaponFactory.create(WeaponType.PICK, mapIndex);
        };
        object.setType(type);
        return object;
    }
}
