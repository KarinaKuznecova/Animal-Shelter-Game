package base.gameobjects;

import base.Game;
import base.gameobjects.interactionzones.InteractionZoneAdoptionNpc;
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
import static base.constants.MapConstants.MAIN_MAP;
import static base.navigationservice.Direction.*;

public class Npc implements GameObject, Walking {

    private static final Logger logger = LoggerFactory.getLogger(Npc.class);

    private final AnimatedSprite animatedSprite;
    private final Rectangle rectangle;

    private Direction direction;
    private int movingTicks = 0;
    private transient Route route;
    private String currentMap;
    private int speed;

    private boolean arrived;
    private boolean isGoingAway;

    private final InteractionZoneAdoptionNpc interactionZone;
    private Animal wantedAnimal;

    public Npc(int startX, int startY, Animal wantedAnimal) {
        animatedSprite = ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_LADY, 64);
        logger.info("Loaded npc sprite");
        speed = 2;
        route = new Route();
        direction = DOWN;

        rectangle = new Rectangle(startX, startY, 32, 32);
        rectangle.generateBorder(1, GREEN);

        this.wantedAnimal = wantedAnimal;
        interactionZone = new InteractionZoneAdoptionNpc(rectangle.getX() + 32, rectangle.getY() + 32, 100, wantedAnimal);
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
        boolean isMoving = false;
        Direction nextDirection = direction;

        if (route.isEmpty() && !arrived) {
            if (rectangle.intersects(game.getNpcSpot().getRectangle())) {
                arrived = true;
            } else {
                route = game.calculateRouteToNpcSpot(this);
                route.addStep(LEFT);
            }
        }

        if (movingTicks < 1 && !route.isEmpty()) {
            nextDirection = route.getNextStep();
            movingTicks = 64 / speed;
            logger.debug(String.format("Direction: %s, moving ticks: %d", direction.name(), movingTicks));
        }

        if (route.isEmpty() && !isGoingAway) {
            nextDirection = STAY;
        }

        handleMoving(game.getGameMap(MAIN_MAP), nextDirection);
        if (nextDirection != STAY) {
            isMoving = true;
        }

        if (nextDirection != direction) {
            direction = nextDirection;
            updateDirection();
        }
        if (animatedSprite != null) {
            if (isMoving) {
                animatedSprite.update(game);
                interactionZone.changePosition(rectangle.getX() + 32, rectangle.getY() + 32);
            } else {
                animatedSprite.reset();
            }
        }

        interactionZone.update(game);
        movingTicks--;

        if (isGoingAway && route.isEmpty()) {
            game.removeNpc(this);
        }
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        return false;
    }

    private void handleMoving(GameMap gameMap, Direction direction) {

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

    private void updateDirection() {
        if (direction == STAY) {
            animatedSprite.setAnimationRange(0, 1);
            return;
        }
        if (animatedSprite != null && direction != null) {
            animatedSprite.setAnimationRange((direction.directionNumber * 8), (direction.directionNumber * 8 + 7)); //if horizontal increase
        }
    }

    public void goAway(Route route) {
        isGoingAway = true;
        this.route = route;
        logger.info("SENDING NPC AWAY");
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

    public InteractionZoneAdoptionNpc getInteractionZone() {
        return interactionZone;
    }

    public Animal getWantedAnimal() {
        return wantedAnimal;
    }

    @Override
    public String toString() {
        return "Npc";
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        logger.info(String.format("changing npc speed from %d to %d", this.speed, speed));
        this.speed = speed;
    }
}
