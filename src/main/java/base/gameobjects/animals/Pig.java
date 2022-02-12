package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;

import static base.constants.MapConstants.SECOND_MAP;

public class Pig extends Animal {

    public static final String NAME = "pig";

    public Pig(int startX, int startY, int speed, int hungerLevel, int currentThirst, int currentEnergy, AgeStage age) {
        super(NAME, startX, startY, speed, 64, hungerLevel, currentThirst, currentEnergy, age);
        setHomeMap(SECOND_MAP);
    }
}
