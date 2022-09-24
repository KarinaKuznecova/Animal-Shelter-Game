package base.gameobjects;

import base.Game;
import base.gameobjects.interactionzones.InteractionZoneStorageChest;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.TILE_SIZE;

public class StorageChest implements GameObject {

    private static final Logger logger = LoggerFactory.getLogger(StorageChest.class);

    private final int x;
    private final int y;
    private Rectangle rectangle;
    private Sprite sprite;

    public InteractionZoneStorageChest interactionZone;

    private boolean isOpen;

    public StorageChest(int x, int y, Sprite sprite) {
        this.x = x;
        this.y = y;
        rectangle = new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
        this.sprite = sprite;
        interactionZone = new InteractionZoneStorageChest(x + 32, y + 32, 50);

        isOpen = false;
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (sprite != null) {
            renderer.renderSprite(sprite, x, y, zoom, false);
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
        if (mouseRectangle.intersects(rectangle) && !isOpen) {
            logger.info("Storage chest clicked");
            // will show another inventory
            return true;
        }
        return false;
    }
}
