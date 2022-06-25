package base.gui;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.ZOOM;

public class OkButton extends GUIButton {

    protected static final Logger logger = LoggerFactory.getLogger(OkButton.class);

    private final Rectangle buttonRegion;

    protected OkButton(Sprite sprite, Rectangle region) {
        super(sprite, region, true);
        buttonRegion = new Rectangle();
        buttonRegion.setX(region.getX() + (region.getWidth() * ZOOM) - 100);
        buttonRegion.setY(region.getY() + 60);
        buttonRegion.setHeight(sprite.getHeight());
        buttonRegion.setWidth(sprite.getWidth());
    }

    @Override
    public void render(RenderHandler renderer, int zoom, Rectangle interfaceRect) {
        renderer.renderSprite(sprite, buttonRegion.getX(), buttonRegion.getY(), 1, fixed, 0);
    }

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (mouseRectangle.intersects(buttonRegion)) {
            activate();
            game.hideDialogBox();
            return true;
        }
        return false;
    }

    @Override
    public void activate() {
        logger.info("OK button clicked");
    }
}
