package base.gameobjects.animals;

import base.gameobjects.Animal;
import base.graphicsservice.Rectangle;
import base.graphicsservice.Sprite;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rat extends Animal {

    private static final Logger logger = LoggerFactory.getLogger(Rat.class);

    public Rat(Sprite playerSprite, int startX, int startY, int speed) {
        super(playerSprite, startX, startY, speed);
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
        logger.info("Click on Animal: rat");
        return false;
    }
}
