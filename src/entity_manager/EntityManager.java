package entity_manager;

import combat.CombatSystem;
import entity.Entity;
import monster_data.Monster;
import player_manager.Player;
import input_manager.InputController;
import object_data.WorldObject;           // << thêm
import main.GamePanel;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class EntityManager {

    private final GamePanel gp;
    private final Player player;
    private final MonsterManager mM;
    private final NPCManager npcM;

    public EntityManager(GamePanel gp, InputController input) {
        this.gp = gp;
        this.player = new Player(gp, input);
        this.mM = new MonsterManager(gp);
        this.npcM = new NPCManager(gp);
    }

    public Player getPlayer() { return player; }
    public List<Entity> getMonsters(int map) { return mM.getMonsters(map); }
    public List<Entity> getNPCs(int map) { return npcM.getNPCs(map); }


    public List<Entity> getMonsters() {
        return mM.getMonsters(gp.getCurrentMap());
    }

    public void update(int currentMap) {
        player.update();
        mM.update(currentMap, player.getWorldX(), player.getWorldY());

        List<Entity> monsters = mM.getMonsters(currentMap);

        for (Entity n : npcM.getNPCs(currentMap)) {
            n.update();
        }

        CombatSystem.resolvePlayerHits(player, monsters);

        for (Entity e : monsters) {
            if (e instanceof Monster m) {
                CombatSystem.resolveMonsterHitAgainstPlayer(m, player);
            }
        }

    }


    public void draw(Graphics2D g2, int currentMap) {

        List<Entity> all = new ArrayList<>();
        all.addAll(mM.getMonsters(currentMap));
        all.addAll(npcM.getNPCs(currentMap));
        all.add(player);

        all.sort((a, b) -> Integer.compare(a.getWorldY(), b.getWorldY()));
        for (Entity e : all) e.draw(g2);
    }
}
