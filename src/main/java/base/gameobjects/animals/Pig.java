package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;

public class Pig extends Animal {

    public static final String TYPE = "pig";

    public Pig(int startX, int startY, int speed, int hungerLevel, int currentThirst, int currentEnergy, AgeStage age, String name) {
        super(TYPE, startX, startY, speed, 64, hungerLevel, currentThirst, currentEnergy, age, name);
    }
}
