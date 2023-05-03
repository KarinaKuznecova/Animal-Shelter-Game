package base.gameobjects.npc;

import base.Game;
import base.graphicsservice.AnimatedSprite;
import base.gameobjects.GameObject;
import base.gameobjects.Walking;
import base.gameobjects.interactionzones.InteractionZone;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.map.GameMap;
import base.navigationservice.Direction;
import base.navigationservice.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.GREEN;
import static base.constants.Constants.*;
import static base.constants.FilePath.NPC_SHEET_PATH_LADY;
import static base.navigationservice.Direction.DOWN;
import static base.navigationservice.Direction.STAY;

public class Npc implements GameObject, Walking {

    private static final Logger logger = LoggerFactory.getLogger(Npc.class);

    public NpcType type;

    protected final transient AnimatedSprite animatedSprite;
    protected final Rectangle rectangle;

    protected Direction direction;
    protected transient int movingTicks = 0;
    protected transient Route route;
    private String currentMap;
    protected int speed;

    protected transient InteractionZone interactionZone;

    public Npc(int startX, int startY) {
        animatedSprite = getAnimatedSprite();
        logger.info("Loaded npc sprite");
        speed = 1;
        route = new Route();
        direction = DOWN;

        rectangle = new Rectangle(startX, startY, TILE_SIZE, TILE_SIZE);
        rectangle.generateBorder(1, GREEN);
    }

    protected AnimatedSprite getAnimatedSprite() {
       return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_LADY, 64, 3);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        int xForSprite = rectangle.getX() - 32;
        int yForSprite = rectangle.getY() - 48;
        if (animatedSprite != null) {
            renderer.renderSprite(animatedSprite, xForSprite, yForSprite, zoom, false);
        }
        if (DEBUG_MODE) {
            renderer.renderRectangle(rectangle, zoom, false);
            interactionZone.render(renderer, zoom);
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

    protected void handleMoving(GameMap gameMap, Direction direction) {

        switch (direction) {
            case LEFT:
                if (rectangle.getX() > 0 || nearPortal(gameMap.getPortals(), rectangle)) {
                    rectangle.setX(rectangle.getX() - speed);
                }
                break;
            case RIGHT:
                if (rectangle.getX() < (gameMap.getMapWidth() * TILE_SIZE - rectangle.getWidth()) * ZOOM || nearPortal(gameMap.getPortals(), rectangle)) {
                    rectangle.setX(rectangle.getX() + speed);
                }
                break;
            case UP:
                if (rectangle.getY() > 0 || nearPortal(gameMap.getPortals(), rectangle)) {
                    rectangle.setY(rectangle.getY() - speed);
                }
                break;
            case DOWN:
                if (rectangle.getY() < (gameMap.getMapHeight() * TILE_SIZE - rectangle.getHeight()) * ZOOM || nearPortal(gameMap.getPortals(), rectangle)) {
                    rectangle.setY(rectangle.getY() + speed);
                }
                break;
        }
    }

    protected void updateDirection() {
        if (direction == STAY) {
            animatedSprite.setAnimationRange(0, 1);
            return;
        }
        if (animatedSprite != null && direction != null) {
            animatedSprite.setAnimationRange((direction.directionNumber * 8), (direction.directionNumber * 8 + 7)); //if horizontal increase
        }
    }

    /**
     * =================================== GETTERS ======================================
     */

    public String getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(String currentMap) {
        this.currentMap = currentMap;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public InteractionZone getInteractionZone() {
        return interactionZone;
    }

    @Override
    public String toString() {
        return "Npc";
    }


    public void setDirection(Direction direction) {
        this.direction = direction;
        updateDirection();
    }

    public Direction getDirection() {
        return direction;
    }
}
