package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.TILE_SIZE;

public class Chest implements GameObject {

    private final int x;
    private final int y;
    private Sprite sprite;
    private final Rectangle rectangle;
    private boolean isOpen;
    private final Animal animal;

    public Chest(int x, int y, Animal animal, Sprite sprite) {
        this.x = x;
        this.y = y;
        this.animal = animal;
        this.sprite = sprite;

        rectangle = new Rectangle(x, y, TILE_SIZE, TILE_SIZE);

        isOpen = false;
    }

    @Override
    public void render(RenderHandler renderer, int xZoom, int yZoom) {
        if (sprite != null) {
            renderer.renderSprite(sprite, x, y, xZoom, yZoom, false);
        }
    }

    @Override
    public void update(Game game) {

    }

    @Override
    public int getLayer() {
        return 2;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom, Game game) {
        if (mouseRectangle.intersects(rectangle) && !isOpen) {
            sprite = game.getTileService().getTiles().get(37).getSprite();
            isOpen = true;
            animal.setHomeMap(game.getGameMap().getMapName());
            game.getAnimalsOnMaps().get(game.getGameMap().getMapName()).add(animal);
            game.addAnimalToPanel(animal);
            game.saveMap();

            return true;
        }
        return false;
    }
}
