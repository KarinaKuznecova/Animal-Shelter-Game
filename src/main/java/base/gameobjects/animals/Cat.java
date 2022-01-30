package base.gameobjects.animals;

import base.gameobjects.Animal;

import static base.constants.Constants.CAT_BLACK;

public class Cat extends Animal {

    public static final String NAME = "cat";

    public Cat(int startX, int startY, int speed, String color) {
        super(color != null ? NAME + "-" + color : CAT_BLACK, startX, startY, speed, 32, MAX_HUNGER, MAX_THIRST);
        setColor(color);
    }

    public Cat(int startX, int startY, int speed, String color, int hungerLevel, int currentThirst) {
        super(color != null ? NAME + "-" + color : CAT_BLACK, startX, startY, speed, 32, hungerLevel, currentThirst);
        setColor(color);
    }
}
