package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.map.GameMap;
import base.map.MapTile;
import base.navigationservice.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.Constants.ZOOM;
import static base.navigationservice.Direction.*;

public interface Walking {

    Random random = new Random();
    Logger logger = LoggerFactory.getLogger(Walking.class);

    default boolean nearPortal(List<MapTile> portals, Rectangle rectangle) {
        for (MapTile portal : portals) {
            logger.debug(String.format("Portal X: %d gameobject X: %d", portal.getX() * (TILE_SIZE * ZOOM), rectangle.getX()));
            int diffX = portal.getX() * (TILE_SIZE * ZOOM) - rectangle.getX();
            logger.debug(String.format("diff x: %d", diffX));
            int diffY = portal.getY() * (TILE_SIZE * ZOOM) - rectangle.getY();
            logger.debug(String.format("diff y: %d", diffY));
            if (Math.abs(diffX) <= TILE_SIZE * ZOOM && Math.abs(diffY) <= TILE_SIZE * ZOOM) {
                return true;
            }
        }
        return false;
    }

    default boolean unwalkableInThisDirection(GameMap gameMap, Direction direction, Rectangle rectangle, int speed, int layer) {
        int xPosition = rectangle.getX();
        int yPosition = rectangle.getY();

        List<MapTile> tilesOnLayer = gameMap.getTilesOnLayer(layer);

        switch (direction) {
            case LEFT:
                xPosition = xPosition - (speed + 1);
                break;
            case RIGHT:
                xPosition = xPosition + (speed + 1);
                break;
            case UP:
                yPosition = yPosition - (speed + 1);
                break;
            case DOWN:
                yPosition = yPosition + (speed + 1);
                break;
        }
        if (tilesOnLayer != null) {
            for (MapTile tile : tilesOnLayer) {
                if (rectangle.potentialIntersects(tile, xPosition, yPosition)) {
                    return true;
                }
            }
        }
        return false;
    }


    default void handleUnwalkable(Rectangle rectangle, Direction direction, int speed) {
        switch (direction) {
            case LEFT:
                rectangle.setX(rectangle.getX() + speed);
                break;
            case RIGHT:
                rectangle.setX(rectangle.getX() - speed);
                break;
            case UP:
                rectangle.setY(rectangle.getY() + speed);
                break;
            case DOWN:
                rectangle.setY(rectangle.getY() - speed);
                break;
        }
    }

    default Direction getRandomDirection() {
        int result = random.nextInt(5);
        switch (result) {
            case 0:
                return DOWN;
            case 1:
                return LEFT;
            case 2:
                return UP;
            case 3:
                return RIGHT;
            default:
                return STAY;
        }
    }

    default int getRandomMovingTicks() {
        return random.nextInt(20) + 64;
    }

    default MapTile getPortalTile(Game game, String currentMap, Rectangle rectangle) {
        if (game.getGameMap(currentMap).getPortals() != null) {
            for (MapTile tile : game.getGameMap(currentMap).getPortals()) {
                if (rectangle.intersects(tile)) {
                    return tile;
                }
            }
        }
        return null;
    }

    default void teleportTo(Rectangle rectangle, int x, int y) {
        rectangle.setX(x);
        rectangle.setY(y);
    }
}
