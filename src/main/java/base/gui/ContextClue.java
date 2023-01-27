package base.gui;

import base.graphicsservice.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.FilePath.HEART_ICON_PATH;

public class ContextClue {

    private final transient Sprite sprite;
    private Rectangle rectangle;

    protected static final Logger logger = LoggerFactory.getLogger(ContextClue.class);

    public ContextClue() {
        sprite = new Sprite(ImageLoader.loadImage(HEART_ICON_PATH));
        rectangle = new Rectangle();
    }

    public void changePosition(Position position) {
        rectangle = new Rectangle(position.getXPosition() - 3, position.getYPosition() - 3, 20, 20);
    }

    public void render(RenderHandler renderer, int zoom) {
        renderer.renderSprite(sprite, rectangle.getX() + 2, rectangle.getY() + 2, zoom, true);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}
