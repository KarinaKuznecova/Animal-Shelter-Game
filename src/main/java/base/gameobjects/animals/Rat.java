package base.gameobjects.animals;

import base.gameobjects.Animal;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rat extends Animal {

    public static final String NAME = "rat";
    public static final String ANIMATION_SHEET_PATH = "img/rat.png";
    public static final String PREVIEW = null;

    private static final Logger logger = LoggerFactory.getLogger(Rat.class);

    public Rat(int startX, int startY, int speed) {
        super(ImageLoader.getAnimatedSprite(ANIMATION_SHEET_PATH, 32), null, startX, startY, speed, 32);
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
        logger.info("Click on Animal: rat");
        return false;
    }
}
