package base.gui;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

import static base.constants.ColorConstant.GREEN;
import static base.constants.ColorConstant.YELLOW;

public class SDKButton extends GUIButton {

    Game game;
    int tileID;
    boolean isGreen = false;

    public SDKButton(Game game, int tileID, Sprite tileSprite, Rectangle rectangle) {
        super(tileSprite, rectangle, true);
        this.game = game;
        this.tileID = tileID;
        rectangle.generateGraphics(3, YELLOW);
    }

    @Override
    public void render(RenderHandler renderer, int xZoom, int yZoom, Rectangle interfaceRect) {
        if (sprite != null) {
            if (objectCount > 1) {
                renderer.renderSprite(sprite,
                        region.getX() + interfaceRect.getX(),
                        region.getY() + interfaceRect.getY(), xZoom, yZoom, fixed, objectCount);
            } else {
                renderer.renderSprite(sprite,
                        region.getX() + interfaceRect.getX(),
                        region.getY() + interfaceRect.getY(),
                        xZoom,
                        yZoom,
                        fixed);
            }
        }
        renderer.renderRectangle(region, interfaceRect, 1, 1, fixed);
    }

    public void activate() {
        game.changeTile(tileID);
    }

    @Override
    public void update(Game game) {
        if (tileID == game.getSelectedTileId()) {
            if (!isGreen) {
                region.generateGraphics(3, GREEN);
                isGreen = true;
            }
        } else {
            if (isGreen) {
                region.generateGraphics(3, YELLOW);
                isGreen = false;
            }
        }
    }

    @Override
    public int getLayer() {
        return 5;
    }
}