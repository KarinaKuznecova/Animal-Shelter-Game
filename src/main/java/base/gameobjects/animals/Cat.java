package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;

import static base.constants.Constants.CAT_BLACK;
import static base.gameobjects.AgeStage.ADULT;

public class Cat extends Animal {

    public static final String TYPE = "cat";

    public Cat(int startX, int startY, int speed, String color) {
        super(getName(color, ADULT), startX, startY, speed, 32, MAX_HUNGER, MAX_THIRST, MAX_ENERGY, ADULT, "");
        setColor(color);
    }

    public Cat(int startX, int startY, int speed, String color, int hungerLevel, int currentThirst, int currentEnergy, AgeStage age, String name) {
        super(getName(color, age), startX, startY, speed, 32, hungerLevel, currentThirst, currentEnergy, age, name);
        setColor(color);
    }


    private static String getName(String color, AgeStage age) {
        String name = color != null ? TYPE + "-" + color : CAT_BLACK;
        if (AgeStage.BABY.equals(age)) {
            name = name + "-baby";
        }
        return name;
    }

    @Override
    protected void updateAge() {
        incrementAge();
        if (isTimeToGrowUp()) {
            setAge(ADULT);
            resetSpeedToDefault();
            setAnimalType(getName(getColor(), ADULT));
            setSprite();
            setPreviewSprite();
        }
    }
}
