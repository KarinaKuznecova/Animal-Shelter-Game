package base.gui;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import base.map.TileService;

import java.util.List;

import static base.constants.ColorConstant.*;
import static base.constants.MultiOptionalObjects.MASTER_TILE_LIST;

public class SDKButton extends GUIButton {

    Game game;
    int tileID;
    boolean isGreen = false;

    public SDKButton(Game game, int tileID, Sprite tileSprite, Rectangle rectangle) {
        super(tileSprite, rectangle, true);
        this.game = game;
        this.tileID = tileID;
        rectangle.generateBorder(3, BROWN, BLUE);

        checkIfContainsMultipleOptions(tileID);
    }

    private void checkIfContainsMultipleOptions(int tileID) {
        for (List<Integer> list : MASTER_TILE_LIST) {
            if (list.contains(tileID)) {
                multipleOptions = true;
                break;
            }
        }
    }

    @Override
    public void render(RenderHandler renderer, int zoom, Rectangle interfaceRect) {
        renderer.renderRectangle(rectangle, interfaceRect, 1, fixed);
        if (sprite != null) {
            if (multipleOptions) {
                renderer.renderSprite(sprite,
                        rectangle.getX() + interfaceRect.getX(),
                        rectangle.getY() + interfaceRect.getY(), zoom, fixed, "<      >");
            } else if (objectCount > 1) {
                renderer.renderSprite(sprite,
                        rectangle.getX() + interfaceRect.getX(),
                        rectangle.getY() + interfaceRect.getY(), zoom, fixed, objectCount);
            } else {
                renderer.renderSprite(sprite,
                        rectangle.getX() + interfaceRect.getX(),
                        rectangle.getY() + interfaceRect.getY(),
                        zoom,
                        fixed);
            }
        }
    }

    public void activate() {
        int nextId = guiService.getNextId(tileID);
        if (isGreen && nextId != tileID) {
            tileID = nextId;
            sprite = new TileService().getTiles().get(tileID).getSprite();
        }
        game.changeTile(tileID);
    }

    @Override
    public void update(Game game) {
        if (tileID == game.getSelectedTileId()) {
            if (!isGreen) {
                rectangle.generateBorder(5, GREEN, BLUE);
                isGreen = true;
            }
        } else {
            if (isGreen) {
                rectangle.generateBorder(3, BROWN, BLUE);
                isGreen = false;
            }
        }
    }

    @Override
    public int getLayer() {
        return 5;
    }
}