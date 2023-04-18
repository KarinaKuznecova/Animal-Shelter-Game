package base.gameobjects;

import base.graphicsservice.Sprite;

import java.util.Arrays;
import java.util.List;

public class PetFood extends Item {

    public static int SIMPLE_MEAL_SPRITE_ID = 80;
    public static String SIMPLE_MEAL = "simpleMeal";

    public static int TASTY_MEAL_SPRITE_ID = 79;
    public static String TASTY_MEAL = "tastyMeal";

    public static int PERFECT_MEAL_SPRITE_ID = 78;
    public static String PERFECT_MEAL = "perfectMeal";

    public static List<String> mealTypes = Arrays.asList(SIMPLE_MEAL, TASTY_MEAL, PERFECT_MEAL);

    public PetFood(int x, int y, String itemName, Sprite sprite) {
        super(x, y, itemName, sprite);
    }

    public PetFood(int x, int y, String itemName) {
        super(x, y, itemName);
    }
}
