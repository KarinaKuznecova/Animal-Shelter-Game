package base.gui;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackpackButton extends SDKButton {

    protected static final Logger logger = LoggerFactory.getLogger(BackpackButton.class);

    public BackpackButton(Game game, int tileID, Sprite tileSprite, Rectangle rectangle) {
        super(game, tileID, tileSprite, rectangle);
    }

    @Override
    public int getLayer() {
        return 8;
    }

    @Override
    public void activate() {
        logger.info("backpack button clicked");
    }
}
