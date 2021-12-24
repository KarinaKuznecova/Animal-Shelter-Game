package base.gui;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static base.constants.ColorConstant.GREEN;
import static base.constants.ColorConstant.YELLOW;

public class BackpackButton extends GUIButton implements Serializable {

    private static final long serialVersionUID = 1L;

    private String item;
    private transient Game game;
    private boolean isGreen = false;
    private final String defaultId;

    protected static final Logger logger = LoggerFactory.getLogger(BackpackButton.class);

    public BackpackButton(String item, Sprite tileSprite, Rectangle rectangle, String defaultId) {
        super(tileSprite, rectangle, true);
        this.item = item;
        this.sprite = tileSprite;
        this.defaultId = defaultId;
        rectangle.generateGraphics(3, YELLOW);
    }

    @Override
    public void render(RenderHandler renderer, int xZoom, int yZoom, Rectangle rectangle) {
        if (sprite != null) {
            if (objectCount > 1) {
                renderer.renderSprite(sprite,
                        region.getX() + rectangle.getX(),
                        region.getY() + rectangle.getY(),
                        xZoom, yZoom, fixed, objectCount);
            } else {
                renderer.renderSprite(sprite,
                        region.getX() + rectangle.getX(),
                        region.getY() + rectangle.getY(),
                        xZoom, yZoom, fixed, 0);
            }
        }
        renderer.renderRectangle(region, rectangle, 1, 1, fixed);
    }

    @Override
    public void update(Game game) {
        if (item.equals(game.getSelectedItem())) {
            if (!isGreen) {
                region.generateGraphics(3, GREEN);
                isGreen = true;
            }
        } else {
            if (isGreen) {
                region.generateGraphics(3, YELLOW);
                isGreen = false;
            }
        }
    }

    @Override
    public int getLayer() {
        return 8;
    }

    @Override
    public void activate() {
        logger.info("backpack button clicked");
        game.changeSelectedItem(item);
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

    public void setGame(Game game) {
        this.game = game;
    }
}
