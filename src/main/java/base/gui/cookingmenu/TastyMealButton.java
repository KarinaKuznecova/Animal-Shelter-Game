package base.gui.cookingmenu;

import base.Game;
import base.gameobjects.PetFood;
import base.graphicsservice.Rectangle;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.*;

public class TastyMealButton extends MealButton {

    protected static final Logger logger = LoggerFactory.getLogger(TastyMealButton.class);

    public TastyMealButton(Sprite tileSprite, Rectangle rectangle) {
        super(tileSprite, rectangle);

        rectangle.generateBorder(3, YELLOW, LIGHT_GRAY);
    }

    @Override
    public void activate(Game game) {
        logger.info("Tasty meal button clicked");
        if (isGreen()) {
            int amount = game.getPlayer().getSkills().getCookingSkill().getTastyMealAmount();
            game.getItem(PetFood.TASTY_MEAL, game.getSpriteService().getTastyMealSprite(), amount);
            game.getCookingMenu().useItems(2);
            if (amount == 1) {
                game.getPlayer().getSkills().getCookingSkill().getExperienceSmall();
            } else {
                game.getPlayer().getSkills().getCookingSkill().getExperienceMedium();
            }
        }
    }

    public void updateColor(int itemsInSlots) {
        if (itemsInSlots >= 2) {
            if (!isGreen()) {
                makeGreen();
            }
        } else {
            if (isGreen()) {
                makeYellow();
            }
        }
    }
}
