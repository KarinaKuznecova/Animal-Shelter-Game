package base.gui.shop;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import base.gui.GUIButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.*;

public class ShopButton extends GUIButton {

    protected static final Logger logger = LoggerFactory.getLogger(ShopButton.class);

    private final String item;
    private final int price;

    private int highlightTimer = 0;

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
        if (highlightTimer > 0) {
            highlightTimer--;
            if (highlightTimer == 0) {
                this.rectangle.generateBorder(3, BROWN, BLUE);
            }
        }
    }

    @Override
    public int getLayer() {
        return 3;
    }

    @Override
    public void activate(Game game) {
        highlightTimer = 30;
        rectangle.generateBorder(3, YELLOW, BLUE);
        game.getItem(item, sprite, 1);
        game.getBackpackGui().removeCoins(price);
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }
}
