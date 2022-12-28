package base.gameobjects.interactionzones;

import base.Game;

public class InteractionZoneFoodVendor extends InteractionZone {

    public InteractionZoneFoodVendor(int centerPointX, int centerPointY, int radius) {
        super(centerPointX, centerPointY, radius);
        changePosition(centerPointX, centerPointY);
    }

    @Override
    public void action(Game game) {
        game.getVendorNpc().getShopMenu().switchVisible();
        if (game.getVendorNpc().getShopMenu().isVisible()) {
            game.openBackpack();
            game.openShopMenu(game.getVendorNpc().getShopMenu());
        } else {
            game.closeBackpack();
            game.closeShopMenu(game.getVendorNpc().getShopMenu());
        }
    }
}
