package base.loading;

import base.Game;
import base.gameobjects.services.PlantService;
import base.graphicsservice.Rectangle;
import base.graphicsservice.Sprite;
import base.gui.GUIButton;
import base.gui.shop.ShopButton;
import base.gui.shop.ShopItem;
import base.gui.shop.ShopMenu;
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

public class ShopLoadingService {

    private static final Logger logger = LoggerFactory.getLogger(ShopLoadingService.class);
    private int maxColumns = 7;

    public void loadShop(Game game) {
        File file = new File(SHOP_PRICES_FILE_PATH);
        if (!file.exists()) {
            logger.info("File with prices doesn't exist");
            game.getShopService().setShopItemList(new ArrayList<>());
            return;
        }
        try {
            Gson gson = new Gson();
            Reader reader = new FileReader(SHOP_PRICES_FILE_PATH);
            Type collectionType = new TypeToken<List<ShopItem>>() {}.getType();
            List<ShopItem> shopItemList = gson.fromJson(reader, collectionType);
            game.getShopService().setShopItemList(shopItemList);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ShopMenu createShopMenuFromJsonPrices(Game game, Rectangle vendorRectangle) {
        logger.info("initializing shop menu from json file");

        List<GUIButton> buttons = new ArrayList<>();

        int currentRow = 0;
        int currentColumn = 0;
        List<ShopItem> itemsForSale = game.getShopService().getShopItemList().stream()
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
}
