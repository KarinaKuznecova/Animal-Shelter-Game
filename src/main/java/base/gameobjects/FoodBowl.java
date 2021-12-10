package base.gameobjects;

import base.Game;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static base.Game.TILE_SIZE;

public class FoodBowl implements GameObject {

    protected static final Logger logger = LoggerFactory.getLogger(FoodBowl.class);

    private final int x;
    private final int y;
    private final AnimatedSprite sprite;
    private final Rectangle rectangle;
    private boolean isFull;

    public FoodBowl(int x, int y) {
        this.x = x;
        this.y = y;
        sprite = ImageLoader.getAnimatedSprite("img/bowl.png", TILE_SIZE);
        sprite.setAnimationRange(0, 1);
        sprite.vertical = false;

        rectangle = new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
        rectangle.generateGraphics(1, 123);

        isFull = false;
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
        return 1;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom, Game game) {
        if (mouseRectangle.intersects(rectangle)) {
            logger.info("Food bowl is clicked");
            if (!isFull && game.getSelectedItem().length() > 2) {
                logger.debug("Will fill food bowl");
                fillBowl();
                game.removeItemFromInventory(game.getSelectedItem());
            }
            return true;
        }
        return false;
    }

    public void emptyBowl() {
        sprite.reset();
        isFull = false;
    }

    public void fillBowl() {
        sprite.incrementSprite();
        isFull = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodBowl foodBowl = (FoodBowl) o;
        return x == foodBowl.x && y == foodBowl.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public boolean isFull() {
        return isFull;
    }
}
