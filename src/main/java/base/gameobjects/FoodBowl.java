package base.gameobjects;

import base.Game;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Rectangle;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.FilePath.FOOD_BOWL_PATH;

public class FoodBowl extends Bowl {

    public FoodBowl(int x, int y) {
        super(x, y);

        sprite = ImageLoader.getAnimatedSprite(FOOD_BOWL_PATH, TILE_SIZE);
        sprite.setAnimationRange(0, 1);
        sprite.vertical = false;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (mouseRectangle.intersects(rectangle)) {
            logger.info("Food bowl is clicked");
            if (!isFull && game.getSelectedItem().length() > 2) {
                logger.debug("Will fill food bowl");
                fillBowl();
                game.removeItemFromInventory(game.getSelectedItem());
            }
            return true;
        }
        return false;
    }
}
