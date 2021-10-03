package base.gui;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

public class SDKButton extends GUIButton {

    private Game game;
    private int tileID;
    private boolean isGreen = false;

    public SDKButton(Game game, int tileID, Sprite tileSprite, Rectangle rectangle) {
        super(tileSprite, rectangle, true);
        this.game = game;
        this.tileID = tileID;
        rectangle.generateGraphics(3, 0xFFDB3D);
    }

    @Override
    public void render(RenderHandler renderer, int xZoom, int yZoom, Rectangle interfaceRect) {
        if (sprite != null) {
            renderer.renderSprite(sprite,
                    region.getX() + interfaceRect.getX(),
                    region.getY() + interfaceRect.getY(),
                    xZoom,
                    yZoom,
                    fixed);
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

    @Override
    public int getLayer() {
        return 5;
    }
}