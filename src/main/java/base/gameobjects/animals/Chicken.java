package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;

import static base.gameobjects.AgeStage.ADULT;

public class Chicken extends Animal {

    public static final String TYPE = "chicken";

    public Chicken(int startX, int startY, int speed, int hungerLevel, int currentThirst, int currentEnergy, AgeStage age, String name) {
        super(AgeStage.BABY.equals(age) ? TYPE + "-baby" : TYPE, startX, startY, speed, 32, hungerLevel, currentThirst, currentEnergy, age, name);
    }

    @Override
    protected void updateAge() {
        incrementAge();
        if (isTimeToGrowUp()) {
            setAge(ADULT);
            resetSpeedToDefault();
            setAnimalType(TYPE);
            loadAnimatedSprite();
            setPreviewSprite();
        }
    }
}
