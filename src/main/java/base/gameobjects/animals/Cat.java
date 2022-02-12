package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;

import static base.constants.Constants.CAT_BLACK;
import static base.gameobjects.AgeStage.ADULT;

public class Cat extends Animal {

    public static final String NAME = "cat";

    public Cat(int startX, int startY, int speed, String color) {
        super(color != null ? NAME + "-" + color : CAT_BLACK, startX, startY, speed, 32, MAX_HUNGER, MAX_THIRST, MAX_ENERGY, ADULT);
        setColor(color);
    }

    public Cat(int startX, int startY, int speed, String color, int hungerLevel, int currentThirst, int currentEnergy, AgeStage age) {
        super(color != null ? NAME + "-" + color : CAT_BLACK, startX, startY, speed, 32, hungerLevel, currentThirst, currentEnergy, age);
        setColor(color);
    }
}
