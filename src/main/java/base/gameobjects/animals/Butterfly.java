package base.gameobjects.animals;

import base.gameobjects.Animal;

import static base.constants.MapConstants.MAIN_MAP;

public class Butterfly extends Animal {

    public static final String TYPE = "butterfly";

    public Butterfly(int startX, int startY) {
        super(TYPE, startX, startY, 32);
        setHomeMap(MAIN_MAP);
    }

    @Override
    public int getLayer() {
        return 4;
    }
}
