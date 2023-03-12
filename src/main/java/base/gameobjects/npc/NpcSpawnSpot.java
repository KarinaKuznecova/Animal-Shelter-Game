package base.gameobjects.npc;

import base.Game;
import base.gameobjects.GameObject;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;

import static base.constants.ColorConstant.BLUE;
import static base.constants.Constants.DEBUG_MODE;
import static base.constants.Constants.ZOOM;

public class NpcSpawnSpot implements GameObject {

    private final Rectangle rectangle;
    private final Npc assignedNpc;

    public NpcSpawnSpot(Rectangle rectangle, Npc assignedNpc) {
        this.rectangle = rectangle;
        this.assignedNpc = assignedNpc;
        if (DEBUG_MODE) {
            rectangle.generateBorder(1, BLUE);
        }
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (DEBUG_MODE) {
            if (rectangle.getPixels().length == 0) {
                rectangle.generateBorder(1, BLUE);
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
