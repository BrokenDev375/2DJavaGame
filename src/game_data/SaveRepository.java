package game_data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class SaveRepository {
    private static final String DEFAULT_SAVE_DIR = "saves";
    private static final String DEFAULT_SAVE_FILE = "savegame.json";

    private final Gson gson;
    private final Path saveFile;

    public SaveRepository() {
        this(Paths.get(DEFAULT_SAVE_DIR, DEFAULT_SAVE_FILE));
    }

    public SaveRepository(Path saveFile) {
        this(saveFile, new GsonBuilder().setPrettyPrinting().create());
    }

    SaveRepository(Path saveFile, Gson gson) {
        this.saveFile = Objects.requireNonNull(saveFile);
        this.gson = Objects.requireNonNull(gson);
    }

    public Path saveFile() {
        return saveFile;
    }

    public boolean exists() {
        return Files.exists(saveFile);
    }

    public void save(GameData data) throws IOException {
        Objects.requireNonNull(data);
        Path parent = saveFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(saveFile, gson.toJson(data), StandardCharsets.UTF_8);
    }

    public GameData load() throws IOException {
        String json = Files.readString(saveFile, StandardCharsets.UTF_8);
        return gson.fromJson(json, GameData.class);
    }
}
