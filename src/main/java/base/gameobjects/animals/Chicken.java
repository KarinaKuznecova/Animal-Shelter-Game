package base.gameobjects.animals;

import base.gameobjects.Animal;
import base.graphicsservice.ImageLoader;

public class Chicken extends Animal {

    public static final String NAME = "chicken";
    public static final String ANIMATION_SHEET_PATH = "img/chicken.png";
    public static final String PREVIEW = null;

    public Chicken(int startX, int startY, int speed) {
        super(ImageLoader.getAnimatedSprite(ANIMATION_SHEET_PATH, 32), null, startX, startY, speed, 32);
    }
}
