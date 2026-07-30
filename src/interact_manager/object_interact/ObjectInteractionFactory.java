package interact_manager.object_interact;

import interact_manager.object_interact.items.ManaPosionInteraction;
import interact_manager.object_interact.items.KeyInteraction;
import interact_manager.object_interact.items.HealthPosionInteraction;
import interact_manager.object_interact.items.DoorInteraction;
import interact_manager.object_interact.weapon.*;
import object_data.WorldObject;
import object_data.WorldObjectType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class ObjectInteractionFactory {
    private static final Map<WorldObjectType, IObjectInteraction> HANDLERS = createHandlers();

    private ObjectInteractionFactory() {}

    public static Optional<IObjectInteraction> getHandler(WorldObject object) {
        if (object == null) {
            return Optional.empty();
        }

        return object.type().flatMap(ObjectInteractionFactory::getHandler);
    }

    public static Optional<IObjectInteraction> getHandler(WorldObjectType type) {
        return Optional.ofNullable(type == null ? null : HANDLERS.get(type));
    }

    private static Map<WorldObjectType, IObjectInteraction> createHandlers() {
        Map<WorldObjectType, IObjectInteraction> handlers = new EnumMap<>(WorldObjectType.class);
        handlers.put(WorldObjectType.KEY, new KeyInteraction());
        handlers.put(WorldObjectType.PORTAL, new PortalInteraction());
        handlers.put(WorldObjectType.DOOR, new DoorInteraction());
        handlers.put(WorldObjectType.MANA_POSION, new ManaPosionInteraction());
        handlers.put(WorldObjectType.HEALTH_POSION, new HealthPosionInteraction());
        handlers.put(WorldObjectType.SWORD, new SwordInteraction());
        handlers.put(WorldObjectType.AXE, new AxeInteraction());
        handlers.put(WorldObjectType.PICK, new PickInteraction());
        return Collections.unmodifiableMap(handlers);
    }
}

