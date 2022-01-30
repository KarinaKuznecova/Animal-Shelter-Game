package base.gameobjects.animals;

import base.gameobjects.Animal;
import base.graphicsservice.ImageLoader;

import static base.constants.MapConstants.SECOND_MAP;

public class Chicken extends Animal {

    public static final String NAME = "chicken";

    public Chicken(int startX, int startY, int speed, int hungerLevel, int currentThirst) {
        super(NAME, startX, startY, speed, 32, hungerLevel, currentThirst);
        setHomeMap(SECOND_MAP);
    }
}
