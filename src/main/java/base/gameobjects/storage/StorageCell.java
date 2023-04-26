package base.gameobjects.storage;

import base.Game;
import base.graphicsservice.Position;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import base.gui.BackpackButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.BLUE;
import static base.constants.ColorConstant.BROWN;
import static base.constants.Constants.*;
import static base.constants.Constants.TILE_SIZE;
import static base.gameobjects.storage.Storage.BORDER_SIZE;

public class StorageCell extends BackpackButton {

    protected static final Logger logger = LoggerFactory.getLogger(StorageCell.class);

    public StorageCell(Rectangle rectangle, String item, Sprite itemSprite, String defaultId) {
        super(item, itemSprite, rectangle, defaultId);
        setObjectCount(objectCount);
    }

    public void render(RenderHandler renderer) {
        if (rectangle.getPixels().length == 0) {
            rectangle.generateBorder(BORDER_SIZE, BROWN, BLUE);
        }
        renderer.renderRectangle(rectangle, 1, false);
        if (sprite != null) {
            if (objectCount > 1) {
                renderer.renderStorageSprite(sprite,
                        rectangle.getX(),
                        rectangle.getY(),
                        ZOOM, false, objectCount);
            } else {
                renderer.renderStorageSprite(sprite,
                        rectangle.getX(),
                        rectangle.getY(),
                        ZOOM, false, 0);
            }
        } else {
            removeRenderedText(renderer);
        }
    }

    public void removeRenderedText(RenderHandler renderer) {
        int xPosition = rectangle.getX();
        int yPosition = rectangle.getY();
        Position numberPosition = new Position(xPosition + (TILE_SIZE * ZOOM) - 25, yPosition + (TILE_SIZE * ZOOM) - 5);
        renderer.removeTextFromPosition(numberPosition);
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        Rectangle fixedRectangle = new Rectangle(rectangle.getX() - 30, rectangle.getY(), rectangle.getWidth(), rectangle.getHeight() - 25);
        if (mouseRectangle.intersects(fixedRectangle)) {
            activate(game);
            return true;
        }
        return false;
    }

    public void activate(Game game) {
        String selectedItem = game.getItemNameByButtonId();
        if (isButtonEmpty() && selectedItem != null && !selectedItem.isEmpty()) {
            this.item = game.getBackpackService().getItemFromHand().getItemName();
            this.objectCount = game.getBackpackService().getItemFromHand().getObjectCount();
            this.sprite = game.getBackpackService().getItemFromHand().getSprite();
            BackpackButton currentlySelectedButton = game.getSelectedButton();
            game.getBackpackService().removeAllItemsFromButton(currentlySelectedButton);
            game.getRenderer().clearRenderedText();
        }
        if (selectedItem != null && !selectedItem.isEmpty()) {
            BackpackButton currentlySelectedButton = game.getSelectedButton();
            if (selectedItem.equals(item) && currentlySelectedButton != null && !defaultId.equals(currentlySelectedButton.getDefaultId())) {
                if (currentlySelectedButton.getObjectCount() == INVENTORY_LIMIT) {
                    // do nothing
                } else if (currentlySelectedButton.getObjectCount() + objectCount <= INVENTORY_LIMIT) {
                    objectCount += currentlySelectedButton.getObjectCount();

                    game.getBackpackService().removeAllItemsFromButton(currentlySelectedButton);
                    game.getRenderer().clearRenderedText();
                } else {
                    currentlySelectedButton.setObjectCount(currentlySelectedButton.getObjectCount() - (INVENTORY_LIMIT - objectCount));
                    objectCount = INVENTORY_LIMIT;
                }
            }
        }
        logger.info("storage cell button clicked");
        game.changeSelectedItem(defaultId, this);
    }

    @Override
    public void update(Game game) {
        super.update(game);
        if (this.sprite == null && this.getItemName() != null && this.getObjectCount() > 0) {
            this.sprite = game.getSpriteService().getItemSprite(item, game.getTileService());
        }
    }
}
