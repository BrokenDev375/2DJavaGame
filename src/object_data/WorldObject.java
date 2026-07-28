package object_data;

import main.GamePanel;
import world.WorldBody;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class WorldObject implements WorldBody {

    private int worldX, worldY;
    private int width, height;
    private BufferedImage staticImage;
    private String name;

    private Rectangle solidArea;
    private boolean collidable = false;

    private int mapIndex = 0;
    private int value = 0;

    protected final GamePanel gp;

    public WorldObject(GamePanel gp) {
        this.gp = gp;
    }

    public void update() {}

    public void draw(Graphics2D g2) {
        BufferedImage image = getRenderImage();
        if (image != null && gp.getEntityManager() != null && gp.getEntityManager().getPlayer() != null) {
            int screenX = gp.getCamera().screenX(this, gp.getEntityManager().getPlayer());
            int screenY = gp.getCamera().screenY(this, gp.getEntityManager().getPlayer());
            g2.drawImage(image, screenX, screenY, null);
        }
    }

    public void moveTo(int x, int y) {
        this.worldX = x;
        this.worldY = y;
    }

    public int getWorldX() {
        return worldX;
    }

    public int getWorldY() {
        return worldY;
    }

    public void setSize(int width, int height) {
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setStaticImage(BufferedImage image) {
        this.staticImage = image;
    }

    public BufferedImage getStaticImage() {
        return staticImage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isNamed(String expectedName) {
        return expectedName != null && expectedName.equals(name);
    }

    public void setSolidArea(Rectangle area) {
        solidArea = area == null ? null : new Rectangle(area);
    }

    public Rectangle getSolidArea() {
        if (solidArea == null) {
            return new Rectangle(0, 0, Math.max(1, width), Math.max(1, height));
        }
        return new Rectangle(solidArea);
    }

    public Rectangle getSolidAreaWorld() {
        Rectangle area = getSolidArea();
        return new Rectangle(
                worldX + area.x,
                worldY + area.y,
                area.width,
                area.height
        );
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setMapIndex(int mapIndex) {
        this.mapIndex = mapIndex;
    }

    public int getMapIndex() {
        return mapIndex;
    }

    public boolean isOnMap(int mapIndex) {
        return this.mapIndex == mapIndex;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    protected BufferedImage setup(String path, int w, int h) {
        try (InputStream is = getClass().getResourceAsStream(path + ".png")) {
            if (is == null) return null;
            BufferedImage src = ImageIO.read(is);
            BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = dst.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(src, 0, 0, w, h, null);
            g.dispose();
            return dst;
        } catch (Exception e) {
            System.err.println("[WorldObject.setup] Load fail: " + path + " -> " + e.getMessage());
            return null;
        }
    }

    public BufferedImage getRenderImage() {
        return staticImage;
    }
}
