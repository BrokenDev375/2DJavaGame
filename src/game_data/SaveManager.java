package game_data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import player_manager.Player;
import monster_data.Monster;
import entity_manager.EntityManager;
import main.GamePanel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    private static final String SAVE_DIR = "saves";
    private static final String SAVE_FILE = "savegame.json";
    private final Gson gson;

    public SaveManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();

        // tạo thư mục save nếu chưa tồn tại
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) dir.mkdir();
    }
    // Lưu game vào JSON
    public void saveGame(GamePanel gp) {
        try {
            // --- Player ---
            Player player = gp.getEntityManager().getPlayer();
            if (player == null) {
                System.err.println("[SaveManager] Player not found!");
                return;
            }
            PlayerData playerData = new PlayerData(
                    player.getWorldX(),
                    player.getWorldY(),
                    player.getHP(),
                    player.getMaxHP(),
                    player.equippedWeaponName(),
                    gp.getCurrentMap(),
                    player.getExp(),
                    player.getLevel()
            );
            // --- Monsters ---
            List<ObjectData> monsterList = new ArrayList<>();
            EntityManager em = gp.getEntityManager();

            if (em.getMonsters(gp.getCurrentMap()) != null) {
                for (var entity : em.getMonsters(gp.getCurrentMap())) {
                    if (entity instanceof Monster mon) {
                        monsterList.add(new ObjectData(
                                mon.getName(),
                                mon.getWorldX(),
                                mon.getWorldY(),
                                !mon.isDead() // active = còn sống
                        ));
                    }
                }
            }
            // --- Map ---
            int mapIndex = gp.getCurrentMap();
            String mapPath = gp.getChunkManager().getMapPath();

            GameData data = new GameData(playerData, monsterList, mapIndex, mapPath);
            // --- Write JSON ---
            String json = gson.toJson(data);
            Files.write(Paths.get(SAVE_DIR, SAVE_FILE), json.getBytes());

            System.out.println("[SaveManager] Game saved successfully.");

        } catch (IOException e) {
            System.err.println("[SaveManager] Error writing save file.");
            e.printStackTrace();
        }
    }
    // Load game từ JSON
    public void loadGame(GamePanel gp) {
        try {
            File saveFile = new File(SAVE_DIR, SAVE_FILE);
            if (!saveFile.exists()) {
                System.out.println("[SaveManager] No save file found.");
                return;
            }

            String json = new String(Files.readAllBytes(saveFile.toPath()));
            GameData data = gson.fromJson(json, GameData.class);

            if (data == null) {
                System.err.println("[SaveManager] Save file corrupted.");
                return;
            }
            // --- Restore Player ---
            Player player = gp.getEntityManager().getPlayer();
            PlayerData savedPlayer = data.getPlayer();
            if (player != null && savedPlayer != null) {
                // vị trí
                player.restorePosition(savedPlayer.getWorldX(), savedPlayer.getWorldY());

                player.setExp(savedPlayer.getExp());
                player.setLevel(savedPlayer.getLevel());

                // chỉ số + HP
                player.setStats(savedPlayer.getMaxHealth(), player.getATK(), player.getDEF());
                player.restoreHP(savedPlayer.getHealth());

                // --- vũ khí ---
                if (savedPlayer.getWeaponName() != null) {
                    object_data.weapons.Weapon w = null;

                    switch (savedPlayer.getWeaponName()) {
                        case "Leviathan Axe" ->
                                w = new object_data.weapons.Axe(gp, data.getMapIndex());
                        case "Argonaut hero's sword", "Argonaut Hero's Sword" ->
                                w = new object_data.weapons.Sword(gp, data.getMapIndex());
                        case "Steve's pick", "Steve Pick" ->
                                w = new object_data.weapons.Pick(gp, data.getMapIndex());
                    }

                    if (w != null) {
                        player.equipWeapon(w);
                    }
                }
                gp.setCurrentMap(savedPlayer.getMapIndex());
                player.setMapIndex(gp.getCurrentMap());

                String newMap = "map" + gp.getCurrentMap();
                gp.getChunkManager().loadMap(newMap);

                if (gp.getObjectManager() != null)
                    gp.getObjectManager().reloadMapObjects(gp.getCurrentMap());

                gp.getEntityManager().update(gp.getCurrentMap());
            }
            // --- Restore Monsters ---
            var monsters = gp.getEntityManager().getMonsters(gp.getCurrentMap());
            if (monsters != null && data.getObjects() != null) {
                for (int i = 0; i < Math.min(monsters.size(), data.getObjects().size()); i++) {
                    var entity = monsters.get(i);
                    var saved = data.getObjects().get(i);

                    if (entity instanceof Monster mon) {
                        mon.restorePosition(saved.getWorldX(), saved.getWorldY());

                        if (!saved.isActive())
                            mon.kill();
                        else
                            mon.revive();
                    }
                }
            }
            // Khởi tạo lại Interact
            if (gp.getEntityManager() != null && gp.getEntityManager().getPlayer() != null) {
                gp.resetInteractionRouter();
            }
            System.out.println("[SaveManager] Game loaded successfully.");
        } catch (IOException e) {
            System.err.println("[SaveManager] Error reading save file.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[SaveManager] Unexpected error while loading.");
            e.printStackTrace();
        }
    }
}
