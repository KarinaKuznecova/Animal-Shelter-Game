package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.GREEN;
import static base.constants.Constants.DEBUG_MODE;
import static base.constants.FilePath.OAK_IMG;
import static base.graphicsservice.ImageLoader.getPreviewSprite;

public class Oak implements GameObject {

    private static final Logger logger = LoggerFactory.getLogger(Oak.class);

    private final int x;
    private final int y;
    private final Sprite sprite;
    private final Rectangle originalRectangle;
    private final Rectangle rectangle;

    public Oak(int x, int y) {
        this.x = x;
        this.y = y;

        sprite = getPreviewSprite(OAK_IMG);
        originalRectangle = new Rectangle(x, y, 72, 65);
        rectangle = new Rectangle(x + 26, y + 78, 72, 65);
        rectangle.generateBorder(1, GREEN);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (sprite != null) {
            renderer.renderSprite(sprite, x - 26, y - 78, zoom, false);
        }
        if (DEBUG_MODE) {
            renderer.renderRectangle(rectangle, 1, false);
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
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        return false;
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }

    public Rectangle getOriginalRectangle() {
        return originalRectangle;
    }
}
