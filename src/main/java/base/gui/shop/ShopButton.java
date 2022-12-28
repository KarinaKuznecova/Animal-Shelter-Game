package base.gui.shop;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import base.gui.GUIButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.BLUE;
import static base.constants.ColorConstant.BROWN;

public class ShopButton extends GUIButton {

    protected static final Logger logger = LoggerFactory.getLogger(ShopButton.class);

    private final String item;
    private final int price;

    public ShopButton(String item, Sprite tileSprite, Rectangle rectangle, int price) {
        super(tileSprite, rectangle, true);
        this.item = item;
        this.price = price;

        rectangle.generateBorder(3, BROWN, BLUE);
    }

    @Override
    public void render(RenderHandler renderer, int zoom, Rectangle rectangle) {
        renderer.renderRectangle(this.rectangle, rectangle, 1, false);
        if (sprite != null) {
            renderer.renderSprite(sprite, this.rectangle.getX() + rectangle.getX(), this.rectangle.getY() + rectangle.getY(), zoom, false, price);
        }
    }

    @Override
    public int getLayer() {
        return 3;
    }

    @Override
    public void activate(Game game) {
        game.getItem(item, sprite);
        game.getBackpackGui().removeCoins(price);
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }
}
