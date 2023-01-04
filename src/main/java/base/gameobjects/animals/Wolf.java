package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;
import base.graphicsservice.RenderHandler;

import static base.constants.ColorConstant.GREEN;
import static base.constants.ColorConstant.YELLOW;
import static base.constants.Constants.DEBUG_MODE;
import static base.gameobjects.AgeStage.BABY;

public class Wolf extends Animal {

    public static final String TYPE = "wolf";

    public Wolf(int startX, int startY, int speed, int currentHunger, int currentThirst, int currentEnergy, AgeStage age, String name) {
        super(TYPE, startX, startY, speed, 64, currentHunger, currentThirst, currentEnergy, age, name);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        int xForSprite;
        int yForSprite;
        if (BABY.equals(age) && !animalType.contains("baby")) {
            zoom = 1;
            xForSprite = rectangle.getX() + (tileSize / 4) + 5;
            yForSprite = rectangle.getY() + rectangle.getHeight() + 5;
        } else {
            xForSprite = rectangle.getX() - 12;
            yForSprite = rectangle.getY() - 12;
        }
        if (animatedSprite != null) {
            renderer.renderSprite(animatedSprite, xForSprite - 46, yForSprite - 52, zoom, false);
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
