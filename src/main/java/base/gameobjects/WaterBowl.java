package base.gameobjects;

import base.Game;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Rectangle;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.FilePath.WATER_BOWL_PATH;

public class WaterBowl extends Bowl {

    public WaterBowl(int x, int y) {
        super(x, y);

        sprite = ImageLoader.getAnimatedSprite(WATER_BOWL_PATH, TILE_SIZE);
        sprite.setAnimationRange(0, 1);
        sprite.vertical = false;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom, Game game) {
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
