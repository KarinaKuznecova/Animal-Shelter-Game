package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.CAT_BLACK;
import static base.constants.Constants.RAT_BLACK;
import static base.gameobjects.AgeStage.ADULT;

public class Rat extends Animal {

    public static final String NAME = "rat";

    public Rat(int startX, int startY, int speed, String color) {
        super(color != null ? NAME + "-" + color : RAT_BLACK, startX, startY, speed, 32, MAX_HUNGER, MAX_THIRST, MAX_ENERGY, ADULT);
        setColor(color);
    }

    public Rat(int startX, int startY, int speed, String color, int hungerLevel, int currentThirst, int currentEnergy, AgeStage age) {
        super(color != null ? NAME + "-" + color : RAT_BLACK, startX, startY, speed, 32, hungerLevel, currentThirst, currentEnergy, age);
        setColor(color);
    }
}
