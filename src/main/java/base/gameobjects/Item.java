package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static base.constants.Constants.TILE_SIZE;
import static base.gameobjects.services.ItemService.STACKABLE_ITEMS;

public class Item implements GameObject {

    protected static final Logger logger = LoggerFactory.getLogger(Item.class);
    private final int x;
    private final int y;
    private final String itemName;
    private final Sprite sprite;
    private final Rectangle rectangle;
    private boolean stackable;
    private String mapName;
    private final int MAX_FRESHNESS = 25_000;
    private int freshness;

    public Item(int x, int y, String itemName, Sprite sprite) {
        this.x = x;
        this.y = y;
        this.itemName = itemName;
        this.sprite = sprite;
        if (STACKABLE_ITEMS.contains(itemName)) {
            stackable = true;
        }

        rectangle = new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
        freshness = MAX_FRESHNESS + new Random().nextInt(MAX_FRESHNESS);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (sprite != null) {
            renderer.renderSprite(sprite, x, y, zoom, false);
        }
    }

    @Override
    public void update(Game game) {
        if (mapName != null && !mapName.isEmpty()) {
            freshness--;
            if (freshness < 1) {
                game.getGameMap().removeItem(itemName, rectangle);
            }
        }
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
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

    public Sprite getSprite() {
        return sprite;
    }

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
