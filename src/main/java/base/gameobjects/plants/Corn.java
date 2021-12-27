package base.gameobjects.plants;

import base.gameobjects.AnimatedSprite;
import base.gameobjects.Plant;
import base.graphicsservice.Sprite;

public class Corn extends Plant {

    public static final String NAME = "corn";

    public Corn(Sprite previewSprite, AnimatedSprite animatedSprite, int x, int y, String plantType) {
        super(previewSprite, animatedSprite, x, y, plantType);
        setGrowingTime(6500);
    }
}
