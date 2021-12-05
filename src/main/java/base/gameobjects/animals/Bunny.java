package base.gameobjects.animals;

import base.gameobjects.Animal;

public class Bunny extends Animal {

    public static final String NAME = "bunny";

    public Bunny(int startX, int startY, int speed, int hungerLevel) {
        super(NAME, startX, startY, speed, 32, hungerLevel);
    }
}
