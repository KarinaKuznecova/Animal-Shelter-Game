package base.gui;

import base.Game;
import base.gameobjects.Animal;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

import static base.constants.ColorConstant.*;

public class AnimalIcon extends GUIButton {

    private final Game game;
    private boolean isGreen = false;
    private final Animal animal;

    public AnimalIcon(Game game, Animal animal, Sprite sprite, Rectangle rectangle) {
        super(sprite, rectangle, true);
        this.sprite = sprite;
        this.animal = animal;
        this.game = game;
        if (isAnimalOnActiveMap(game)) {
            rectangle.generateGraphics(3, YELLOW);
        } else {
            rectangle.generateGraphics(3, GRAY);
        }
    }

    private boolean isAnimalOnActiveMap(Game game) {
        return animal.getHomeMap().equalsIgnoreCase(game.getGameMap().getMapName());
    }

    @Override
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
        if (animal == game.getYourSelectedAnimal()) {
            if (!isGreen) {
                region.generateGraphics(3, GREEN);
                isGreen = true;
            }
        } else {
            if (isGreen) {
                if (isAnimalOnActiveMap(game)) {
                    region.generateGraphics(3, YELLOW);
                } else {
                    region.generateGraphics(3, GRAY);
                }
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
        game.changeYourAnimal(animal);
    }

    public Animal getAnimal() {
        return animal;
    }
}
