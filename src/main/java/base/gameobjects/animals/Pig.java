package base.gameobjects.animals;

import base.gameobjects.Animal;

public class Pig extends Animal {

    public static final String NAME = "pig";

    public Pig(int startX, int startY, int speed) {
        super(NAME, startX, startY, speed, 64);
    }
}
