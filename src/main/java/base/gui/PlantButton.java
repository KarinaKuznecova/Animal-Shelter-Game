package base.gui;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

import static base.constants.ColorConstant.*;

public class PlantButton extends GUIButton {

    private final String plantType;
    private boolean isGreen = false;

    public PlantButton(String plantType, Sprite tileSprite, Rectangle rectangle) {
        super(tileSprite, rectangle, true);
        this.plantType = plantType;
        this.sprite = tileSprite;
        rectangle.generateBorder(3, BROWN, BLUE);
    }

    @Override
    public void render(RenderHandler renderer, int zoom, Rectangle rectangle) {
        renderer.renderRectangle(this.rectangle, rectangle, 1, fixed);
        if (sprite != null) {
            if (objectCount > 1) {
                renderer.renderSprite(sprite,
                        this.rectangle.getX() + rectangle.getX(),
                        this.rectangle.getY() + rectangle.getY(),
                        zoom, fixed, objectCount);
            } else {
                renderer.renderSprite(sprite,
                        this.rectangle.getX() + rectangle.getX(),
                        this.rectangle.getY() + rectangle.getY(),
                        zoom, fixed);
            }
        }
    }

    @Override
    public void activate(Game game) {
        game.changeSelectedPlant(plantType);
    }

    @Override
    public void update(Game game) {
        if (plantType.equals(game.getSelectedPlant())) {
            if (!isGreen) {
                rectangle.generateBorder(5, GREEN, BLUE);
                isGreen = true;
            }
        } else {
            if (isGreen) {
                rectangle.generateBorder(3, BROWN, BLUE);
                isGreen = false;
            }
        }
    }

    @Override
    public int getLayer() {
        return 5;
    }
}
