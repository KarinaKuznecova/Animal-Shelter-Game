package base.gui.cookingmenu;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import base.gui.GUIButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.*;
import static base.constants.ColorConstant.LIGHT_GRAY;

public abstract class MealButton extends GUIButton {

    protected static final Logger logger = LoggerFactory.getLogger(MealButton.class);

    private boolean unlocked;
    private boolean isGreen;

    public MealButton(Sprite tileSprite, Rectangle rectangle) {
        super(tileSprite, rectangle, true);

        makeYellow();
    }

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public void activate(Game game) {
        logger.info("Simple meal button clicked");
    }

    @Override
    public void render(RenderHandler renderer, int zoom, Rectangle rectangle) {
        if (!unlocked) {
            return;
        }
        renderer.renderRectangle(this.rectangle, rectangle, 1, true);
        if (sprite != null) {
            renderer.renderSprite(sprite, this.rectangle.getX() + rectangle.getX(), this.rectangle.getY() + rectangle.getY(), zoom, true);
        }
    }

    public abstract void updateColor(int itemsInSlots);

    public void unlock() {
        unlocked = true;
    }

    public void makeGreen() {
        rectangle.generateBorder(3, GREEN, LIGHT_BLUE);
        isGreen = true;
    }

    public void makeYellow() {
        rectangle.generateBorder(3, YELLOW, LIGHT_GRAY);
        isGreen = false;
    }

    public boolean isGreen() {
        return isGreen;
    }
}
