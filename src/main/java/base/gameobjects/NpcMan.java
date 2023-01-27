package base.gameobjects;

import base.Game;
import base.gameobjects.interactionzones.InteractionZoneFoodVendor;
import base.gameobjects.services.PlantService;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import base.gui.GUIButton;
import base.gui.shop.ShopButton;
import base.gui.shop.ShopMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static base.constants.Constants.*;
import static base.constants.FilePath.NPC_SHEET_PATH_MAN;

public class NpcMan extends Npc {

    private static final transient Logger logger = LoggerFactory.getLogger(NpcMan.class);

    private transient ShopMenu shopMenu;

    public NpcMan(int startX, int startY, Game game) {
        super(startX, startY);
        interactionZone = new InteractionZoneFoodVendor(startX + 32, startY + 32, 200);
        initializeShop(game);
    }

    // TODO: move to service, issue #332
    public void initializeShop(Game game) {
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
        shopMenu = new ShopMenu(buttons, rectangle.getX() - (3 * CELL_SIZE) + (CELL_SIZE / 2), rectangle.getY() - ((columns * TILE_SIZE)));
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

    @Override
    protected AnimatedSprite getAnimatedSprite() {
        return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_MAN, 64);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        int xForSprite = rectangle.getX() - 32;
        int yForSprite = rectangle.getY() - 48;
        if (animatedSprite != null) {
            renderer.renderSprite(animatedSprite, xForSprite, yForSprite, zoom, false);
        }
        if (DEBUG_MODE) {
            renderer.renderRectangle(rectangle, zoom, false);
            interactionZone.render(renderer, zoom);
        }
        shopMenu.render(renderer, zoom);
    }

    @Override
    public void update(Game game) {
        interactionZone.update(game);
        if (!interactionZone.isPlayerInRange() && shopMenu.isVisible()) {
            shopMenu.setVisible(false);
            game.closeBackpack();
            game.closeShopMenu(shopMenu);
        }
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (mouseRectangle.intersects(rectangle) && interactionZone.isPlayerInRange()) {
            logger.info("click on npc");
            if (shopMenu.isVisible()) {
                shopMenu.setVisible(false);
                game.closeBackpack();
                game.closeShopMenu(shopMenu);
            } else {
                shopMenu.setVisible(true);
                game.openBackpack();
                game.openShopMenu(shopMenu);
            }
        }
        return false;
    }

    public ShopMenu getShopMenu() {
        return shopMenu;
    }
}
