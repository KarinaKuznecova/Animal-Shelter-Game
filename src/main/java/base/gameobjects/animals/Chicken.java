package base.gameobjects.animals;

import base.gameobjects.Animal;
import base.graphicsservice.ImageLoader;

public class Chicken extends Animal {

    public static final String NAME = "chicken";

    public Chicken(int startX, int startY, int speed) {
        super(NAME, startX, startY, speed, 32);
    }
}
