package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;

import static base.constants.Constants.*;
import static base.gameobjects.AgeStage.ADULT;

public class Rat extends Animal {

    public static final String TYPE = "rat";

    public Rat(int startX, int startY, int speed, String color) {
        super(color != null ? TYPE + "-" + color : RAT_BLACK, startX, startY, speed, 32, MAX_HUNGER, MAX_THIRST, MAX_ENERGY, ADULT, "");
        setColor(color);
        originalType = TYPE;
    }

    public Rat(int startX, int startY, int speed, String color, int hungerLevel, int currentThirst, int currentEnergy, AgeStage age, String name) {
        super(color != null ? TYPE + "-" + color : RAT_BLACK, startX, startY, speed, 32, hungerLevel, currentThirst, currentEnergy, age, name);
        setColor(color);
        originalType = TYPE;
    }
}
