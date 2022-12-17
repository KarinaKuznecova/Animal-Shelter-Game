package base.gameobjects.animals;

import base.gameobjects.AgeStage;
import base.gameobjects.Animal;
import base.graphicsservice.RenderHandler;

import static base.constants.ColorConstant.GREEN;
import static base.constants.ColorConstant.YELLOW;
import static base.constants.Constants.DEBUG_MODE;
import static base.gameobjects.AgeStage.BABY;

public class Mouse extends Animal {

    public static final String TYPE = "mouse";

    public Mouse(int startX, int startY, int speed, int hungerLevel, int currentThirst, int currentEnergy, AgeStage age, String name) {
        super(TYPE, startX, startY, speed, 32, hungerLevel, currentThirst, currentEnergy, age, name);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        int xForSprite = rectangle.getX();
        int yForSprite = rectangle.getY();
        if (BABY.equals(age) && !animalType.contains("baby")) {
            zoom = 1;
            xForSprite = rectangle.getX() + rectangle.getWidth();
            yForSprite = rectangle.getY() + rectangle.getHeight() + 10;
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
