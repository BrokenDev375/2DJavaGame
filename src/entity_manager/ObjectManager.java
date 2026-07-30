package entity_manager;

import game_data.ObjectData;
import object_data.ObjectSpawnPlan;
import object_data.ObjectDropRequest;
import object_data.TeleportTarget;
import object_data.WorldObject;
import object_data.WorldObjectFactory;
import object_data.WorldObjectSpawnTable;
import object_data.WorldObjectType;
import entity.Entity;
import main.GamePanel;
import main.DebugLog;

import java.awt.Graphics2D;
import java.util.*;    

public class ObjectManager {

    private final GamePanel gp;
    private final WorldObjectFactory objectFactory;
    private final List<ObjectSpawnPlan> spawnPlans;
    private final Map<Integer, List<WorldObject>> objectsByMap = new HashMap<>();

    public ObjectManager(GamePanel gp) {
        this(gp, gp.getWorldObjectFactory(), WorldObjectSpawnTable.defaultPlans(gp.tileSize()));
    }

    public ObjectManager(GamePanel gp, WorldObjectFactory objectFactory, List<ObjectSpawnPlan> spawnPlans) {
        this.gp = gp;
        this.objectFactory = objectFactory;
        this.spawnPlans = new ArrayList<>(spawnPlans);
        spawnObjects();
    }

    private void spawnObjects() {
        for (ObjectSpawnPlan plan : spawnPlans) {
            WorldObject object = objectFactory.create(plan.type(), plan.mapId());
            if (object instanceof TeleportTarget target) {
                plan.teleportDestination().ifPresent(target::setTeleportDestination);
            }
            addObject(object, plan.worldX(), plan.worldY());
        }
    }
    public void addObject(WorldObject obj, int wx, int wy) {
        obj.moveTo(wx, wy);
        objectsOnMap(obj.getMapIndex()).add(obj);
    }

    public boolean removeObject(int mapId, WorldObject obj) {
        return obj != null && objectsOnMap(mapId).remove(obj);
    }

    public List<WorldObject> getObjects(int mapId) {
        return Collections.unmodifiableList(objectsByMap.getOrDefault(mapId, Collections.emptyList()));
    }

    public List<ObjectData> snapshotObjects() {
        List<ObjectData> snapshots = new ArrayList<>();
        for (List<WorldObject> objects : objectsByMap.values()) {
            for (WorldObject object : objects) {
                object.type().ifPresent(type -> snapshots.add(new ObjectData(
                        type.name(),
                        object.getWorldX(),
                        object.getWorldY(),
                        true,
                        object.getWorldX(),
                        object.getWorldY(),
                        object.getMapIndex()
                )));
            }
        }
        return Collections.unmodifiableList(snapshots);
    }

    public void restoreObjects(List<ObjectData> snapshots) {
        objectsByMap.clear();
        if (snapshots == null) {
            return;
        }

        for (ObjectData saved : snapshots) {
            if (saved == null || !saved.isActive()) {
                continue;
            }

            Optional<WorldObjectType> type = resolveType(saved.getType());
            if (type.isEmpty()) {
                continue;
            }

            WorldObject object = objectFactory.create(type.get(), saved.getMapIndex());
            if (object instanceof TeleportTarget target) {
                findTeleportDestination(saved, type.get()).ifPresent(target::setTeleportDestination);
            }
            addObject(object, saved.getWorldX(), saved.getWorldY());
        }
    }

    public Optional<WorldObject> objectAt(int mapId, int index) {
        List<WorldObject> objects = objectsByMap.getOrDefault(mapId, Collections.emptyList());
        if (index < 0 || index >= objects.size()) return Optional.empty();
        return Optional.of(objects.get(index));
    }

    public Optional<WorldObject> findObjectByType(int mapId, WorldObjectType type) {
        for (WorldObject object : objectsByMap.getOrDefault(mapId, Collections.emptyList())) {
            if (object != null && object.isType(type)) {
                return Optional.of(object);
            }
        }
        return Optional.empty();
    }

    private List<WorldObject> objectsOnMap(int mapId) {
        return objectsByMap.computeIfAbsent(mapId, k -> new ArrayList<>());
    }

    public void update() {
        update(gp.getCurrentMap());
    }
    public void update(int mapId) {
        for (WorldObject o : objectsOnMap(mapId))
            o.update();
    }

    public void draw(Graphics2D g2, Entity player) {
        draw(g2, gp.getCurrentMap(), player);
    }

    public void draw(Graphics2D g2, int mapId, Entity player) {
        if (player == null) return;

        List<WorldObject> list = objectsOnMap(mapId);

        for (WorldObject o : list) {
            if (!gp.getCamera().isVisible(o, player, gp.tileSize())) {
                continue;
            }

            o.draw(g2, gp);

        }
    }
    public void reloadMapObjects(int mapId) {
        objectsByMap.clear();
        spawnObjects();
    }
    public void spawnHealthPosion(int mapIndex, int wx, int wy) {
        spawnDrop(ObjectDropRequest.of(WorldObjectType.HEALTH_POSION, mapIndex, wx, wy));
    }
    public void spawnSword(int mapIndex, int wx, int wy) {
        spawnDrop(ObjectDropRequest.of(WorldObjectType.SWORD, mapIndex, wx, wy));
    }

    public void spawnDrop(ObjectDropRequest request) {
        if (request == null) return;

        WorldObject object = objectFactory.create(request.type(), request.mapIndex());
        addObject(object, request.worldX(), request.worldY());

        int size = objectsOnMap(request.mapIndex()).size();
        DebugLog.info("[DROP] " + request.type() + " spawn at map " + request.mapIndex() +
                " (" + request.worldX() + ", " + request.worldY() + ")  -> list size = " + size);
    }

    private Optional<WorldObjectType> resolveType(String savedType) {
        if (savedType == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(WorldObjectType.valueOf(savedType.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<object_data.TeleportDestination> findTeleportDestination(
            ObjectData saved,
            WorldObjectType type
    ) {
        int spawnX = saved.hasSpawnIdentity() ? saved.getSpawnX() : saved.getWorldX();
        int spawnY = saved.hasSpawnIdentity() ? saved.getSpawnY() : saved.getWorldY();

        for (ObjectSpawnPlan plan : spawnPlans) {
            if (plan.type() == type
                    && plan.mapId() == saved.getMapIndex()
                    && plan.worldX() == spawnX
                    && plan.worldY() == spawnY) {
                return plan.teleportDestination();
            }
        }
        return Optional.empty();
    }
}
