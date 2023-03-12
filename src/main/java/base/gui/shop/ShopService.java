package base.gui.shop;

import base.Game;
import base.gameobjects.services.PlantService;
import base.graphicsservice.Rectangle;
import base.graphicsservice.Sprite;
import base.gui.GUIButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static base.constants.Constants.CELL_SIZE;
import static base.constants.Constants.TILE_SIZE;

public class ShopService {

    private static final transient Logger logger = LoggerFactory.getLogger(ShopService.class);

    public ShopMenu createShopMenu(Game game, Rectangle vendorRectangle) {
        logger.info("initializing shop menu");

        List<GUIButton> buttons = new ArrayList<>();

        int rows = 2;
        int columns = 7;
        for (int i = 0; i < rows; i++) { // rows
            for (int j = 0; j < columns; j++) { // columns
                Rectangle buttonRectangle = new Rectangle(j * (CELL_SIZE + 2), i * (CELL_SIZE + 2), CELL_SIZE, CELL_SIZE);
                String itemName = getItemName(i, j, columns);
                Sprite itemSprite = game.getSpriteService().getPlantPreviewSprite(itemName);
                buttons.add(new ShopButton(itemName, itemSprite, buttonRectangle, 1));
            }
        }
        // (3 * CELL_SIZE) - half of the buttons; (CELL_SIZE / 2) - half of cell, to be in the middle; 100 - distance from npc to shop cells
        return new ShopMenu(buttons, vendorRectangle.getX() - (3 * CELL_SIZE) + (CELL_SIZE / 2), vendorRectangle.getY() - ((columns * TILE_SIZE)));
    }

    private String getItemName(int i, int j, int columns) {
        int cellNumber = (i * columns) + j;       // 6 - columns in row
        if (cellNumber < PlantService.plantTypes.size()) {
            return PlantService.plantTypes.get(cellNumber);
        }
        else if (cellNumber < PlantService.plantTypes.size() * 2){
            return "seed" + PlantService.plantTypes.get(cellNumber - PlantService.plantTypes.size());
        }
        return null;
    }
}
