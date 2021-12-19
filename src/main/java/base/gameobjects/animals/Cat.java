package base.gameobjects.animals;

import base.gameobjects.Animal;

import java.util.Arrays;
import java.util.List;

import static base.constants.Constants.*;

public class Cat extends Animal {

    public static final String NAME = "cat";

    public static final List<String> colors = Arrays.asList(CAT_WHITE, CAT_BROWN, CAT_CARAMEL, CAT_BLACK);

    public Cat(int startX, int startY, int speed, String color) {
        super(color != null ? NAME + "-" + color : CAT_BLACK, startX, startY, speed, 32, MAX_HUNGER);
        setColor(color);
    }

    public Cat(int startX, int startY, int speed, String color, int hungerLevel) {
        super(color != null ? NAME + "-" + color : CAT_BLACK, startX, startY, speed, 32, hungerLevel);
        setColor(color);
    }
}
