package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;

import static base.constants.ColorConstant.GREEN;
import static base.constants.Constants.DEBUG_MODE;
import static base.constants.Constants.ZOOM;

public class Portal implements GameObject {

    private final String direction;
    private final Rectangle rectangle;

    public Portal (Rectangle rectangle, String direction) {
        this.rectangle = rectangle;
        this.direction = direction;
        if (DEBUG_MODE) {
            rectangle.generateBorder(1, GREEN);
        }
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (DEBUG_MODE) {
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

    public String getDirection() {
        return direction;
    }
}
