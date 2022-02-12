package base.gameobjects.animals;

import base.gameobjects.Animal;

import static base.constants.MapConstants.MAIN_MAP;

public class Butterfly extends Animal {

    public static final String NAME = "butterfly";

    public Butterfly(int startX, int startY, int speed, int hungerLevel, int currentThirst, int currentEnergy) {
        super(NAME, startX, startY, speed, 32, hungerLevel, currentThirst, currentEnergy);
        setHomeMap(MAIN_MAP);
    }

    @Override
    public int getLayer() {
        return 4;
    }
}
