package base.gui;

import base.Game;
import base.gameobjects.services.AnimalService;
import base.gameobjects.animals.Cat;
import base.gameobjects.animals.Rat;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

import static base.constants.ColorConstant.GREEN;
import static base.constants.ColorConstant.YELLOW;

public class NewAnimalButton extends GUIButton {

    private final Game game;
    private boolean isGreen = false;
    private String animalType;

    private final AnimalService animalService = new AnimalService();

    public NewAnimalButton(Game game, String animalType, Sprite sprite, Rectangle rectangle) {
        super(sprite, rectangle, true);
        this.sprite = sprite;
        this.animalType = animalType;
        this.game = game;
        rectangle.generateBorder(3, YELLOW);
        if (animalType.equalsIgnoreCase(Cat.TYPE) || animalType.equalsIgnoreCase(Rat.TYPE)) {
            multipleOptions = true;
        }
    }

    @Override
    public void render(RenderHandler renderer, int xZoom, int yZoom, Rectangle rectangle) {
        if (sprite != null) {
            if (multipleOptions) {
                renderer.renderSprite(sprite,
                        region.getX() + rectangle.getX(),
                        region.getY() + rectangle.getY(),
                        xZoom,
                        yZoom,
                        fixed, "<      >");
            } else {
                renderer.renderSprite(sprite,
                        region.getX() + rectangle.getX(),
                        region.getY() + rectangle.getY(),
                        xZoom,
                        yZoom,
                        fixed);
            }
        }
        renderer.renderRectangle(region, rectangle, 1, 1, fixed);
    }

    @Override
    public void update(Game game) {
        if (animalType.equals(game.getSelectedAnimal())) {
            if (!isGreen) {
                region.generateBorder(3, GREEN);
                isGreen = true;
            }
        } else {
            if (isGreen) {
                region.generateBorder(3, YELLOW);
                isGreen = false;
            }
        }
    }

    @Override
    public int getLayer() {
        return 5;
    }

    @Override
    public void activate() {
        String nextColor = animalService.getNextColor(animalType);
        if (isGreen && !animalType.equals(nextColor)) {
            animalType = nextColor;
            sprite = animalService.getNewColorSprite(animalType);
        }
        game.changeAnimal(animalType);
    }
}
