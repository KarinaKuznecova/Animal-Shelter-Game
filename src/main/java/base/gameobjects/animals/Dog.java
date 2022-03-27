package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;

public class Dog extends Animal {

    public static final String TYPE = "dog";

    public Dog(int startX, int startY, int speed, int hungerLevel, int currentThirst, int currentEnergy, AgeStage age, String name) {
        super(TYPE, startX, startY, speed, 48, hungerLevel, currentThirst, currentEnergy, age, name);
    }
}
