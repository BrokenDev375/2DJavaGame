package tile;
import java.awt.image.BufferedImage;

public class Tile {
    private BufferedImage image;
    // Image used to render this tile
    
    private boolean collision = false;
    // Whether the player or objects can collide with this tile

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public boolean isCollidable() {
        return collision;
    }

    public void setCollidable(boolean collision) {
        this.collision = collision;
    }
}
