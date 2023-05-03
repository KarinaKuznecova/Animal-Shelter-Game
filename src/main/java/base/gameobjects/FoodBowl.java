package base.gameobjects;

import base.Game;
import base.graphicsservice.AnimatedSprite;
import base.graphicsservice.Rectangle;

public class FoodBowl extends Bowl {

    private String foodType;

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
            if (!isFull && game.isPetFoodSelected()) {
                logger.debug("Will fill food bowl");
                fillBowl(game.getItemNameByButtonId());
                game.removeItemFromInventory(game.getSelectedItem());
                return true;
            }
        }
        return false;
    }

    public void fillBowl(String item) {
        if (sprite != null) {
            if (PetFood.PERFECT_MEAL.equals(item)) {
                sprite.incrementSprite();
                sprite.incrementSprite();
                sprite.incrementSprite();
                isFull = true;
            } else if (PetFood.TASTY_MEAL.equals(item)) {
                sprite.incrementSprite();
                sprite.incrementSprite();
                isFull = true;
            } else if (PetFood.SIMPLE_MEAL.equals(item)) {
                sprite.incrementSprite();
                isFull = true;
            }
            foodType = item;
        }
    }

    public void emptyBowl() {
        super.emptyBowl();
        foodType = null;
    }

    public String getFoodType() {
        return foodType;
    }
}
