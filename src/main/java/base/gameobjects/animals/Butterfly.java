package base.gameobjects.animals;

import base.gameobjects.Animal;

public class Butterfly extends Animal {

    public static final String TYPE = "butterfly";

    public Butterfly(int startX, int startY) {
        super(TYPE, startX, startY, 32);
    }

    @Override
    public int getLayer() {
        return 4;
    }
}
