package base.gui;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackpackButton extends GUIButton {

    private String item;
    private final Game game;
    private boolean isGreen = false;

    protected static final Logger logger = LoggerFactory.getLogger(BackpackButton.class);

    public BackpackButton(Game game, String item, Sprite tileSprite, Rectangle rectangle) {
        super(tileSprite, rectangle, true);
        this.item = item;
        this.sprite = tileSprite;
        this.game = game;
        rectangle.generateGraphics(3, 0xFFDB3D);
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
                        xZoom, yZoom, fixed);
            }
        }
        renderer.renderRectangle(region, rectangle, 1, 1, fixed);
    }

    @Override
    public void update(Game game) {
        if (item.equals(game.getSelectedItem())) {
            if (!isGreen) {
                region.generateGraphics(3,0x67FF3D);
                isGreen = true;
            }
        } else {
            if (isGreen) {
                region.generateGraphics(3,0xFFDB3D);
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
}
