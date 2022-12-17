package base.gameobjects;

import base.Game;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.CELL_SIZE;
import static base.constants.FilePath.MONEY_ICON_PATH;

public class Coin implements GameObject {

    protected static final Logger logger = LoggerFactory.getLogger(Coin.class);

    private final Sprite sprite;
    private Rectangle rectangle;
    private int amount;

    public Coin(int xPosition, int yPosition, int amount) {
        this.amount = amount;

        sprite = new Sprite(ImageLoader.loadImage(MONEY_ICON_PATH));
        rectangle = new Rectangle(xPosition, yPosition, CELL_SIZE, CELL_SIZE);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        renderer.renderSprite(sprite, rectangle.getX() + 16, rectangle.getY() + 16, 1, false);
    }

    @Override
    public void update(Game game) {

    }

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (mouseRectangle.intersects(rectangle)) {
            logger.info("Coin is clicked, will pick up");
            game.pickUpCoins(this);
            return true;
        }
        return false;
    }

    @Override
    public Rectangle getRectangle() {
        return null;
    }

    public int getAmount() {
        return amount;
    }
}
