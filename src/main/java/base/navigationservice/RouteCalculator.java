package base.navigationservice;

import base.gameobjects.Animal;
import base.gameobjects.FoodBowl;
import base.gameobjects.Item;
import base.graphicsservice.Rectangle;
import base.map.GameMap;
import base.map.MapTile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static base.navigationservice.Direction.*;

public class RouteCalculator {

    protected static final Logger logger = LoggerFactory.getLogger(RouteCalculator.class);

    public Route calculateRouteToFood(GameMap gameMap, Animal animal) {
        return calculateRoute(gameMap, animal, null);
    }

    public Route calculateRouteToPortal(GameMap gameMap, Animal animal, String destination) {
        return calculateRoute(gameMap, animal, destination);
    }

    public Route calculateRoute(GameMap gameMap, Animal animal, String destination) {
        Route newRoute = new Route();

        MapTile portal = null;
        if (destination != null && getPortal(gameMap, destination) != null) {
            portal = getPortal(gameMap, destination);
        }
        if (destination != null && portal == null) {
            return newRoute;
        }
        List<Map<Rectangle, Route>> searchQueue = new LinkedList<>();

        int adjustedX = animal.getCurrentX() - (animal.getCurrentX() % 64);
        int adjustedY = animal.getCurrentY() - (animal.getCurrentY() % 64);
        Rectangle initialRectangle = new Rectangle(adjustedX, adjustedY, 32, 32);

        Route potentialRoute = new Route();
        if (tryDown(gameMap, initialRectangle) != null) {
            Map<Rectangle, Route> map = new HashMap<>();
            map.put(tryDown(gameMap, initialRectangle), potentialRoute);
            potentialRoute.addStep(DOWN);
            searchQueue.add(map);
        }

        Route potentialRoute3 = new Route();
        if (tryLeft(gameMap, initialRectangle) != null) {
            Map<Rectangle, Route> map = new HashMap<>();
            map.put(tryLeft(gameMap, initialRectangle), potentialRoute3);
            potentialRoute3.addStep(LEFT);
            searchQueue.add(map);
        }

        Route potentialRoute2 = new Route();
        if (tryUp(gameMap, initialRectangle) != null) {
            Map<Rectangle, Route> map = new HashMap<>();
            map.put(tryUp(gameMap, initialRectangle), potentialRoute2);
            potentialRoute2.addStep(UP);
            searchQueue.add(map);
        }

        Route potentialRoute4 = new Route();
        if (tryRight(gameMap, initialRectangle) != null) {
            Map<Rectangle, Route> map = new HashMap<>();
            map.put(tryRight(gameMap, initialRectangle), potentialRoute4);
            potentialRoute4.addStep(RIGHT);
            searchQueue.add(map);
        }

        List<Rectangle> searched = new ArrayList<>();
        while (!searchQueue.isEmpty()) {
            Map<Rectangle, Route> map = searchQueue.get(0);
            Rectangle rectangleToCheck = null;
            for (Rectangle rectangle : map.keySet()) {
                rectangleToCheck = rectangle;
            }
            if (!searched.contains(rectangleToCheck)) {
                searchQueue.remove(0);
                boolean found;
                if (destination != null) {
                    found = isTherePortal(portal, rectangleToCheck);
                } else {
                    found = isThereFood(gameMap, rectangleToCheck);
                }
                if (found) {
                    logger.info(String.format("%s found his way!", animal.getAnimalName()));
                    return map.get(rectangleToCheck);
                } else {
                    searched.add(rectangleToCheck);
                    fillSearchQueue(gameMap, searchQueue, rectangleToCheck, map.get(rectangleToCheck), searched);
                }

            } else {
                searchQueue.remove(0);
            }
        }
        if (destination == null && newRoute.isEmpty()) {
            newRoute = calculateRouteToPortal(gameMap, animal, NavigationService.getNextMapToGetToHome(gameMap.getMapName()));
        }
        return newRoute;
    }

