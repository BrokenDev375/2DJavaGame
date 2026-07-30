package game_data;

import entity.Entity;
import main.GamePanel;
import monster_data.Monster;
import player_manager.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

        player.restorePosition(savedPlayer.getWorldX(), savedPlayer.getWorldY());
        player.restoreProgression(savedPlayer.getLevel(), savedPlayer.getExp());
        player.restoreHealthStats(savedPlayer.getMaxHealth(), savedPlayer.getHealth());

        if (savedPlayer.getWeaponName() != null) {
            gp.getWeaponFactory()
                    .createByName(savedPlayer.getWeaponName(), data.getMapIndex())
                    .ifPresent(player::equipWeapon);
        }

        gp.setCurrentMap(savedPlayer.getMapIndex());
        player.placeOnMap(gp.getCurrentMap());

        String mapPath = data.getMapPath() == null ? "map" + gp.getCurrentMap() : data.getMapPath();
        gp.getChunkManager().loadMap(mapPath);

        if (gp.getObjectManager() != null) {
            gp.getObjectManager().reloadMapObjects(gp.getCurrentMap());
        }

        gp.getEntityManager().update(gp.getCurrentMap());
    }

    private void restoreMonsters(GamePanel gp, GameData data) {
        if (data.getObjects() == null) {
            return;
        }

        List<Entity> monsters = gp.getEntityManager().getMonsters(gp.getCurrentMap());
        Set<Monster> restored = new HashSet<>();

        for (int i = 0; i < data.getObjects().size(); i++) {
            ObjectData saved = data.getObjects().get(i);
            Optional<Monster> target = findMonsterBySpawn(monsters, saved, restored);
            if (target.isEmpty()) {
                target = findMonsterByIndex(monsters, i, restored);
            }

            target.ifPresent(mon -> {
                restoreMonster(mon, saved);
                restored.add(mon);
            });
        }
    }

    private Optional<Monster> findMonsterBySpawn(List<Entity> monsters, ObjectData saved, Set<Monster> restored) {
        if (saved == null || !saved.hasSpawnIdentity()) {
            return Optional.empty();
        }

        for (Entity entity : monsters) {
            if (entity instanceof Monster mon
                    && !restored.contains(mon)
                    && mon.getHomeX() == saved.getSpawnX()
                    && mon.getHomeY() == saved.getSpawnY()) {
                return Optional.of(mon);
            }
        }
        return Optional.empty();
    }

    private Optional<Monster> findMonsterByIndex(List<Entity> monsters, int index, Set<Monster> restored) {
        if (index < 0 || index >= monsters.size()) {
            return Optional.empty();
        }

        Entity entity = monsters.get(index);
        if (entity instanceof Monster mon && !restored.contains(mon)) {
            return Optional.of(mon);
        }
        return Optional.empty();
    }

    private void restoreMonster(Monster mon, ObjectData saved) {
        if (saved == null) {
            return;
        }

        mon.restorePosition(saved.getWorldX(), saved.getWorldY());

        if (!saved.isActive()) {
            mon.kill();
        } else {
            mon.revive();
        }
    }
}
