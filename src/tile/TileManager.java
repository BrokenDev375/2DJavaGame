package tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

import main.GamePanel;

public class TileManager {
    // Reference to main game panel
    GamePanel gp;
    // Array of all tiles loaded from tileset
    private Tile[] tile;

    public TileManager(GamePanel gp){
        this.gp = gp;
        tile = null; // initialize tile array
        loadTileset("/maptiles/tileset.png", gp.originalTileSize);
        loadTilesetProperties("/maptiles/tileset.tsx");
    }
    public void loadTileset(String path, int tileSize) {
        try {
            BufferedImage tileset = ImageIO.read(getClass().getResourceAsStream(path));
            int cols = tileset.getWidth() / tileSize;
            int rows = tileset.getHeight() / tileSize;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadTilesetProperties(String tsxPath) {
        try {
            InputStream is = getClass().getResourceAsStream(tsxPath);
            if(is == null){
                System.out.println("TSX file not found!");
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

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
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2 ,ChunkManager chunkM){
        var player = gp.getEntityManager().getPlayer();
        int screenLeft   = gp.getCamera().visibleLeft(player, 0);
        int screenTop    = gp.getCamera().visibleTop(player, 0);
        int screenRight  = gp.getCamera().visibleRight(player, 5 * gp.tileSize);
        int screenBottom = gp.getCamera().visibleBottom(player, 5 * gp.tileSize);

        for(Chunk c : chunkM.getActiveChunks()){
            int chunkWorldX = c.getChunkX() * c.getSize() * gp.tileSize;
            int chunkWorldY = c.getChunkY() * c.getSize() * gp.tileSize;

            // Skip chunks outside the screen
            if(chunkWorldX + c.getSize()*gp.tileSize < screenLeft) continue;
            if(chunkWorldX > screenRight) continue;
            if(chunkWorldY + c.getSize()*gp.tileSize < screenTop) continue;
            if(chunkWorldY > screenBottom) continue;

            // Draw each tile in the chunk
            for(int row=0; row<c.getSize(); row++){
                for(int col=0; col<c.getSize(); col++){
                    int tileNum = c.getTileNum(row, col); // take tile num from chunk
                    if (tileNum < 0 || tileNum >= tile.length) continue;

                    int tileWorldX = chunkWorldX + col*gp.tileSize;
                    int tileWorldY = chunkWorldY + row*gp.tileSize;
                    int tileScreenX = gp.getCamera().screenX(tileWorldX, player);
                    int tileScreenY = gp.getCamera().screenY(tileWorldY, player);

                    g2.drawImage(tile[tileNum].getImage(), tileScreenX, tileScreenY, gp.tileSize, gp.tileSize, null);
                }
            }

        }
    }
    public boolean isCollisionAtWorld(int worldX, int worldY, ChunkManager chunkM) {
        int tileNum = chunkM.getTileNumAtWorld(worldX, worldY);

        // Bảo vệ out-of-range
        if (tileNum < 0 || tileNum >= tile.length) {
            return false;
        }

        return tile[tileNum].isCollidable();  // dùng cờ collision của Tile[]
    }

    public boolean isTileCollidable(int tileNum) {
        return tileNum >= 0 && tileNum < tile.length && tile[tileNum].isCollidable();
    }

}
