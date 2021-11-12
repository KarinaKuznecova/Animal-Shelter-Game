package base.gameobjects.animals;

import base.gameobjects.Animal;
import base.graphicsservice.ImageLoader;

public class Butterfly extends Animal {

    public static final String NAME = "butterfly";
    public static final String ANIMATION_SHEET_PATH = "img/butterfly.png";
    public static final String PREVIEW = null;

    public Butterfly(int startX, int startY, int speed) {
        super(ImageLoader.getAnimatedSprite(ANIMATION_SHEET_PATH, 32), null, startX, startY, speed, 32);
    }

    @Override
    public int getLayer() {
        return 4;
    }
}
