package base.gameobjects.npc;

import base.Game;
import base.graphicsservice.AnimatedSprite;
import base.gameobjects.interactionzones.InteractionZoneFoodVendor;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.gui.shop.ShopMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.DEBUG_MODE;
import static base.constants.FilePath.NPC_SHEET_PATH_MAN;

public class NpcVendor extends Npc {

    private static final transient Logger logger = LoggerFactory.getLogger(NpcVendor.class);

    private transient ShopMenu shopMenu;

    public NpcVendor(int startX, int startY) {
        super(startX, startY);
        type = NpcType.VENDOR;
        interactionZone = new InteractionZoneFoodVendor(startX + 32, startY + 32, 200);
    }

    @Override
    protected AnimatedSprite getAnimatedSprite() {
        return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_MAN, 64, 10);
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

    /** =================================== GETTERS & SETTERS ====================================== */

    public void setShopMenu(ShopMenu shopMenu) {
        this.shopMenu = shopMenu;
    }

    public ShopMenu getShopMenu() {
        return shopMenu;
    }
}
