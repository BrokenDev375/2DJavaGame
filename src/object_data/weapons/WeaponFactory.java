package object_data.weapons;

import main.GamePanel;

import java.util.Objects;
import java.util.Optional;

public final class WeaponFactory {
    private final GamePanel gp;

    public WeaponFactory(GamePanel gp) {
        this.gp = Objects.requireNonNull(gp);
    }

    public Weapon create(WeaponType type, int mapIndex) {
        Objects.requireNonNull(type);

        return switch (type) {
            case SWORD -> new Sword(gp, mapIndex);
            case AXE -> new Axe(gp, mapIndex);
            case PICK -> new Pick(gp, mapIndex);
        };
    }

    public Optional<Weapon> createByName(String name, int mapIndex) {
        return resolveType(name).map(type -> create(type, mapIndex));
    }

    public static Optional<WeaponType> resolveType(String name) {
        return WeaponType.fromName(name);
    }
}
