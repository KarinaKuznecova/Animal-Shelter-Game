package base.gui.cookingmenu;

import base.Game;
import base.gameobjects.services.PlantService;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import base.gui.GUIButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.*;

public class ItemSlotButton extends GUIButton {

    protected static final Logger logger = LoggerFactory.getLogger(ItemSlotButton.class);

    private String item;

    public ItemSlotButton(Sprite tileSprite, Rectangle rectangle) {
        super(tileSprite, rectangle, true);

        rectangle.generateBorder(3, YELLOW, LIGHT_GRAY);
    }

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public void activate(Game game) {
        logger.info("Item button clicked");
        if (!game.getSelectedItem().isEmpty() && this.item == null) {
            putItemInCookingSlot(game);
        } else if (!game.getSelectedItem().isEmpty() && this.item != null) {
            changeItemInCookingSlot(game);
        } else if (game.getSelectedItem().isEmpty() && this.item != null) {
            putItemBackInInventory(game);
        }
    }

    private void changeItemInCookingSlot(Game game) {
        putItemBackInInventory(game);
        putItemInCookingSlot(game);
    }

    private void putItemBackInInventory(Game game) {
        game.getItem(item, game.getSpriteService().getPlantPreviewSprite(item), 1);
        removeItem();
    }

    private void putItemInCookingSlot(Game game) {
        String selectedItem = game.getItemNameByButtonId();
        if (CookingMenu.foodTypes.contains(selectedItem)) {
            this.item = selectedItem;
            sprite = game.getSpriteService().getPlantPreviewSprite(item);
            game.removeItemFromInventory(game.getSelectedItem());
            rectangle.generateBorder(3, GREEN, LIGHT_BLUE);
        }
    }

    @Override
    public void render(RenderHandler renderer, int zoom, Rectangle rectangle) {
        renderer.renderRectangle(this.rectangle, rectangle, 1, true);
        if (sprite != null) {
            renderer.renderSprite(sprite, this.rectangle.getX() + rectangle.getX(), this.rectangle.getY() + rectangle.getY(), zoom, true);
        }
    }

    public String getItem() {
        return item;
    }

    public void removeItem() {
        this.item = null;
        this.sprite = null;
        rectangle.generateBorder(3, YELLOW, LIGHT_GRAY);
    }
}
