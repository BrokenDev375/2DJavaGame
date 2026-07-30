package game_data;

import main.GamePanel;
import player_manager.Player;

public final class GameRestorer {
    public LoadResult restore(GamePanel gp, GameData data) {
        if (data == null || data.getPlayer() == null) {
            return LoadResult.CORRUPTED;
        }

        Player player = gp.getEntityManager().getPlayer();
        if (player == null) {
            return LoadResult.FAILED;
        }

        restorePlayer(gp, data, player);
        restoreMonsters(gp, data);
        return LoadResult.LOADED;
    }

    private void restorePlayer(GamePanel gp, GameData data, Player player) {
        PlayerData savedPlayer = data.getPlayer();
        int savedMapIndex = savedPlayer.getMapIndex();

        player.restorePosition(savedPlayer.getWorldX(), savedPlayer.getWorldY());
        player.restoreProgression(savedPlayer.getLevel(), savedPlayer.getExp());
        player.restoreHealthStats(savedPlayer.getMaxHealth(), savedPlayer.getHealth());
        player.restoreKeyCount(savedPlayer.getKeyCount());

        if (savedPlayer.getWeaponName() != null) {
            gp.getWeaponFactory()
                    .createByName(savedPlayer.getWeaponName(), savedMapIndex)
                    .ifPresent(player::equipWeapon);
        }

        gp.setCurrentMap(savedMapIndex);
        player.placeOnMap(gp.getCurrentMap());

        String mapPath = data.getMapPath();
        if (mapPath == null || mapPath.isBlank()) {
            mapPath = "map" + gp.getCurrentMap();
        }
        gp.getChunkManager().loadMap(mapPath);

        if (gp.getObjectManager() != null) {
            if (data.hasWorldObjectSnapshot()) {
                gp.getObjectManager().restoreObjects(data.getWorldObjects());
            } else {
                gp.getObjectManager().reloadMapObjects(gp.getCurrentMap());
            }
        }

        gp.getChunkManager().updateChunks(player.getWorldX(), player.getWorldY());
    }

    private void restoreMonsters(GamePanel gp, GameData data) {
        if (gp.getEntityManager() == null || data.getObjects() == null) {
            return;
        }

        gp.getEntityManager().restoreMonsters(data.getObjects(), gp.getCurrentMap());
    }
}