    public void fillSearchQueue(GameMap gameMap, List<Map<Rectangle, Route>> searchQueue, Rectangle rectangleChecked, Route potentialRoute, List<Rectangle> searched) {
        if (tryDown(gameMap, rectangleChecked) != null) {
            Rectangle nextStep = tryDown(gameMap, rectangleChecked);
            if (!searched.contains(nextStep)) {
                Map<Rectangle, Route> map = new HashMap<>();
                Route newRoute = new Route();
                for (Direction direction : potentialRoute.getAllSteps()) {
                    newRoute.addStep(direction);
                }
                newRoute.addStep(DOWN);
                map.put(nextStep, newRoute);
                searchQueue.add(map);
            }
        }

        if (tryUp(gameMap, rectangleChecked) != null) {
            Rectangle nextStep = tryUp(gameMap, rectangleChecked);
            if (!searched.contains(nextStep)) {
                Map<Rectangle, Route> map = new HashMap<>();
                Route newRoute = new Route();
                for (Direction direction : potentialRoute.getAllSteps()) {
                    newRoute.addStep(direction);
                }
                newRoute.addStep(UP);
                map.put(nextStep, newRoute);
                searchQueue.add(map);
            }
        }

        if (tryLeft(gameMap, rectangleChecked) != null) {
            Rectangle nextStep = tryLeft(gameMap, rectangleChecked);
            if (!searched.contains(nextStep)) {
                Map<Rectangle, Route> map = new HashMap<>();
                Route newRoute = new Route();
                for (Direction direction : potentialRoute.getAllSteps()) {
                    newRoute.addStep(direction);
                }
                newRoute.addStep(LEFT);
                map.put(nextStep, newRoute);
                searchQueue.add(map);
            }
        }

        if (tryRight(gameMap, rectangleChecked) != null) {
            Rectangle nextStep = tryRight(gameMap, rectangleChecked);
            if (!searched.contains(nextStep)) {
                Map<Rectangle, Route> map = new HashMap<>();
                Route newRoute = new Route();
                for (Direction direction : potentialRoute.getAllSteps()) {
                    newRoute.addStep(direction);
                }
                newRoute.addStep(RIGHT);
                map.put(nextStep, newRoute);
                searchQueue.add(map);
            }
        }
    }

    public Rectangle tryUp(GameMap gameMap, Rectangle rectangle) {
        if (rectangle.getY() >= 0 && canWalkThisDirection(gameMap, UP, rectangle.getX(), rectangle.getY())) {
            return new Rectangle(rectangle.getX(), rectangle.getY() - 64, 63, 63);
        }
        return null;
    }

    public Rectangle tryDown(GameMap gameMap, Rectangle rectangle) {
        if (rectangle.getY() + 64 <= gameMap.getMapHeight() * 64 && canWalkThisDirection(gameMap, DOWN, rectangle.getX(), rectangle.getY())) {
            return new Rectangle(rectangle.getX(), rectangle.getY() + 64, 63, 63);
        }
        return null;
    }

    public Rectangle tryLeft(GameMap gameMap, Rectangle rectangle) {
        if (rectangle.getX() >= 0 && canWalkThisDirection(gameMap, LEFT, rectangle.getX(), rectangle.getY())) {
            return new Rectangle(rectangle.getX() - 64, rectangle.getY(), 63, 63);
        }
        return null;
    }

    public Rectangle tryRight(GameMap gameMap, Rectangle rectangle) {
        if (rectangle.getX() + 64 <= gameMap.getMapWidth() * 64 && canWalkThisDirection(gameMap, RIGHT, rectangle.getX(), rectangle.getY())) {
            return new Rectangle(rectangle.getX() + 64, rectangle.getY(), 63, 63);
        }
        return null;
    }

    public boolean isThereFood(GameMap gameMap, Rectangle rectangle) {
        for (FoodBowl foodBowl : gameMap.getFoodBowls()) {
            if (foodBowl.isFull() && foodBowl.getRectangle().intersects(rectangle)) {
                return true;
            }
        }
        for (Item item : gameMap.getItems()) {
            if (item.getRectangle().intersects(rectangle)) {
                return true;
            }
        }
        return false;
    }

    private boolean canWalkThisDirection(GameMap gameMap, Direction direction, int xPosition, int yPosition) {
        switch (direction) {
            case LEFT:
                xPosition = xPosition - 63;
                break;
            case RIGHT:
                xPosition = xPosition + 63;
                break;
            case UP:
                yPosition = yPosition - 63;
                break;
            case DOWN:
                yPosition = yPosition + 63;
                break;
        }
        return isWalkable(gameMap, xPosition, yPosition);
    }

    public boolean isWalkable(GameMap gameMap, int x, int y) {
        Rectangle rectangle = new Rectangle(x, y, 64, 64);

        if (gameMap.isTherePortal(rectangle)) {
            return true;
        }

        List<MapTile> tilesOnLayer = new ArrayList<>(gameMap.getTilesOnLayer(2));
        if (tilesOnLayer.isEmpty()) {
            return true;
        }
        for (MapTile tile : tilesOnLayer) {
            Rectangle tileRectangle = new Rectangle(tile.getX(), tile.getY(), 64, 64);
            if (rectangle.intersects(tileRectangle)) {
                return false;
            }
            int tileX = Math.abs(tile.getX() * 64);
            int tileY = Math.abs(tile.getY() * 64);
            if (Math.abs(tileX - x) < 64 && Math.abs(tileY - y) < 64) {
                return false;
            }
        }
        return true;
    }

    private MapTile getPortal(GameMap gameMap, String destination) {
        for (MapTile portal : gameMap.getPortals()) {
            if (destination.equalsIgnoreCase(portal.getPortalDirection())) {
                return portal;
            }
        }
        return null;
    }

    private boolean isTherePortal(MapTile portal, Rectangle rectangle) {
        return rectangle.intersects(portal);
    }
}
