package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;

import java.io.Serializable;

public interface GameObject extends Serializable {

    //should be called as often as possible
    void render(RenderHandler renderer, int zoom);

    //called 60fps
    void update(Game game);

    int getLayer();

    boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game);

    Rectangle getRectangle();
}
