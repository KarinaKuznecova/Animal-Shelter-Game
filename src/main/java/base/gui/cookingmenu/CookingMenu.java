package base.gui.cookingmenu;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.gui.GUI;
import base.gui.GUIButton;

import java.util.List;

import static base.constants.ColorConstant.BROWN;
import static base.constants.ColorConstant.SOFT_PINK;

public class CookingMenu extends GUI {

    private Rectangle backGroundRectangle;
    private int knownCookingSkill;

    public CookingMenu(List<GUIButton> buttons, int x, int y, int width, int height) {
        super(buttons, x, y, true);

        backGroundRectangle = new Rectangle(x, y, width, height);
        backGroundRectangle.generateBorder(2, BROWN, SOFT_PINK);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        super.render(renderer, zoom);
        renderer.renderRectangle(backGroundRectangle, zoom, true);

        for (GUIButton button : buttons) {
            button.render(renderer, zoom, rectangle);
        }
    }

    @Override
    public void update(Game game) {
        int filledButtons = 0;
        for (GUIButton button : buttons) {
            if (button instanceof ItemSlotButton && ((ItemSlotButton) button).getItem() != null) {
                filledButtons++;
            }
        }
        for (GUIButton button : buttons) {
            button.update(game);
            if (button instanceof MealButton) {
                ((MealButton) button).updateColor(filledButtons);
            }
        }
    }

    public void updateCookingSkill(int cookingSkillLevel) {
        if (knownCookingSkill == cookingSkillLevel) {
            return;
        }
        knownCookingSkill = cookingSkillLevel;
        for (GUIButton button : buttons) {
            if (button instanceof MealButton) {
                if (button instanceof TastyMealButton && cookingSkillLevel >= 2) {
                    ((TastyMealButton) button).unlock();
                }
                if (button instanceof PerfectMealButton && cookingSkillLevel >= 3) {
                    ((PerfectMealButton) button).unlock();
                }
            }
        }
    }

    protected void useItems(int count) {
        int used = 0;
        for (GUIButton button : buttons) {
            if (used == count) {
                break;
            }
            if (button instanceof ItemSlotButton && ((ItemSlotButton) button).getItem() != null) {
                ((ItemSlotButton) button).removeItem();
                used++;
            }
        }
    }
}
