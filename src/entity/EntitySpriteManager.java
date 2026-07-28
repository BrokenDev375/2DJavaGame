/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import main.UtilityTool;

public class EntitySpriteManager {
    private final UtilityTool uTool = new UtilityTool();

    public EntitySpriteManager(){

    }
    public void updateSprite(Entity e){
        e.advanceSpriteFrame(8);
    }
    public BufferedImage setup(String imagePath, int width , int height){
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath +".png"));
            image = uTool.scaleImage(image, width , height);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
