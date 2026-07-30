package game_data;

import com.google.gson.JsonParseException;
import main.DebugLog;
import main.GamePanel;

import java.io.IOException;
import java.util.Optional;

public class SaveManager {
    private final SaveRepository repository;
    private final GameSnapshotMapper snapshotMapper;
    private final GameRestorer restorer;

    public SaveManager() {
        this(new SaveRepository(), new GameSnapshotMapper(), new GameRestorer());
    }

    public SaveManager(SaveRepository repository) {
        this(repository, new GameSnapshotMapper(), new GameRestorer());
    }

    SaveManager(SaveRepository repository, GameSnapshotMapper snapshotMapper, GameRestorer restorer) {
        this.repository = repository;
        this.snapshotMapper = snapshotMapper;
        this.restorer = restorer;
    }

    public boolean saveGame(GamePanel gp) {
        Optional<GameData> snapshot = snapshotMapper.capture(gp);
        if (snapshot.isEmpty()) {
            DebugLog.error("[SaveManager] Cannot save because player was not found.", null);
            return false;
        }

        try {
            repository.save(snapshot.get());
            DebugLog.info("[SaveManager] Game saved successfully: " + repository.saveFile());
            return true;
        } catch (IOException e) {
            DebugLog.error("[SaveManager] Error writing save file: " + repository.saveFile(), e);
            return false;
        }
    }

    public LoadResult loadGame(GamePanel gp) {
        if (!repository.exists()) {
            DebugLog.info("[SaveManager] No save file found: " + repository.saveFile());
            return LoadResult.MISSING;
        }

        try {
            GameData data = repository.load();

            if (data == null) {
                DebugLog.error("[SaveManager] Save file corrupted: " + repository.saveFile(), null);
                return LoadResult.CORRUPTED;
            }

            LoadResult result = restorer.restore(gp, data);
            if (result == LoadResult.LOADED) {
                DebugLog.info("[SaveManager] Game loaded successfully: " + repository.saveFile());
            } else {
                DebugLog.error("[SaveManager] Save file could not be restored: "
                        + repository.saveFile() + " result=" + result, null);
            }
            return result;
        } catch (JsonParseException e) {
            DebugLog.error("[SaveManager] Save file corrupted: " + repository.saveFile(), e);
            return LoadResult.CORRUPTED;
        } catch (IOException e) {
            DebugLog.error("[SaveManager] Error reading save file: " + repository.saveFile(), e);
            return LoadResult.FAILED;
        } catch (RuntimeException e) {
            DebugLog.error("[SaveManager] Unexpected error while loading: " + repository.saveFile(), e);
            return LoadResult.FAILED;
        }
    }
}
