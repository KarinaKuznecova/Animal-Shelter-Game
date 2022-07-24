package base.gui;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static base.constants.ColorConstant.*;

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
        rectangle.generateBorder(3, BROWN, BLUE);
    }

    @Override
    public void render(RenderHandler renderer, int zoom, Rectangle rectangle) {
        renderer.renderRectangle(region, rectangle, 1, fixed);
        if (sprite != null) {
            if (objectCount > 1) {
                renderer.renderSprite(sprite,
                        region.getX() + rectangle.getX(),
                        region.getY() + rectangle.getY(),
                        zoom, fixed, objectCount);
            } else {
                renderer.renderSprite(sprite,
                        region.getX() + rectangle.getX(),
                        region.getY() + rectangle.getY(),
                        zoom, fixed, 0);
            }
        }
    }

    @Override
    public void update(Game game) {
        if (defaultId.equals(game.getSelectedItem())) {
            if (!isGreen) {
                region.generateBorder(5, GREEN, BLUE);
                isGreen = true;
            }
        } else {
            if (isGreen) {
                region.generateBorder(3, BROWN, BLUE);
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
        game.changeSelectedItem(defaultId);
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
