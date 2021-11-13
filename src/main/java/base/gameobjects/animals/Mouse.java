package base.gameobjects.animals;

import base.gameobjects.Animal;
import base.graphicsservice.ImageLoader;

public class Mouse extends Animal {

    public static final String NAME = "mouse";

    public Mouse(int startX, int startY, int speed) {
        super(NAME, startX, startY, speed, 32);
    }
}
