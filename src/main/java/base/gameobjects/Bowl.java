package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static base.constants.Constants.TILE_SIZE;

public abstract class Bowl implements GameObject {

    protected static final Logger logger = LoggerFactory.getLogger(Bowl.class);

    private final int x;
    private final int y;
    protected AnimatedSprite sprite;
    protected final Rectangle rectangle;
    protected boolean isFull;

    protected Bowl(int x, int y) {
        this.x = x;
        this.y = y;
        rectangle = new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
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
        Bowl foodBowl = (Bowl) o;
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
