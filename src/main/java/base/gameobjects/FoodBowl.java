package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;

public class FoodBowl extends Bowl {

    public FoodBowl(int x, int y) {
        super(x, y);
    }

    public FoodBowl(int x, int y, boolean isFull) {
        super(x, y, isFull);
    }

    public FoodBowl(int x, int y, AnimatedSprite animatedSprite) {
        super(x, y);
        setSprite(animatedSprite);
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (mouseRectangle.intersects(rectangle)) {
            logger.info("Food bowl is clicked");
            if (!isFull && game.isFoodSelected()) {
                logger.debug("Will fill food bowl");
                fillBowl(game.getSelectedItem());
                game.removeItemFromInventory(game.getSelectedItem());
            }
            return true;
        }
        return false;
    }

    public void fillBowl(String item) {
        if (sprite != null) {
            if (item.equals("")) {
                sprite.incrementSprite();
                sprite.incrementSprite();
                sprite.incrementSprite();
            } else if (item.equals("")) {
                sprite.incrementSprite();
                sprite.incrementSprite();
            }
            sprite.incrementSprite();
        }
        isFull = true;
    }
}
