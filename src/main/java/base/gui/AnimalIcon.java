package base.gui;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

public class AnimalIcon extends GUIButton {

    private Game game;
    private final Sprite sprite;
    private boolean isGreen = false;
    private int animalId;

    public AnimalIcon(Game game, int animalId, Sprite sprite, Rectangle rectangle) {
        super(sprite, rectangle, true);
        this.sprite = sprite;
        this.animalId = animalId;
        this.game = game;
        rectangle.generateGraphics(3, 0xFFDB3D);
    }

    public void render(RenderHandler renderer, int xZoom, int yZoom, Rectangle rectangle) {
        if (sprite != null) {
            renderer.renderSprite(sprite,
                    region.getX() + rectangle.getX(),
                    region.getY() + rectangle.getY(),
                    xZoom,
                    yZoom,
                    fixed);
        }
        renderer.renderRectangle(region, rectangle, 1, 1, fixed);
    }

    @Override
    public void update(Game game) {
        if (animalId == game.getSelectedAnimal()) {
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
        game.changeAnimal(animalId);
    }
}
