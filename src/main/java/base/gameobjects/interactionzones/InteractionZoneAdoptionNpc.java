package base.gameobjects.interactionzones;

import base.Game;
import base.gameobjects.Animal;

import static base.constants.VisibleText.getAdoptionDialog;

public class InteractionZoneAdoptionNpc extends InteractionZone {

    Animal wantedAnimal;

    public InteractionZoneAdoptionNpc(int centerPointX, int centerPointY, int radius, Animal wantedAnimal) {
        super(centerPointX, centerPointY, radius);
        this.wantedAnimal = wantedAnimal;
    }

    @Override
    public void update(Game game) {
        super.update(game);
        if (!isInRange(game.getPlayer().getPlayerRectangle())) {
            game.hideDialogBox();
        }
    }

    @Override
    public void action(Game game) {
        game.switchDialogBox();
        game.setDialogText(getAdoptionDialog(wantedAnimal));
    }

}
