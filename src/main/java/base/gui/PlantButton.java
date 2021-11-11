package base.gui;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.Sprite;

public class PlantButton extends SDKButton{


    public PlantButton(Game game, int tileID, Sprite tileSprite, Rectangle rectangle) {
        super(game, tileID, tileSprite, rectangle);
    }

    @Override
    public void activate() {
        game.changeSelectedPlant(tileID);
    }

    @Override
    public void update(Game game) {
        if (tileID == game.getSelectedPlant()) {
            if (!isGreen) {
                region.generateGraphics(3,0x67FF3D);
                isGreen = true;
            }
        } else {
            if (isGreen) {
                region.generateGraphics(3,0xFFDB3D);
                isGreen = false;
            }
        }
    }
}
