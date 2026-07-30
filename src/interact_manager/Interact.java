package interact_manager;

import interact_manager.object_interact.ObjectInteract;
import interact_manager.monster_interact.MonsterInteract;
import interact_manager.npc_interact.NPCInteract;
import main.GamePanel;
import player_manager.Player;
import input_manager.InputController;

public class Interact {

    private final ObjectInteract objectInteract;
    private final MonsterInteract monsterInteract;
    private final NPCInteract npcInteract;

    public Interact(GamePanel gp, Player player, InputController input) {
        // tạo từng nhóm interact riêng
        this.objectInteract = new ObjectInteract(gp, player, input);
        this.monsterInteract = new MonsterInteract(gp, player, input);
        this.npcInteract = new NPCInteract(gp, player, input);
    }

    public void InteractObject(int index) {
        objectInteract.handle(index);
    }

    public void InteractMonster(int index) {
        monsterInteract.handle(index);
    }

    public void InteractNPC(int index) {
        npcInteract.handle(index);
    }

    public void updateNPCProximity() {
        npcInteract.updateProximity();
    }
}
