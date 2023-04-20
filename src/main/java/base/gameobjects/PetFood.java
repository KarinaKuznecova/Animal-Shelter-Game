package base.gameobjects;

import base.graphicsservice.Sprite;

import java.util.Arrays;
import java.util.List;

public class PetFood extends Item {

    public static final int SIMPLE_MEAL_SPRITE_ID = 80;
    public static final String SIMPLE_MEAL = "simpleMeal";

    public static final int TASTY_MEAL_SPRITE_ID = 79;
    public static final String TASTY_MEAL = "tastyMeal";

    public static final int PERFECT_MEAL_SPRITE_ID = 78;
    public static final String PERFECT_MEAL = "perfectMeal";

    public static final List<String> mealTypes = Arrays.asList(SIMPLE_MEAL, TASTY_MEAL, PERFECT_MEAL);

    public PetFood(int x, int y, String itemName, Sprite sprite) {
        super(x, y, itemName, sprite);
    }

    public PetFood(int x, int y, String itemName) {
        super(x, y, itemName);
    }
}
