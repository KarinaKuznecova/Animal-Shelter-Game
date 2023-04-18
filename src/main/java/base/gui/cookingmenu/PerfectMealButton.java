package base.gui.cookingmenu;

import base.Game;
import base.gameobjects.PetFood;
import base.graphicsservice.Rectangle;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.LIGHT_GRAY;
import static base.constants.ColorConstant.YELLOW;

public class PerfectMealButton extends MealButton {

    protected static final Logger logger = LoggerFactory.getLogger(PerfectMealButton.class);

    public PerfectMealButton(Sprite tileSprite, Rectangle rectangle) {
        super(tileSprite, rectangle);

        rectangle.generateBorder(3, YELLOW, LIGHT_GRAY);
    }

    @Override
    public void activate(Game game) {
        logger.info("Perfect meal button clicked");
        if (isGreen()) {
            int amount = game.getPlayer().getSkills().getCookingSkill().getPerfectMealAmount();
            game.getItem(PetFood.PERFECT_MEAL, game.getSpriteService().getPerfectMealSprite(), amount);
            game.getCookingMenu().useItems(3);
            if (amount == 1) {
                game.getPlayer().getSkills().getCookingSkill().getExperienceSmall();
            } else {
                game.getPlayer().getSkills().getCookingSkill().getExperienceMedium();
            }
        }
    }

    public void updateColor(int itemsInSlots) {
        if (itemsInSlots >= 3) {
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
