package base.gameobjects.animals;

import base.gameobjects.Animal;
import base.graphicsservice.ImageLoader;

public class Butterfly extends Animal {

    public static final String NAME = "butterfly";

    public Butterfly(int startX, int startY, int speed) {
        super(NAME, startX, startY, speed, 32);
    }

    @Override
    public int getLayer() {
        return 4;
    }
}
