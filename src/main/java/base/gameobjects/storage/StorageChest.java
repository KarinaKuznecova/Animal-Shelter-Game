package base.gameobjects.storage;

import base.Game;
import base.gameobjects.GameObject;
import base.gameobjects.interactionzones.InteractionZoneStorageChest;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import static base.constants.ColorConstant.YELLOW;
import static base.constants.Constants.DEBUG_MODE;
import static base.constants.Constants.TILE_SIZE;

public class StorageChest implements GameObject {

    private static final Logger logger = LoggerFactory.getLogger(StorageChest.class);

    private final int x;
    private final int y;
    private Rectangle rectangle;
    private transient Sprite spriteClosed;
    private transient Sprite spriteOpen;
    private Storage storage;
    private String fileName;

    public transient InteractionZoneStorageChest interactionZone;

    private boolean isOpen;

    public StorageChest(int x, int y, Sprite spriteClosed, Sprite spriteOpen) {
        this.x = x;
        this.y = y;
        rectangle = new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
        this.spriteClosed = spriteClosed;
        this.spriteOpen = spriteOpen;
        interactionZone = new InteractionZoneStorageChest(x + 32, y + 32, 90);
        storage = new Storage(6, rectangle, fileName);
        fileName = String.valueOf(new Random().nextInt(Integer.MAX_VALUE));     // TODO: change to smth prettier - issue #324 on github

        isOpen = false;
    }

    public StorageChest(int x, int y, String fileName) {
        this.x = x;
        this.y = y;
        rectangle = new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
        interactionZone = new InteractionZoneStorageChest(x + 32, y + 32, 90);
        storage = new Storage(6, rectangle, fileName);
        this.fileName = fileName;
        readFile();

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
        storage.update(game);
        if (!interactionZone.isPlayerInRange() && isOpen) {
            isOpen = false;
            storage.setVisible(false);
            storage.removeRenderedText(game.getRenderer());
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
                storage.removeRenderedText(game.getRenderer());
                logger.info("Storage chest closed");
            }
            return true;
        }
        return storage.handleMouseClick(mouseRectangle, camera, zoom, game);
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }

    public Storage getStorage() {
        return storage;
    }

    public String getFileName() {
        return fileName;
    }

    // TODO: change to json, issue #353
    public void readFile() {
        File mapFile = new File("maps/storages/" + fileName);
        try (Scanner scanner = new Scanner(mapFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitLine = line.split(":");
                String itemName = splitLine[0];
                int qty = Integer.parseInt(splitLine[1]);
                if (!itemName.startsWith(fileName) && qty > 0) {
                    storage.addItem(itemName, qty);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setSpriteClosed(Sprite spriteClosed) {
        this.spriteClosed = spriteClosed;
    }

    public void setSpriteOpen(Sprite spriteOpen) {
        this.spriteOpen = spriteOpen;
    }
}
