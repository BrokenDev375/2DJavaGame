package game_data;

import entity_manager.EntityManager;
import main.GamePanel;
import monster_data.Monster;
import player_manager.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class GameSnapshotMapper {
    public Optional<GameData> capture(GamePanel gp) {
        Player player = gp.getEntityManager().getPlayer();
        if (player == null) {
            return Optional.empty();
        }

        PlayerData playerData = new PlayerData(
                player.getWorldX(),
                player.getWorldY(),
                player.getHP(),
                player.getMaxHP(),
                player.equippedWeaponName(),
                gp.getCurrentMap(),
                player.getExp(),
                player.getLevel(),
                player.getKeyCount()
        );

        int mapIndex = gp.getCurrentMap();
        String mapPath = gp.getChunkManager().getMapPath();
        List<ObjectData> monsterList = mapMonsters(gp.getEntityManager(), mapIndex);

        return Optional.of(new GameData(playerData, monsterList, mapIndex, mapPath));
    }

    private List<ObjectData> mapMonsters(EntityManager em, int mapIndex) {
        List<ObjectData> monsterList = new ArrayList<>();

        for (var entity : em.getMonsters(mapIndex)) {
            if (entity instanceof Monster mon) {
                monsterList.add(new ObjectData(
                        mon.getName(),
                        mon.getWorldX(),
                        mon.getWorldY(),
                        !mon.isDead(),
                        mon.getHomeX(),
                        mon.getHomeY()
                ));
            }
        }

        return monsterList;
    }
}
