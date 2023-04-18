package base.gameobjects.interactionzones;

import base.Game;

public class InteractionZoneKitchen extends InteractionZone {

    public InteractionZoneKitchen(int centerPointX, int centerPointY, int radius) {
        super(centerPointX, centerPointY, radius);
    }

    public void action(Game game) {
        if (!game.isCookingMenuOpen()) {
            game.showCookingMenu();
        } else {
            game.hideCookingMenu();
            game.closeBackpack();
        }
    }
}
