package base.gameObjects.animals;

import base.gameObjects.Animal;
import base.graphicsService.Rectangle;
import base.graphicsService.Sprite;

public class Rat extends Animal {

    public Rat(Sprite playerSprite, int startX, int startY) {
        super(playerSprite, startX, startY);
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
        System.out.println("Click on rat " + this);
        return false;
    }
}
