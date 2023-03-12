package base.gameobjects.npc;

import base.Game;
import base.gameobjects.GameObject;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;

import static base.constants.ColorConstant.GREEN;
import static base.constants.Constants.DEBUG_MODE;
import static base.constants.Constants.ZOOM;

public class NpcSpot implements GameObject {

    private final Rectangle rectangle;

    public NpcSpot(Rectangle rectangle) {
        this.rectangle = rectangle;
        if (DEBUG_MODE) {
            rectangle.generateBorder(1, GREEN);
        }
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (DEBUG_MODE) {
            if (rectangle.getPixels().length == 0) {
                rectangle.generateBorder(1, GREEN);
            }
            renderer.renderRectangle(rectangle, ZOOM, false);
        }
    }

    @Override
    public void update(Game game) {

    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        return false;
    }

    /** =================================== GETTERS ====================================== */

    public Rectangle getRectangle() {
        return rectangle;
    }
}
