package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;

import static base.constants.MapConstants.SECOND_MAP;
import static base.gameobjects.AgeStage.ADULT;

public class Chicken extends Animal {

    public static final String NAME = "chicken";

    public Chicken(int startX, int startY, int speed, int hungerLevel, int currentThirst, int currentEnergy, AgeStage age) {
        super(AgeStage.BABY.equals(age) ? NAME + "-baby" : NAME, startX, startY, speed, 32, hungerLevel, currentThirst, currentEnergy, age);
        setHomeMap(SECOND_MAP);
    }

    @Override
    protected void updateAge() {
        incrementAge();
        if (isTimeToGrowUp()) {
            setAge(ADULT);
            resetSpeedToDefault();
            setAnimalName(NAME);
            setSprite();
            setPreviewSprite();
        }
    }
}
