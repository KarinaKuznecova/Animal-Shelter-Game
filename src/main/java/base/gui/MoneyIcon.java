package base.gui;

import base.graphicsservice.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.FilePath.MONEY_ICON_PATH;

public class MoneyIcon {

    private final Sprite sprite;
    private Rectangle rectangle;

    protected static final Logger logger = LoggerFactory.getLogger(MoneyIcon.class);

    public MoneyIcon() {
        sprite = new Sprite(ImageLoader.loadImage(MONEY_ICON_PATH));
        rectangle = new Rectangle();
    }

    public void render(RenderHandler renderer) {
        renderer.renderSprite(sprite, rectangle.getX(), rectangle.getY(), 1, true);
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}
