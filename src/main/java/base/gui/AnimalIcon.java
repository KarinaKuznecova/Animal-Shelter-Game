package base.gui;

import base.Game;
import base.gameobjects.Animal;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

import static base.constants.ColorConstant.*;
import static base.gameobjects.AgeStage.BABY;

public class AnimalIcon extends GUIButton {

    private final Game game;
    private boolean isGreen = false;
    private final Animal animal;
    private final AnimalStats stats;

    public AnimalIcon(Game game, Animal animal, Sprite sprite, Rectangle rectangle) {
        super(sprite, rectangle, true);
        this.sprite = sprite;
        this.animal = animal;
        this.game = game;

        rectangle.generateBorder(3, getBorderColor());
        stats = new AnimalStats(animal, region);
    }

    private boolean isAnimalOnActiveMap(Game game) {
        return animal.getCurrentMap().equalsIgnoreCase(game.getGameMap().getMapName());
    }

    @Override
    public void render(RenderHandler renderer, int zoom, Rectangle rectangle) {
        if (sprite != null) {
            renderer.renderSprite(sprite,
                    region.getX() + rectangle.getX(),
                    region.getY() + rectangle.getY(),
                    zoom,
                    fixed);
        }
        renderer.renderRectangle(region, rectangle, 1, fixed);
        if (stats.isVisible()) {
            stats.renderStats(renderer, rectangle);
        }
    }

    @Override
    public void update(Game game) {
        if (animal == game.getYourSelectedAnimal()) {
            if (!isGreen) {
                region.generateBorder(5, GREEN);
                isGreen = true;
                stats.setVisible(true);
            }
        } else {
            if (isGreen) {
                region.generateBorder(3, getBorderColor());
                game.getRenderer().clearRenderedText();
                isGreen = false;
                stats.setVisible(false);
            }
        }
        stats.update(game);
    }

    @Override
    public int getLayer() {
        return 5;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (mouseRectangle.intersects(region)) {
            activate();
            return true;
        }
        if (isGreen) {
            return stats.handleMouseClick(mouseRectangle, game);
        }
        return false;
    }

    @Override
    public void activate() {
        game.changeYourAnimal(animal);
    }

    public Animal getAnimal() {
        return animal;
    }

    public int getBorderColor() {
        if (isAnimalOnActiveMap(game)) {
            if (BABY.equals(animal.getAge())) {
                return LIGHT_YELLOW;
            } else {
                return YELLOW;
            }
        } else {
            if (BABY.equals(animal.getAge())) {
                return LIGHT_GRAY;
            } else {
                return GRAY;
            }
        }
    }

}
