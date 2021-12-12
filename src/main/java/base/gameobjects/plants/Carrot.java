package base.gameobjects.plants;

import base.gameobjects.AnimatedSprite;
import base.gameobjects.Plant;
import base.graphicsservice.Sprite;

public class Carrot  extends Plant {

    public static final String NAME = "carrot";

    public Carrot(Sprite previewSprite, AnimatedSprite animatedSprite, int x, int y, String plantType) {
        super(previewSprite, animatedSprite, x, y, plantType);
        setGrowingTime(4300);
    }
}
