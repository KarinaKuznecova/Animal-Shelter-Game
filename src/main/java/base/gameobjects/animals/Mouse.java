package base.gameobjects.animals;

import base.gameobjects.Animal;
import base.graphicsservice.ImageLoader;

public class Mouse extends Animal {

    public static final String NAME = "mouse";
    public static final String ANIMATION_SHEET_PATH = "img/mouse.png";
    public static final String PREVIEW = null;

    public Mouse(int startX, int startY, int speed) {
        super(ImageLoader.getAnimatedSprite(ANIMATION_SHEET_PATH, 32), null, startX, startY, speed, 32);
    }
}
