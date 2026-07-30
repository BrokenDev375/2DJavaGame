package main;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class UtilityTool {
    public BufferedImage scaleImage(BufferedImage original , int width, int height){
        Objects.requireNonNull(original, "original");
        int safeWidth = Math.max(1, width);
        int safeHeight = Math.max(1, height);
        int imageType = original.getType() == BufferedImage.TYPE_CUSTOM
                ? BufferedImage.TYPE_INT_ARGB
                : original.getType();
        BufferedImage scaledImage = new BufferedImage(safeWidth, safeHeight, imageType);
        Graphics2D g2 = scaledImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(original, 0, 0, safeWidth, safeHeight, null);
        g2.dispose();
        
        return scaledImage;
    }
    public int mapNameToIndex(String mapName){
        switch(mapName){
        case "map0": return 0;
        case "map1": return 1;
        case "map2": return 2;
        case "map3": return 3;// shop 
        default: return 0;
        }
    }
}
