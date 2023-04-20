package base.gui;

import base.graphicsservice.Position;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextClue {

    private final transient Sprite sprite;
    private Rectangle rectangle;
    private boolean isVisible;

    protected static final Logger logger = LoggerFactory.getLogger(ContextClue.class);

    public ContextClue(Sprite sprite) {
        this.sprite = sprite;
        rectangle = new Rectangle();
    }

    public void changePosition(Position position) {
        rectangle = new Rectangle(position.getXPosition() - 3, position.getYPosition() - 3, 20, 20);
    }

    public void render(RenderHandler renderer, int zoom, boolean fixed) {
        if (isVisible) {
            renderer.renderSprite(sprite, rectangle.getX() + 2, rectangle.getY() + 2, zoom, fixed);
        }
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
