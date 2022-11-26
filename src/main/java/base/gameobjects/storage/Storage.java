package base.gameobjects.storage;

import base.Game;
import base.gameobjects.GameObject;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;

import java.util.ArrayList;
import java.util.List;

import static base.constants.ColorConstant.*;
import static base.constants.Constants.*;

public class Storage implements GameObject {

    public static final int BORDER_SIZE = 2;
    private int size;
    private Rectangle chestRectangle;
    private boolean isVisible;
    private List<StorageCell> cells = new ArrayList<>();

    public Storage(int size, Rectangle chestRectangle) {
        this.size = size;
        for (int i = 0; i < size; i++) {
            cells.add(new StorageCell());
        }
        this.chestRectangle = chestRectangle;
        isVisible = false;
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        int halfWidthOfAllCells = (CELL_SIZE * size) / 2;
        int halfWidthOfChest = (chestRectangle.getWidth() / 2) * ZOOM - BORDER_SIZE;
        int storageX = (chestRectangle.getX() + halfWidthOfChest) - halfWidthOfAllCells;
        int storageY = chestRectangle.getY() - (CELL_SIZE + 20);

        for (int i = 0; i < cells.size(); i++) {
            Rectangle cellRectangle = new Rectangle(storageX + (i * (CELL_SIZE + BORDER_SIZE)), storageY, CELL_SIZE, CELL_SIZE);
            cellRectangle.generateBorder(BORDER_SIZE, BROWN, BLUE);
            renderer.renderRectangle(cellRectangle, 1, false);
        }

        if (DEBUG_MODE) {
            Rectangle storageRectangle = new Rectangle(storageX, storageY, CELL_SIZE * size + ((cells.size() - 1) * BORDER_SIZE), CELL_SIZE);
            storageRectangle.generateBorder(BORDER_SIZE, GREEN);
            renderer.renderRectangle(storageRectangle, 1, false);
        }
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
        if (isVisible) {
            // TODO: do smth with items
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
}
