package tile;

import main.AssetLoadException;
import main.AssetLoader;
import main.DebugLog;
import main.GamePanel;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class TileManager {
    private final GamePanel gp;
    private final AssetLoader assetLoader;
    private Tile[] tile;

    public TileManager(GamePanel gp) {
        this(gp, AssetLoader.defaultLoader());
    }

    public TileManager(GamePanel gp, AssetLoader assetLoader) {
        this.gp = Objects.requireNonNull(gp, "gp");
        this.assetLoader = Objects.requireNonNull(assetLoader, "assetLoader");
        loadTileset("/maptiles/tileset", gp.originalTileSize());
        loadTilesetProperties("/maptiles/tileset.tsx");
    }

    public void loadTileset(String path, int tileSize) {
        if (tileSize <= 0) {
            DebugLog.error("[TileManager] Invalid tile size for tileset " + path + ": " + tileSize, null);
            usePlaceholderTile(1);
            return;
        }

        try {
            Optional<BufferedImage> image = assetLoader.findImage(path, "TileManager");
            if (image.isEmpty()) {
                DebugLog.error("[TileManager] Tileset not found: " + AssetLoader.normalizeImagePath(path), null);
                usePlaceholderTile(tileSize);
                return;
            }

            BufferedImage tileset = image.get();
            int cols = tileset.getWidth() / tileSize;
            int rows = tileset.getHeight() / tileSize;
            if (cols <= 0 || rows <= 0) {
                DebugLog.error("[TileManager] Tileset too small for tile size "
                        + tileSize + ": " + AssetLoader.normalizeImagePath(path), null);
                usePlaceholderTile(tileSize);
                return;
            }

            tile = new Tile[cols * rows];

            int index = 0;
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    tile[index] = new Tile();
                    tile[index].setImage(tileset.getSubimage(
                            x * tileSize, y * tileSize, tileSize, tileSize
                    ));
                    tile[index].setCollidable(false);
                    index++;
                }
            }
        } catch (AssetLoadException | IllegalArgumentException e) {
            DebugLog.error("[TileManager] Failed to load tileset: " + path, e);
            usePlaceholderTile(tileSize);
        }
    }

    public void loadTilesetProperties(String tsxPath) {
        try (InputStream is = assetLoader.requireStream(tsxPath, "TileManager");
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            int tileIndex = -1;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("<tile id=")) {
                    int start = line.indexOf("\"") + 1;
                    int end = line.indexOf("\"", start);
                    tileIndex = Integer.parseInt(line.substring(start, end));
                }
                if (line.contains("<property name=\"collision\"")) {
                    if (tileIndex >= 0 && tileIndex < tile.length) {
                        tile[tileIndex].setCollidable(line.contains("value=\"true\""));
                    }
                }
            }
        } catch (AssetLoadException e) {
            DebugLog.error(e.getMessage(), e);
        } catch (IOException | IllegalArgumentException | IndexOutOfBoundsException e) {
            DebugLog.error("[TileManager] Failed to load tileset properties: " + tsxPath, e);
        }
    }

    public void draw(Graphics2D g2, ChunkManager chunkM) {
        var player = gp.getEntityManager().getPlayer();
        int screenLeft = gp.getCamera().visibleLeft(player, 0);
        int screenTop = gp.getCamera().visibleTop(player, 0);
        int screenRight = gp.getCamera().visibleRight(player, 5 * gp.tileSize());
        int screenBottom = gp.getCamera().visibleBottom(player, 5 * gp.tileSize());

        for (Chunk c : chunkM.getActiveChunks()) {
            int chunkWorldX = c.getChunkX() * c.getSize() * gp.tileSize();
            int chunkWorldY = c.getChunkY() * c.getSize() * gp.tileSize();

            if (chunkWorldX + c.getSize() * gp.tileSize() < screenLeft) continue;
            if (chunkWorldX > screenRight) continue;
            if (chunkWorldY + c.getSize() * gp.tileSize() < screenTop) continue;
            if (chunkWorldY > screenBottom) continue;

            for (int row = 0; row < c.getSize(); row++) {
                for (int col = 0; col < c.getSize(); col++) {
                    int tileNum = c.getTileNum(row, col);
                    if (tileNum < 0 || tileNum >= tile.length) continue;

                    int tileWorldX = chunkWorldX + col * gp.tileSize();
                    int tileWorldY = chunkWorldY + row * gp.tileSize();
                    int tileScreenX = gp.getCamera().screenX(tileWorldX, player);
                    int tileScreenY = gp.getCamera().screenY(tileWorldY, player);

                    g2.drawImage(tile[tileNum].getImage(), tileScreenX, tileScreenY, gp.tileSize(), gp.tileSize(), null);
                }
            }
        }
    }

    public boolean isCollisionAtWorld(int worldX, int worldY, ChunkManager chunkM) {
        int tileNum = chunkM.getTileNumAtWorld(worldX, worldY);
        return isTileCollidable(tileNum);
    }

    public boolean isTileCollidable(int tileNum) {
        return tile != null && tileNum >= 0 && tileNum < tile.length && tile[tileNum].isCollidable();
    }

    private void usePlaceholderTile(int tileSize) {
        Tile placeholder = new Tile();
        placeholder.setImage(AssetLoader.placeholderImage(tileSize, tileSize));
        placeholder.setCollidable(false);
        tile = new Tile[] { placeholder };
    }
}
