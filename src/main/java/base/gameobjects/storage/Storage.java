package base.gameobjects.storage;

import base.Game;
import base.gameobjects.GameObject;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static base.constants.ColorConstant.*;
import static base.constants.Constants.*;

public class Storage implements GameObject {

    private static final Logger logger = LoggerFactory.getLogger(Storage.class);

    public static final int BORDER_SIZE = 2;
    private int size;
    private Rectangle chestRectangle;
    private boolean isVisible;
    private List<StorageCell> cells = new ArrayList<>();
    private String fileName;

    public Storage(int size, Rectangle chestRectangle, String fileName) {
        this.size = size;
        this.fileName = fileName;
        this.chestRectangle = chestRectangle;
        isVisible = false;

        createCells();
    }

    private void createCells() {
        int halfWidthOfAllCells = (CELL_SIZE * size) / 2;
        int halfWidthOfChest = (chestRectangle.getWidth() / 2) * ZOOM - BORDER_SIZE;
        int storageX = (chestRectangle.getX() + halfWidthOfChest) - halfWidthOfAllCells;
        int storageY = chestRectangle.getY() - (CELL_SIZE + 20);

        for (int i = 0; i < size; i++) {
            Rectangle cellRectangle = new Rectangle(storageX + (i * (CELL_SIZE + BORDER_SIZE)), storageY, CELL_SIZE, CELL_SIZE);
            cellRectangle.generateBorder(BORDER_SIZE, BROWN, BLUE);
            StorageCell cell = new StorageCell(cellRectangle, fileName + "-" + i, null, fileName + "-" + i);
            cells.add(cell);
        }
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        int halfWidthOfAllCells = (CELL_SIZE * size) / 2;
        int halfWidthOfChest = (chestRectangle.getWidth() / 2) * ZOOM - BORDER_SIZE;
        int storageX = (chestRectangle.getX() + halfWidthOfChest) - halfWidthOfAllCells;
        int storageY = chestRectangle.getY() - (CELL_SIZE + 20);

        for (StorageCell cell : cells) {
            cell.render(renderer);
        }

        if (DEBUG_MODE) {
            Rectangle storageRectangle = new Rectangle(storageX, storageY, CELL_SIZE * size + ((cells.size() - 1) * BORDER_SIZE), CELL_SIZE);
            storageRectangle.generateBorder(BORDER_SIZE, GREEN);
            renderer.renderRectangle(storageRectangle, 1, false);
        }
    }

    public void removeRenderedText(RenderHandler renderHandler) {
        for (StorageCell cell : cells) {
            cell.removeRenderedText(renderHandler);
        }
    }

    @Override
    public void update(Game game) {
        for (StorageCell cell : cells) {
            cell.update(game);
        }
    }

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (isVisible) {
            for (StorageCell cell : cells) {
                if (cell.handleMouseClick(mouseRectangle, camera, zoom, game)) {
                    logger.info(String.format("Storage cell %d clicked", cells.indexOf(cell)));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Rectangle getRectangle() {
        return null;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public List<StorageCell> getCells() {
        return cells;
    }

    public void addItem(String itemName, int qty) {
        for (StorageCell cell : cells) {
            if (cell.isButtonEmpty()) {
                cell.setItem(itemName);
                cell.setObjectCount(qty);
                return;
            }
        }
    }
}
