package base.gameobjects.animals;

import base.gameobjects.Animal;
import base.graphicsservice.ImageLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pig extends Animal {

    private static final Logger logger = LoggerFactory.getLogger(Pig.class);

    public static final String NAME = "pig";
    public static final String ANIMATION_SHEET_PATH = "img/pig.png";
    public static final String PREVIEW = "img/pig-preview.png";

    public Pig(int startX, int startY, int speed) {
        super(ImageLoader.getAnimatedSprite(ANIMATION_SHEET_PATH, 64), ImageLoader.getPreviewSprite(PREVIEW), startX, startY, speed, 64);
    }
}
