package base.gameobjects.animals;

import base.Game;
import base.gameobjects.Animal;
import base.graphicsservice.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rat extends Animal {

    public static final String NAME = "rat";

    private static final Logger logger = LoggerFactory.getLogger(Rat.class);

    public Rat(int startX, int startY, int speed) {
        super(NAME, startX, startY, speed, 32);
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom, Game game) {
        logger.info("Click on Animal: rat");
        return false;
    }
}
