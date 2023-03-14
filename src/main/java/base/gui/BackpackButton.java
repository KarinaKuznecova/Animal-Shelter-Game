package base.gui;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static base.constants.ColorConstant.*;
import static base.constants.Constants.INVENTORY_LIMIT;

public class BackpackButton extends GUIButton implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String item;
    private transient boolean isGreen = false;
    protected final String defaultId;

    protected static final Logger logger = LoggerFactory.getLogger(BackpackButton.class);

    public BackpackButton(String item, Sprite tileSprite, Rectangle rectangle, String defaultId) {
        super(tileSprite, rectangle, true);
        this.item = item;
        this.defaultId = defaultId;
        rectangle.generateBorder(3, BROWN, BLUE);
    }

    @Override
    public void render(RenderHandler renderer, int zoom, Rectangle rectangle) {
        renderer.renderRectangle(this.rectangle, rectangle, 1, fixed);
        if (sprite != null) {
            if (objectCount > 1) {
                renderer.renderSprite(sprite,
                        this.rectangle.getX() + rectangle.getX(),
                        this.rectangle.getY() + rectangle.getY(),
                        zoom, fixed, objectCount);
            } else {
                renderer.renderSprite(sprite,
                        this.rectangle.getX() + rectangle.getX(),
                        this.rectangle.getY() + rectangle.getY(),
                        zoom, fixed, 0);
            }
        }
    }

    @Override
    public void update(Game game) {
        if (defaultId.equals(game.getSelectedItem())) {
            if (!isGreen) {
                rectangle.generateBorder(5, GREEN, BLUE);
                isGreen = true;
            }
        } else {
            if (isGreen) {
                rectangle.generateBorder(3, BROWN, BLUE);
                isGreen = false;
            }
        }
    }

    @Override
    public int getLayer() {
        return 8;
    }

    @Override
    public void activate(Game game) {
        String selectedItem = game.getItemNameByButtonId();
        if (game.getVendorNpc() != null && game.getVendorNpc().getShopMenu().isVisible()) {
            sellItem(game);
            return;
        }
        if (selectedItem != null && !selectedItem.isEmpty()) {
            BackpackButton currentlySelectedButton = game.getSelectedButton();
            if (isButtonEmpty()) {
                this.item = game.getBackpackService().getItemFromHand().getItemName();
                this.objectCount = game.getBackpackService().getItemFromHand().getObjectCount();
                this.sprite = game.getBackpackService().getItemFromHand().getSprite();

                game.getBackpackService().removeAllItemsFromButton(currentlySelectedButton);
                game.getRenderer().clearRenderedText();
            } else if (selectedItem.equals(item) && !defaultId.equals(currentlySelectedButton.getDefaultId())) {
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
        logger.info("backpack button clicked");
        game.changeSelectedItem(defaultId, this);
    }

    private void sellItem(Game game) {
        objectCount--;
        game.getBackpackGui().addCoins(game.getShopService().getItemPrice(item));
        if (objectCount == 0) {
            makeEmpty();
        }
    }

    public boolean isButtonEmpty() {
        return defaultId.equals(item);
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItemName() {
        return item;
    }

    public void makeEmpty() {
        setItem(defaultId);
        sprite = null;
        objectCount = 0;
    }

    public String getDefaultId() {
        return defaultId;
    }
}
