package tile;

import main.DebugLog;
import main.GamePanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkManager {
    private final int chunkSize;
    private final Map<String, Chunk> chunks;
    private final GamePanel gp;
    private final ExecutorService loader = Executors.newSingleThreadExecutor();
    private String pathMap = "map0";

    public ChunkManager(int chunkSize, GamePanel gp) {
        this.chunkSize = chunkSize;
        this.chunks = new HashMap<>();
        this.gp = gp;
    }

    private String chunkKey(int x, int y) {
        return x + "_" + y;
    }

    private Optional<Chunk> loadChunkFromFile(int chunkX, int chunkY, String pathMap) {
        Chunk c = new Chunk(chunkX, chunkY, chunkSize);
        String path = "/" + pathMap + "/chunk" + chunkX + "_" + chunkY + ".tmx";
        Optional<InputStream> resource = gp.getAssetLoader().findStream(path, "ChunkManager");
        if (resource.isEmpty()) {
            return Optional.empty();
        }

        try (InputStream is = resource.get();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder xml = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                xml.append(line.trim());
            }

            String content = xml.toString();
            String dataStart = "<data encoding=\"csv\">";
            int markerStart = content.indexOf(dataStart);
            int end = content.indexOf("</data>");
            if (markerStart < 0 || end < 0 || end <= markerStart) {
                throw new IllegalArgumentException("Missing CSV tile data in chunk: " + path);
            }

            int start = markerStart + dataStart.length();
            String csv = content.substring(start, end).trim();
            String[] numbers = csv.split(",");

            int idx = 0;
            for (int row = 0; row < chunkSize; row++) {
                for (int col = 0; col < chunkSize; col++) {
                    if (idx >= numbers.length) {
                        throw new IllegalArgumentException("Not enough tile values in chunk: " + path);
                    }
                    int num = Integer.parseInt(numbers[idx].trim());
                    c.setTileNum(row, col, (num == 0) ? 0 : num - 1);
                    idx++;
                }
            }
            return Optional.of(c);
        } catch (IOException | IllegalArgumentException | IndexOutOfBoundsException e) {
            DebugLog.error("[ChunkManager] Failed to load chunk: " + path, e);
            return Optional.empty();
        }
    }

    public void loadChunkAsync(int chunkX, int chunkY, String pathMap) {
        String key = chunkKey(chunkX, chunkY);
        synchronized (chunks) {
            if (chunks.containsKey(key)) return;
        }

        loader.submit(() -> {
            Optional<Chunk> chunk = loadChunkFromFile(chunkX, chunkY, pathMap);
            if (chunk.isPresent()) {
                synchronized (chunks) {
                    chunks.put(key, chunk.get());
                }
            }
        });
    }

    private void unloadFarChunks(int left, int right, int top, int bottom) {
        synchronized (chunks) {
            chunks.entrySet().removeIf(e -> {
                int cx = e.getValue().getChunkX();
                int cy = e.getValue().getChunkY();
                return cx < left - 1 || cx > right + 1 || cy < top - 1 || cy > bottom + 1;
            });
        }
    }

    public void updateChunks(int playerWorldX, int playerWorldY) {
        int buffer = gp.tileSize * (chunkSize / 2);

        var player = gp.getEntityManager().getPlayer();
        int screenLeft = gp.getCamera().visibleLeft(player, buffer);
        int screenRight = gp.getCamera().visibleRight(player, buffer);
        int screenTop = gp.getCamera().visibleTop(player, buffer);
        int screenBottom = gp.getCamera().visibleBottom(player, buffer);

        int chunkLeft = screenLeft / (chunkSize * gp.tileSize);
        int chunkRight = screenRight / (chunkSize * gp.tileSize);
        int chunkTop = screenTop / (chunkSize * gp.tileSize);
        int chunkBottom = screenBottom / (chunkSize * gp.tileSize);

        for (int cx = chunkLeft; cx <= chunkRight; cx++) {
            for (int cy = chunkTop; cy <= chunkBottom; cy++) {
                if (cx < 0 || cy < 0 || cx >= gp.chunkSize || cy >= gp.chunkSize) {
                    continue;
                }
                loadChunkAsync(cx, cy, pathMap);
            }
        }
        unloadFarChunks(chunkLeft, chunkRight, chunkTop, chunkBottom);
    }

    public void clearChunks() {
        synchronized (chunks) {
            chunks.clear();
        }
    }

    public Iterable<Chunk> getActiveChunks() {
        synchronized (chunks) {
            return new HashMap<>(chunks).values();
        }
    }

    public void loadMap(String mapName) {
        clearChunks();
        this.pathMap = mapName;
    }

    public String getMapPath() {
        return pathMap;
    }

    public void shutdown() {
        loader.shutdownNow();
    }

    public void loadAllChunksSync() {
        synchronized (chunks) {
            chunks.clear();
        }

        for (int cx = 0; cx < gp.chunkSize; cx++) {
            for (int cy = 0; cy < gp.chunkSize; cy++) {
                if (cx < 0 || cy < 0 || cx >= gp.chunkSize || cy >= gp.chunkSize) continue;

                Optional<Chunk> chunk = loadChunkFromFile(cx, cy, pathMap);
                if (chunk.isPresent()) {
                    synchronized (chunks) {
                        chunks.put(chunkKey(cx, cy), chunk.get());
                    }
                }
            }
        }
    }

    private Chunk getChunk(int chunkX, int chunkY) {
        String key = chunkKey(chunkX, chunkY);
        synchronized (chunks) {
            return chunks.get(key);
        }
    }

    public int getTileNum(int tileCol, int tileRow) {
        if (tileCol < 0 || tileRow < 0
                || tileCol >= gp.maxWorldCol
                || tileRow >= gp.maxWorldRow) {
            return 0;
        }

        int chunkX = tileCol / chunkSize;
        int chunkY = tileRow / chunkSize;
        int inChunkCol = tileCol % chunkSize;
        int inChunkRow = tileRow % chunkSize;

        Chunk c = getChunk(chunkX, chunkY);
        if (c == null) {
            return 0;
        }

        return c.getTileNum(inChunkRow, inChunkCol);
    }

    public int getTileNumAtWorld(int worldX, int worldY) {
        int tileCol = worldX / gp.tileSize;
        int tileRow = worldY / gp.tileSize;
        return getTileNum(tileCol, tileRow);
    }
}
