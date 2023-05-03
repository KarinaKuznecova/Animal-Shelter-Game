package base.gameobjects;

import base.Game;
import base.graphicsservice.AnimatedSprite;
import base.graphicsservice.Rectangle;

public class WaterBowl extends Bowl {

    public WaterBowl(int x, int y) {
        super(x, y);
    }

    public WaterBowl(int x, int y, boolean isFull) {
        super(x, y, isFull);
    }

    public WaterBowl(int x, int y, AnimatedSprite animatedSprite) {
        super(x, y);
        setSprite(animatedSprite);
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (mouseRectangle.intersects(rectangle)) {
            logger.info("Water bowl is clicked");
            if (!isFull) {
                logger.debug("Will fill water bowl");
                fillBowl();
            }
            return true;
        }
        return false;
    }
}
