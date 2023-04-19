package base.gui.shop;

import base.Game;
import base.gameobjects.services.PlantService;
import base.graphicsservice.Rectangle;
import base.graphicsservice.Sprite;
import base.gui.GUIButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static base.constants.Constants.CELL_SIZE;
import static base.constants.Constants.TILE_SIZE;
import static base.constants.FilePath.SHOP_PRICES_FILE_PATH;

public class ShopService {

    private static final Logger logger = LoggerFactory.getLogger(ShopService.class);

    private List<ShopItem> shopItemList;
    private int maxColumns = 7;

    public ShopService() {
        initializeShop();
    }

    private void initializeShop() {
        File file = new File(SHOP_PRICES_FILE_PATH);
        if (!file.exists()) {
            logger.info("File with prices doesn't exist");
            shopItemList = new ArrayList<>();
            return;
        }
        try {
            Gson gson = new Gson();
            Reader reader = new FileReader(SHOP_PRICES_FILE_PATH);
            Type collectionType = new TypeToken<List<ShopItem>>() {}.getType();
            shopItemList = gson.fromJson(reader, collectionType);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ShopMenu createShopMenu(Game game, Rectangle vendorRectangle) {
        if (!shopItemList.isEmpty()) {
            return createShopMenuFromJsonPrices(game, vendorRectangle);
        }
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
        return new ShopMenu(buttons, vendorRectangle.getX() - (3 * CELL_SIZE) + (CELL_SIZE / 2), vendorRectangle.getY() - (columns * TILE_SIZE));
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

    public ShopMenu createShopMenuFromJsonPrices(Game game, Rectangle vendorRectangle) {
        logger.info("initializing shop menu from json file");

        List<GUIButton> buttons = new ArrayList<>();

        int currentRow = 0;
        int currentColumn = 0;
        List<ShopItem> itemsForSale = shopItemList.stream()
                .filter(shopItem -> shopItem.getBuyPrice() > 0)
                .collect(Collectors.toList());

        for (ShopItem shopItem : itemsForSale) {
            Rectangle buttonRectangle = new Rectangle(currentColumn * (CELL_SIZE + 2), currentRow * (CELL_SIZE + 2), CELL_SIZE, CELL_SIZE);
            Sprite itemSprite = game.getSpriteService().getPlantPreviewSprite(shopItem.getItemName());
            buttons.add(new ShopButton(shopItem.getItemName(), itemSprite, buttonRectangle, shopItem.getBuyPrice()));

            currentColumn++;
            if (currentColumn >= maxColumns) {
                currentRow++;
                currentColumn = 0;
            }
        }
        // (3 * CELL_SIZE) - half of the buttons; (CELL_SIZE / 2) - half of cell, to be in the middle; 100 - distance from npc to shop cells
        return new ShopMenu(buttons, vendorRectangle.getX() - (3 * CELL_SIZE) + (CELL_SIZE / 2), vendorRectangle.getY() - (maxColumns * TILE_SIZE));
    }

    public int getItemPrice(String itemName) {
        for (ShopItem price : shopItemList) {
            if (price.getItemName().equals(itemName)) {
                return price.getSellPrice();
            }
        }
        return 0;
    }
}
