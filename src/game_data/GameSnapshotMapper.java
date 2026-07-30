package game_data;

import main.GamePanel;
import player_manager.Player;

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
        List<ObjectData> monsterList = gp.getEntityManager().snapshotMonsters();
        List<ObjectData> worldObjects = gp.getObjectManager().snapshotObjects();

        return Optional.of(new GameData(playerData, monsterList, mapIndex, mapPath, worldObjects));
    }
}
