package object_data;

import main.DebugLog;
import main.GamePanel;
import main.RenderContext;
import main.AssetLoadException;
import main.AssetLoader;
import world.WorldBody;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class WorldObject implements WorldBody {

    private int worldX, worldY;
    private int width, height;
    private BufferedImage staticImage;
    private String name;
    private WorldObjectType type;

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
        draw(g2, gp);
    }

    public void draw(Graphics2D g2, RenderContext render) {
        BufferedImage image = getRenderImage();
        var player = render.player();
        if (image != null && player != null) {
            int screenX = render.camera().screenX(this, player);
            int screenY = render.camera().screenY(this, player);
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

    protected void useStaticImage(BufferedImage image) {
        this.staticImage = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    void setType(WorldObjectType type) {
        this.type = type;
    }

    public java.util.Optional<WorldObjectType> type() {
        return java.util.Optional.ofNullable(type);
    }

    public boolean isType(WorldObjectType expectedType) {
        return expectedType != null && expectedType == type;
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
        try {
            return gp.getAssetLoader().requireScaledImage(path, w, h, getClass().getSimpleName());
        } catch (AssetLoadException e) {
            DebugLog.error(e.getMessage(), e);
            return AssetLoader.placeholderImage(w, h);
        }
    }

    protected BufferedImage getRenderImage() {
        return staticImage;
    }
}
