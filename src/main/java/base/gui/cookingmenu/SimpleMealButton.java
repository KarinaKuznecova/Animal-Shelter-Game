package base.gui.cookingmenu;

import base.Game;
import base.gameobjects.PetFood;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.*;

public class SimpleMealButton extends MealButton {

    protected static final Logger logger = LoggerFactory.getLogger(SimpleMealButton.class);

    public SimpleMealButton(Sprite tileSprite, Rectangle rectangle) {
        super(tileSprite, rectangle);

        rectangle.generateBorder(3, YELLOW, LIGHT_GRAY);
        unlock();
    }

    @Override
    public void activate(Game game) {
        logger.info("Simple meal button clicked");
        if (isGreen()) {
            int amount = game.getPlayer().getSkills().getCookingSkill().getSimpleMealAmount();
            game.getItem(PetFood.SIMPLE_MEAL, game.getSpriteService().getSimpleMealSprite(), amount);
            game.getCookingMenu().useItems(1);
            if (amount == 1) {
                game.getPlayer().getSkills().getCookingSkill().getExperienceMedium(game.getRenderer());
            } else {
                game.getPlayer().getSkills().getCookingSkill().getExperienceLarge(game.getRenderer());
            }
        }
    }

    @Override
    public void render(RenderHandler renderer, int zoom, Rectangle rectangle) {
        renderer.renderRectangle(this.rectangle, rectangle, 1, true);
        if (sprite != null) {
            renderer.renderSprite(sprite, this.rectangle.getX() + rectangle.getX(), this.rectangle.getY() + rectangle.getY(), zoom, true);
        }
    }

    public void updateColor(int itemsInSlots) {
        if (itemsInSlots >= 1) {
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
