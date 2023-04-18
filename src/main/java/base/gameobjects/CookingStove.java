package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.Constants.ZOOM;

public class CookingStove implements GameObject {

    protected static final Logger logger = LoggerFactory.getLogger(CookingStove.class);

    public static final int TILE_ID = 152;

    private transient Sprite sprite;
    private final int xPosition;
    private final int yPosition;
    private final Rectangle rectangle;

    public CookingStove(int xPosition, int yPosition, Sprite sprite) {
        this(xPosition, yPosition);
        this.sprite = sprite;
    }

    public CookingStove(int xPosition, int yPosition) {
        logger.info("CREATIN STOVE");
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.rectangle = new Rectangle(xPosition, yPosition, TILE_SIZE, TILE_SIZE);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (sprite != null) {
            renderer.renderSprite(sprite, xPosition, yPosition, ZOOM, false);
        }
    }

    @Override
    public void update(Game game) {

    }

    @Override
    public int getLayer() {
        return 3;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (mouseRectangle.intersects(rectangle)) {
            logger.info("Stove is clicked, cooking menu coming soon");
            // open cooking menu
            return true;
        }
        return false;
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
}
