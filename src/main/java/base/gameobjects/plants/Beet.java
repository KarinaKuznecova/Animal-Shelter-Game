package base.gameobjects.plants;

import base.gameobjects.AnimatedSprite;
import base.gameobjects.Plant;
import base.graphicsservice.Sprite;

public class Beet  extends Plant {

    public static final String NAME = "beet";

    public Beet(Sprite previewSprite, AnimatedSprite animatedSprite, int x, int y, String plantType) {
        super(previewSprite, animatedSprite, x, y, plantType);
        setGrowingTime(5500);
    }
}
