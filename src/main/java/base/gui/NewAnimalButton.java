package base.gui;

import base.Game;
import base.gameobjects.AnimalService;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

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
        rectangle.generateGraphics(3, 0xFFDB3D);
        if (animalType.equalsIgnoreCase("Cat")) {
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
                region.generateGraphics(3, 0x67FF3D);
                isGreen = true;
            }
        } else {
            if (isGreen) {
                region.generateGraphics(3, 0xFFDB3D);
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
