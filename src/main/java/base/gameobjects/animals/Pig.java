package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;
import base.graphicsservice.RenderHandler;

import static base.constants.ColorConstant.GREEN;
import static base.constants.ColorConstant.YELLOW;
import static base.constants.Constants.DEBUG_MODE;
import static base.gameobjects.AgeStage.BABY;

public class Pig extends Animal {

    public static final String TYPE = "pig";

    public Pig(int startX, int startY, int speed, int hungerLevel, int currentThirst, int currentEnergy, AgeStage age, String name) {
        super(TYPE, startX, startY, speed, 64, hungerLevel, currentThirst, currentEnergy, age, name);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        int xForSprite;
        int yForSprite;
        if (BABY.equals(age) && !animalType.contains("baby")) {
            zoom = 1;
            xForSprite = rectangle.getX();
            yForSprite = rectangle.getY() + 5;
        } else {
            xForSprite = rectangle.getX() - 32;
            yForSprite = rectangle.getY() - 32;
        }
        if (animatedSprite != null) {
            renderer.renderSprite(animatedSprite, xForSprite - 24, yForSprite - 38, zoom, false);
        }
        if (DEBUG_MODE) {
            if (interactionZone.isPlayerInRange()) {
                rectangle.generateBorder(2, GREEN);
            } else {
                rectangle.generateBorder(1, YELLOW);
            }
            renderer.renderRectangle(rectangle, 1, false);
            interactionZone.render(renderer, zoom);
        }
        if (isSelected && !DEBUG_MODE) {
            interactionZone.render(renderer, zoom);
        }
        if (interactionZone.isPlayerInRange()) {
            heartIcon.render(renderer, 1);
        }
    }
}
