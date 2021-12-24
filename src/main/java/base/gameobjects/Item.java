package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.TILE_SIZE;

public class Item implements GameObject {

    protected static final Logger logger = LoggerFactory.getLogger(Item.class);
    private final int x;
    private final int y;
    private final String itemName;
    private final Sprite sprite;
    private final Rectangle rectangle;

    public Item(int x, int y, String itemName, Sprite sprite) {
        this.x = x;
        this.y = y;
        this.itemName = itemName;
        this.sprite = sprite;

        rectangle = new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
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
            logger.info("Item is clicked");
            game.pickUpItem(itemName, sprite, rectangle);
            return true;
        }
        return false;
    }

    public String getItemName() {
        return itemName;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}
