package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;

public class Mouse extends Animal {

    public static final String NAME = "mouse";

    public Mouse(int startX, int startY, int speed, int hungerLevel, int currentThirst, int currentEnergy, AgeStage age) {
        super(NAME, startX, startY, speed, 32, hungerLevel, currentThirst, currentEnergy, age);
    }
}
