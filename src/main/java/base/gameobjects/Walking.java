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

import static base.constants.Constants.*;
import static base.constants.MapConstants.CITY_MAP;
import static base.constants.MapConstants.FOREST_MAP;
import static base.navigationservice.Direction.*;

public interface Walking {

    Random random = new Random();
    Logger logger = LoggerFactory.getLogger(Walking.class);

    default boolean nearPortal(List<Portal> portals, Rectangle rectangle) {
        for (Portal portal : portals) {
            if (this instanceof Animal && (portal.getDirection().equalsIgnoreCase(FOREST_MAP) || portal.getDirection().equalsIgnoreCase(CITY_MAP))) {
                // don't check this portal, animal should not use it
                continue;
            }
            logger.debug(String.format("Portal X: %d gameobject X: %d", portal.getRectangle().getX(), rectangle.getX()));
            int diffX = portal.getRectangle().getX() - rectangle.getX();
            logger.debug(String.format("diff x: %d", diffX));
            int diffY = portal.getRectangle().getY() - rectangle.getY();
            logger.debug(String.format("diff y: %d", diffY));
            if (Math.abs(diffX) <= CELL_SIZE && Math.abs(diffY) <= CELL_SIZE) {
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
                xPosition = xPosition - speed;
                break;
            case RIGHT:
                xPosition = xPosition + speed;
                break;
            case UP:
                yPosition = yPosition - speed;
                break;
            case DOWN:
                yPosition = yPosition + speed;
                break;
        }

        if (nearPortal(gameMap.getPortals(), rectangle)) {
            return false;
        }

        if (tilesOnLayer != null) {
            for (MapTile tile : tilesOnLayer) {
                if (rectangle.potentialIntersects(tile, xPosition, yPosition)) {
                    if (tile.isPortal()) {
                        return false;
                    }
                    // TODO
//                    if (gameMap.getWaterCornerTiles().contains(tile.getId())) {
//                        return false;
//                    }
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

    default Portal getPortalTile(Game game, String currentMap, Rectangle rectangle) {
        if (game.getGameMap(currentMap).getPortals() != null) {
            for (Portal portal : game.getGameMap(currentMap).getPortals()) {
                if (rectangle.intersects(portal.getRectangle())) {
                    return portal;
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
