package player_manager;

import main.GamePanel;
import object_data.WorldObject; // chỉ để rõ ràng; không dùng trực tiếp cũng OK

public class PlayerInteractor {
    private final Player player;
    private final GamePanel gp;

    public PlayerInteractor(Player player, GamePanel gp){
        this.player = player;
        this.gp = gp;
    }

    public void allCheck(int nextX, int nextY){
        gp.getCollisionChecker().checkTile(player, nextX, nextY);

        // TÍNH delta từ vị trí hiện tại -> vị trí kế tiếp
        int dx = nextX - player.getWorldX();
        int dy = nextY - player.getWorldY();

        // DÙNG API MỚI cho object (WorldObject)
        int objIndex = gp.getCollisionChecker().checkWorldObject(
                player,
                gp.getObjectManager().getObjects(gp.getCurrentMap()),
                dx, dy
        );
        player.interactObject(objIndex);

        int monsterIndex = gp.getCollisionChecker().checkEntity(player, gp.getEntityManager().getMonsters(gp.getCurrentMap()), nextX, nextY);
        player.interactMonster(monsterIndex);

        int npcIndex = gp.getCollisionChecker().checkEntity(player, gp.getEntityManager().getNPCs(gp.getCurrentMap()), nextX, nextY);
        player.interactNPC(npcIndex);
    }
}
