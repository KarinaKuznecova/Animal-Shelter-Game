package base.gameobjects.storage;

import base.Game;
import base.gameobjects.GameObject;
import base.gameobjects.interactionzones.InteractionZoneStorageChest;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.YELLOW;
import static base.constants.Constants.*;

public class StorageChest implements GameObject {

    private static final Logger logger = LoggerFactory.getLogger(StorageChest.class);

    private final int x;
    private final int y;
    private Rectangle rectangle;
    private Sprite spriteClosed;
    private Sprite spriteOpen;
    private Storage storage;

    public InteractionZoneStorageChest interactionZone;

    private boolean isOpen;

    public StorageChest(int x, int y, Sprite spriteClosed, Sprite spriteOpen) {
        this.x = x;
        this.y = y;
        rectangle = new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
        this.spriteClosed = spriteClosed;
        this.spriteOpen = spriteOpen;
        interactionZone = new InteractionZoneStorageChest(x + 32, y + 32, 90);
        storage = new Storage(6, rectangle);

        isOpen = false;
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (!isOpen && spriteClosed != null) {
            renderer.renderSprite(spriteClosed, x, y, zoom, false);
        } else if (isOpen && spriteOpen != null) {
            renderer.renderSprite(spriteOpen, x, y, zoom, false);
            storage.render(renderer, zoom);
        }
        if (DEBUG_MODE) {
            rectangle.generateBorder(1, YELLOW);
            renderer.renderRectangle(rectangle, zoom, false);
            interactionZone.render(renderer, zoom);
        }
    }

    @Override
    public void update(Game game) {
        interactionZone.update(game);
        if (!interactionZone.isPlayerInRange() && isOpen) {
            isOpen = false;
        }
    }

    @Override
    public int getLayer() {
        return 2;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (mouseRectangle.intersects(rectangle) && interactionZone.isPlayerInRange()) {
            if (!isOpen) {
                isOpen = true;
                storage.setVisible(true);
                logger.info("Storage chest opened");
            } else {
                isOpen = false;
                storage.setVisible(false);
                logger.info("Storage chest closed");
            }
            return true;
        }
        return false;
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }
}
