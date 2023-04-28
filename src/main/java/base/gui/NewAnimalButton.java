package base.gui;

import base.Game;
import base.gameobjects.services.AnimalService;
import base.gameobjects.animals.Cat;
import base.gameobjects.animals.Rat;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

import static base.constants.ColorConstant.*;

public class NewAnimalButton extends GUIButton {

    private boolean isGreen = false;
    private String animalType;

    private final AnimalService animalService = new AnimalService();

    public NewAnimalButton(String animalType, Sprite sprite, Rectangle rectangle) {
        super(sprite, rectangle, true);
        this.sprite = sprite;
        this.animalType = animalType;
        rectangle.generateBorder(3, BROWN, BLUE);
        if (Cat.TYPE.equalsIgnoreCase(animalType) || Rat.TYPE.equalsIgnoreCase(animalType)) {
            multipleOptions = true;
        }
    }

    @Override
    public void render(RenderHandler renderer, int zoom, Rectangle rectangle) {
        renderer.renderRectangle(this.rectangle, rectangle, 1, fixed);
        if (sprite != null) {
            if (multipleOptions) {
                renderer.renderSprite(sprite,
                        this.rectangle.getX() + rectangle.getX(),
                        this.rectangle.getY() + rectangle.getY(),
                        zoom,
                        fixed, "<      >");
            } else {
                renderer.renderSprite(sprite,
                        this.rectangle.getX() + rectangle.getX(),
                        this.rectangle.getY() + rectangle.getY(),
                        zoom,
                        fixed);
            }
        }
    }

    @Override
    public void update(Game game) {
        if (animalType.equals(game.getSelectedAnimal())) {
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

    @Override
    public void activate(Game game) {
        String nextColor = animalService.getNextColor(animalType);
        if (isGreen && !animalType.equals(nextColor)) {
            animalType = nextColor;
            sprite = animalService.getNewColorSprite(animalType);
        }
        game.changeAnimal(animalType);
    }
}
