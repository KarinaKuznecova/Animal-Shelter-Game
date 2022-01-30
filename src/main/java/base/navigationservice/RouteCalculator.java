package base.navigationservice;

import base.gameobjects.Animal;
import base.gameobjects.FoodBowl;
import base.gameobjects.Item;
import base.gameobjects.WaterBowl;
import base.graphicsservice.Rectangle;
import base.map.GameMap;
import base.map.MapTile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.Constants.ZOOM;
import static base.navigationservice.Direction.*;

public class RouteCalculator {

    protected static final Logger logger = LoggerFactory.getLogger(RouteCalculator.class);

    public static final String WATER = "water";
    public static final String FOOD = "food";

    public Route calculateRouteToFood(GameMap gameMap, Animal animal) {
        return calculateRoute(gameMap, animal, FOOD);
    }

    public Route calculateRouteToWater(GameMap gameMap, Animal animal) {
        return calculateRoute(gameMap, animal, WATER);
    }

    public Route calculateRouteToPortal(GameMap gameMap, Animal animal, String destination) {
        return calculateRoute(gameMap, animal, destination);
    }

    public Route calculateRoute(GameMap gameMap, Animal animal, String destination) {
        Route newRoute = new Route();

        MapTile portal = null;
        if (!isWaterOrFood(destination) && getPortal(gameMap, destination) != null) {
            portal = getPortal(gameMap, destination);
        }
        if (!isWaterOrFood(destination) && portal == null) {
            return newRoute;
        }
        List<Map<Rectangle, Route>> searchQueue = new LinkedList<>();

        fillInitialRoutes(gameMap, searchQueue, animal);

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
                if (!isWaterOrFood(destination)) {
                    found = isTherePortal(portal, rectangleToCheck);
                } else if (FOOD.equals(destination)) {
                    found = isThereFood(gameMap, rectangleToCheck);
                } else if (WATER.equals(destination)) {
                    found = isThereWater(gameMap, rectangleToCheck);
                } else {
                    found = false;
                }
                if (found) {
                    logger.info(String.format("%s found his way to %s!", animal.getAnimalName(), destination));
                    return map.get(rectangleToCheck);
                } else {
                    searched.add(rectangleToCheck);
                    fillSearchQueue(gameMap, searchQueue, rectangleToCheck, map.get(rectangleToCheck), searched);
                }

            } else {
                searchQueue.remove(0);
            }
        }
        if (isWaterOrFood(destination) && newRoute.isEmpty()) {
            newRoute = calculateRouteToPortal(gameMap, animal, NavigationService.getNextPortalToGetToHome(gameMap.getMapName()));
        }
        return newRoute;
    }

    private void fillInitialRoutes(GameMap gameMap, List<Map<Rectangle, Route>> searchQueue, Animal animal) {
        int adjustedX = animal.getCurrentX() - (animal.getCurrentX() % (TILE_SIZE * ZOOM));
        int adjustedY = animal.getCurrentY() - (animal.getCurrentY() % (TILE_SIZE * ZOOM));
        Rectangle initialRectangle = new Rectangle(adjustedX, adjustedY, TILE_SIZE, TILE_SIZE);

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
    }

    private boolean isWaterOrFood(String destination) {
        return WATER.equals(destination) || FOOD.equals(destination);
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
            return new Rectangle(rectangle.getX(), rectangle.getY() - (TILE_SIZE * ZOOM), (TILE_SIZE * ZOOM) - 1, (TILE_SIZE * ZOOM) - 1);
        }
        return null;
    }

    public Rectangle tryDown(GameMap gameMap, Rectangle rectangle) {
        if (rectangle.getY() + (TILE_SIZE * ZOOM) <= gameMap.getMapHeight() * (TILE_SIZE * ZOOM) && canWalkThisDirection(gameMap, DOWN, rectangle.getX(), rectangle.getY())) {
            return new Rectangle(rectangle.getX(), rectangle.getY() + (TILE_SIZE * ZOOM), (TILE_SIZE * ZOOM) - 1, (TILE_SIZE * ZOOM) - 1);
        }
        return null;
    }

    public Rectangle tryLeft(GameMap gameMap, Rectangle rectangle) {
        if (rectangle.getX() >= 0 && canWalkThisDirection(gameMap, LEFT, rectangle.getX(), rectangle.getY())) {
            return new Rectangle(rectangle.getX() - (TILE_SIZE * ZOOM), rectangle.getY(), (TILE_SIZE * ZOOM) - 1, (TILE_SIZE * ZOOM) - 1);
        }
        return null;
    }

    public Rectangle tryRight(GameMap gameMap, Rectangle rectangle) {
        if (rectangle.getX() + (TILE_SIZE * ZOOM) <= gameMap.getMapWidth() * (TILE_SIZE * ZOOM) && canWalkThisDirection(gameMap, RIGHT, rectangle.getX(), rectangle.getY())) {
            return new Rectangle(rectangle.getX() + (TILE_SIZE * ZOOM), rectangle.getY(), (TILE_SIZE * ZOOM) - 1, (TILE_SIZE * ZOOM) - 1);
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

    public boolean isThereWater(GameMap gameMap, Rectangle rectangle) {
        for (WaterBowl waterBowl : gameMap.getWaterBowls()) {
            if (waterBowl.isFull() && waterBowl.getRectangle().intersects(rectangle)) {
                return true;
            }
        }
        return false;
    }

    private boolean canWalkThisDirection(GameMap gameMap, Direction direction, int xPosition, int yPosition) {
        switch (direction) {
            case LEFT:
                xPosition = xPosition - (TILE_SIZE * ZOOM);
                break;
            case RIGHT:
                xPosition = xPosition + (TILE_SIZE * ZOOM);
                break;
            case UP:
                yPosition = yPosition - (TILE_SIZE * ZOOM);
                break;
            case DOWN:
                yPosition = yPosition + (TILE_SIZE * ZOOM);
                break;
        }
        return isWalkable(gameMap, xPosition, yPosition);
    }

    public boolean isWalkable(GameMap gameMap, int x, int y) {
        Rectangle rectangle = new Rectangle(x, y, (TILE_SIZE * ZOOM) - 1, (TILE_SIZE * ZOOM) - 1);

        if (gameMap.isTherePortal(rectangle)) {
            return true;
        } else if (x < 0 || y < 0 || x > gameMap.getMapWidth() * (TILE_SIZE * ZOOM) || y > gameMap.getMapHeight() * (TILE_SIZE * ZOOM)) {
            return false;
        }

        List<MapTile> tilesOnLayer = new ArrayList<>(gameMap.getTilesOnLayer(2));
        if (tilesOnLayer.isEmpty()) {
            return true;
        }
        for (MapTile tile : tilesOnLayer) {
            Rectangle tileRectangle = new Rectangle(tile.getX(), tile.getY(), (TILE_SIZE * ZOOM), (TILE_SIZE * ZOOM));
            if (rectangle.intersects(tileRectangle)) {
                return false;
            }
            int tileX = Math.abs(tile.getX() * (TILE_SIZE * ZOOM));
            int tileY = Math.abs(tile.getY() * (TILE_SIZE * ZOOM));
            if (Math.abs(tileX - x) < (TILE_SIZE * ZOOM) && Math.abs(tileY - y) < (TILE_SIZE * ZOOM)) {
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
