package tile;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.GamePanel;
public class ChunkManager {
    // Size of each chunk (number of tiles per side)
    private final int chunkSize;
    // Map storing loaded chunks using "chunkX_chunkY" as key
    private final Map<String, Chunk> chunks;
    // Reference to the main game panel
    private final GamePanel gp;
    // Single-thread executor for background chunk loading
    private ExecutorService loader = Executors.newSingleThreadExecutor(); // private thread for load chunk
    // Current map path
    private String pathMap = "map0";

    public ChunkManager(int chunkSize , GamePanel gp){
        this.chunkSize = chunkSize;
        this.chunks = new HashMap<>();
        this.gp = gp;
    }
    // Generate a unique key for chunk coordinates
    private String chunkKey(int x, int y){
        return x + "_" + y;
    }
    private Chunk loadChunkFromFile(int chunkX, int chunkY, String pathMap){
        Chunk c = new Chunk(chunkX, chunkY, chunkSize);
        try {
            String path = "/" + pathMap + "/chunk" + chunkX + "_" + chunkY + ".tmx";
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                // System.out.println("Không tìm thấy file: " + path);
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder xml = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) xml.append(line.trim());
            br.close();

            String content = xml.toString();
            int start = content.indexOf("<data encoding=\"csv\">") + "<data encoding=\"csv\">".length();
            int end = content.indexOf("</data>");
            String csv = content.substring(start, end).trim();
            String[] numbers = csv.split(",");

            int idx = 0;
            for(int row=0; row<chunkSize; row++){
                for(int col=0; col<chunkSize; col++){
                    int num = Integer.parseInt(numbers[idx].trim());
                    c.setTileNum(row, col, (num == 0) ? 0 : num - 1);
                    idx++;
                }
            }
            return c;

        } catch(Exception e){ 
            e.printStackTrace(); 
            return null;
        }
    }
    public void loadChunkAsync(int chunkX, int chunkY , String pathMap){
        String key = chunkKey(chunkX, chunkY);
        synchronized (chunks) {
            if(chunks.containsKey(key)) return; // đã có thì bỏ qua
        }

        loader.submit(() -> {
            Chunk c = loadChunkFromFile(chunkX, chunkY, pathMap);
            if(c != null){
                synchronized (chunks) {
                    chunks.put(key, c);
                }
            }
        });
    }

    private void unloadFarChunks(int left, int right, int top, int bottom){
        synchronized (chunks) {
            chunks.entrySet().removeIf(e -> {
                int cx = e.getValue().getChunkX();
                int cy = e.getValue().getChunkY();
                return cx < left - 1 || cx > right + 1 || cy < top - 1 || cy > bottom + 1;
            });
        }
    }

    public void updateChunks(int playerWorldX, int playerWorldY){
        int buffer = gp.tileSize * (chunkSize / 2);

        var player = gp.getEntityManager().getPlayer();
        int screenLeft   = gp.getCamera().visibleLeft(player, buffer);
        int screenRight  = gp.getCamera().visibleRight(player, buffer);
        int screenTop    = gp.getCamera().visibleTop(player, buffer);
        int screenBottom = gp.getCamera().visibleBottom(player, buffer);

        int chunkLeft   = screenLeft / (chunkSize * gp.tileSize);
        int chunkRight  = screenRight / (chunkSize * gp.tileSize);
        int chunkTop    = screenTop / (chunkSize * gp.tileSize);
        int chunkBottom = screenBottom / (chunkSize * gp.tileSize);

        for(int cx = chunkLeft; cx <= chunkRight; cx++){
            for(int cy = chunkTop; cy <= chunkBottom; cy++){
                if (cx < 0 || cy < 0 || cx >= gp.chunkSize || cy >= gp.chunkSize) {
                    continue;
                }
                loadChunkAsync(cx, cy , pathMap);
            }
        }
        unloadFarChunks(chunkLeft, chunkRight, chunkTop, chunkBottom);
    }

    public void clearChunks(){
        synchronized (chunks) {
            chunks.clear();
        }
    }

    public Iterable<Chunk> getActiveChunks(){
        synchronized (chunks) {
            return new HashMap<>(chunks).values(); 
        }
    }

    public void loadMap(String mapName) {
        clearChunks();                // xoá toàn bộ chunk cũ
        this.pathMap = mapName;       // cập nhật đường dẫn map mới
    }

    public String getMapPath() {
        return pathMap;
    }
    public void shutdown(){
        loader.shutdownNow();
    }
    public void loadAllChunksSync() {
        synchronized (chunks) {
            chunks.clear();
        }

        for (int cx = 0; cx < gp.chunkSize; cx++) {
            for (int cy = 0; cy < gp.chunkSize; cy++) {
                // Giới hạn biên map nếu cần
                if (cx < 0 || cy < 0 || cx >= gp.chunkSize || cy >= gp.chunkSize) continue;

                Chunk c = loadChunkFromFile(cx, cy, pathMap);
                if (c != null) {
                    synchronized (chunks) {
                        chunks.put(chunkKey(cx, cy), c);
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
    // Lấy tile num theo toạ độ tile (col, row) trong world
    public int getTileNum(int tileCol, int tileRow) {
        // Ngoài biên map thì cho 0 hoặc -1
        if (tileCol < 0 || tileRow < 0
                || tileCol >= gp.maxWorldCol
                || tileRow >= gp.maxWorldRow) {
            return 0;
        }

        int worldTilesPerChunk = chunkSize; // 32
        int chunkX = tileCol / worldTilesPerChunk;
        int chunkY = tileRow / worldTilesPerChunk;

        int inChunkCol = tileCol % worldTilesPerChunk;
        int inChunkRow = tileRow % worldTilesPerChunk;

        Chunk c = getChunk(chunkX, chunkY);
        if (c == null) {
            // Chunk chưa load → coi như tile rỗng, hoặc 0
            return 0;
        }

        return c.getTileNum(inChunkRow, inChunkCol);
    }
    // Lấy tile num theo toạ độ world (pixel)
    public int getTileNumAtWorld(int worldX, int worldY) {
        int tileCol = worldX / gp.tileSize;
        int tileRow = worldY / gp.tileSize;
        return getTileNum(tileCol, tileRow);
    }

}
