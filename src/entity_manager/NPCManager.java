package entity_manager;

import entity.Entity;
import main.GamePanel;
import npc_data.*;

import java.util.*;

public class NPCManager {
    private final GamePanel gp;
    private final Map<Integer, List<Entity>> npcsByMap = new HashMap<>();

    public NPCManager(GamePanel gp) {
        this.gp = gp;
        spawnNPCs();
    }

    private void spawnNPCs() {
        int t = gp.tileSize;
        NPC_Oldman oldman0 = new NPC_Oldman(gp, 3);
        oldman0.spawnAt(10 * t, 20 * t);
        addNPC(oldman0);

    }

    public void addNPC(Entity npc) {
        npcsOnMap(npc.getMapIndex()).add(npc);
    }

    public List<Entity> getNPCs(int mapId) {
        return Collections.unmodifiableList(npcsByMap.getOrDefault(mapId, Collections.emptyList()));
    }

    public Optional<Entity> npcAt(int mapId, int index) {
        List<Entity> npcs = npcsByMap.get(mapId);
        if (npcs == null || index < 0 || index >= npcs.size()) return Optional.empty();
        return Optional.of(npcs.get(index));
    }

    private List<Entity> npcsOnMap(int mapId) {
        return npcsByMap.computeIfAbsent(mapId, k -> new ArrayList<>());
    }

    public void update(int mapId) {
        for (Entity npc : npcsByMap.getOrDefault(mapId, Collections.emptyList())) {
            npc.update();
        }
    }

    public void draw(java.awt.Graphics2D g2, int mapId) {
        for (Entity npc : npcsByMap.getOrDefault(mapId, Collections.emptyList())) {
            npc.draw(g2);
        }
    }
}
