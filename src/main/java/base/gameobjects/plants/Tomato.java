package base.gameobjects.plants;

import base.gameobjects.AnimatedSprite;
import base.gameobjects.Plant;
import base.graphicsservice.Sprite;

public class Tomato  extends Plant {

    public static final String NAME = "tomato";

    public Tomato(Sprite previewSprite, AnimatedSprite animatedSprite, int x, int y, String plantType) {
        super(previewSprite, animatedSprite, x, y, plantType);
        setGrowingTime(3900);
        setRefreshable(true);
    }
}
