package base.gameobjects.tree;

import base.Game;
import base.gameobjects.GameObject;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.GREEN;
import static base.constants.Constants.DEBUG_MODE;

public class Oak implements Tree, GameObject {

    private static final Logger logger = LoggerFactory.getLogger(Oak.class);

    private final int x;
    private final int y;
    private transient Sprite sprite;
    private final Rectangle originalRectangle;
    private final Rectangle rectangle;

    public Oak(int x, int y) {
        this.x = x;
        this.y = y;

        originalRectangle = new Rectangle(x, y, 72, 65);
        rectangle = new Rectangle(x + 26, y + 78, 72, 65);
        rectangle.generateBorder(1, GREEN);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (sprite != null) {
            renderer.renderSprite(sprite, x - 32, y - 98, zoom, false);
        }
        if (DEBUG_MODE) {
            renderer.renderRectangle(rectangle, 1, false);
        }
    }

    @Override
    public void update(Game game) {

    }

    @Override
    public int getLayer() {
        return 3;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        return false;
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }

    @Override
    public Rectangle getOriginalRectangle() {
        return originalRectangle;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }


    @Override
    public TreeType getTreeType() {
        return TreeType.OAK;
    }
}
