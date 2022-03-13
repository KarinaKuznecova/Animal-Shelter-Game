package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;

public class Mouse extends Animal {

    public static final String TYPE = "mouse";

    public Mouse(int startX, int startY, int speed, int hungerLevel, int currentThirst, int currentEnergy, AgeStage age, String name) {
        super(TYPE, startX, startY, speed, 32, hungerLevel, currentThirst, currentEnergy, age, name);
    }
}
