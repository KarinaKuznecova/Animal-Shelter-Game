package base.gameobjects.interactionzones;

import base.Game;

public class InteractionZoneKitchen extends InteractionZone {

    private final String mapName;

    public InteractionZoneKitchen(String mapName, int centerPointX, int centerPointY, int radius) {
        super(centerPointX, centerPointY, radius);
        this.mapName = mapName;
    }

    @Override
    public void action(Game game) {
        if (!game.getGameMap().getMapName().equalsIgnoreCase(mapName)){
            return;
        }
        if (!game.isCookingMenuOpen()) {
            game.showCookingMenu();
        } else {
            game.hideCookingMenu();
            game.closeBackpack();
        }
    }
}
