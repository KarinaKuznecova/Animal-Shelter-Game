package base.navigationservice;

import base.gameobjects.*;
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
    public static final String PILLOW = "pillow";
    public static final String NPC_SPOT = "npc-spot";
    public static final String NPC = "npc";
    public static final String CITY = "city";

    // TODO: refactor and reduce complexity
    public Route calculateRoute(GameMap currentMap, Animal animal, String destination) {
        Route newRoute = new Route();
        logger.debug(String.format("%s is looking for a way to : %s", animal, destination));
        Portal portal = null;
        if (destination == null) {
            return newRoute;
        }
        if (isAnotherMap(destination)) {
            portal = getPortal(currentMap, destination);
            if (portal == null) {
                return newRoute;
            }
        }
        List<Map<Rectangle, Route>> searchQueue = new LinkedList<>();

        fillInitialRoutes(currentMap, searchQueue, animal.getRectangle());

        List<Rectangle> searched = new ArrayList<>();
        while (!searchQueue.isEmpty()) {
            Map<Rectangle, Route> map = searchQueue.get(0);
            Rectangle rectangleToCheck = null;
            for (Rectangle rectangle : map.keySet()) {
                rectangleToCheck = rectangle;
            }
            if (rectangleToCheck != null && !searched.contains(rectangleToCheck)) {
                searchQueue.remove(0);
                boolean found;
                if (isAnotherMap(destination)) {
                    found = isTherePortal(portal, rectangleToCheck);
                } else if (FOOD.equals(destination)) {
                    found = isThereFood(currentMap, rectangleToCheck);
                } else if (WATER.equals(destination)) {
                    found = isThereWater(currentMap, rectangleToCheck);
                } else if (PILLOW.equals(destination)) {
                    found = isTherePillow(currentMap, rectangleToCheck);
                } else if (NPC.equals(destination)) {
                    found = isThereNpc(currentMap, rectangleToCheck);
                } else {
                    found = false;
                }
                if (found) {
                    logger.info(String.format("%s found his way to %s!", animal, destination));
                    return map.get(rectangleToCheck);
                } else {
                    searched.add(rectangleToCheck);
                    fillSearchQueue(currentMap, searchQueue, rectangleToCheck, map.get(rectangleToCheck), searched);
                }

            } else {
                searchQueue.remove(0);
            }
        }
        return newRoute;
    }

    public Route calculateRoute(GameMap currentMap, Npc npc, String destination) {
        Route newRoute = new Route();
        logger.debug(String.format("%s is looking for a way to : %s", npc, destination));
        Portal portal = null;
        if (destination == null) {
            return newRoute;
        }
        if (isAnotherMap(destination)) {
            portal = getPortal(currentMap, destination);
            if (portal == null) {
                return newRoute;
            }
        }
        List<Map<Rectangle, Route>> searchQueue = new LinkedList<>();

        fillInitialRoutes(currentMap, searchQueue, npc.getRectangle());

        List<Rectangle> searched = new ArrayList<>();
        while (!searchQueue.isEmpty()) {
            Map<Rectangle, Route> map = searchQueue.get(0);
            Rectangle rectangleToCheck = null;
            for (Rectangle rectangle : map.keySet()) {
                rectangleToCheck = rectangle;
            }
            if (rectangleToCheck != null && !searched.contains(rectangleToCheck)) {
                searchQueue.remove(0);
                boolean found;
                if (isAnotherMap(destination)) {
                    found = isTherePortal(portal, rectangleToCheck);
                } else if (FOOD.equals(destination)) {
                    found = isThereFood(currentMap, rectangleToCheck);
                } else if (WATER.equals(destination)) {
                    found = isThereWater(currentMap, rectangleToCheck);
                } else if (NPC_SPOT.equals(destination)) {
                    found = isThereNpcSpot(currentMap, rectangleToCheck);
                } else {
                    found = false;
                }
                if (found) {
                    logger.info(String.format("%s found his way to %s!", npc, destination));
                    return map.get(rectangleToCheck);
                } else {
                    searched.add(rectangleToCheck);
                    fillSearchQueue(currentMap, searchQueue, rectangleToCheck, map.get(rectangleToCheck), searched);
                }

            } else {
                searchQueue.remove(0);
            }
        }
        return newRoute;
    }

    private void fillInitialRoutes(GameMap gameMap, List<Map<Rectangle, Route>> searchQueue, Rectangle rectangle) {
        Rectangle initialRectangle = new Rectangle(rectangle.getX(), rectangle.getY(), TILE_SIZE, TILE_SIZE);

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

    private boolean isAnotherMap(String destination) {
        return !(WATER.equals(destination) || FOOD.equals(destination) || PILLOW.equals(destination) || NPC.equals(destination) || NPC_SPOT.equals(destination));
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
            return new Rectangle(rectangle.getX(), rectangle.getY() - NavigationService.getPixelsToAdjustPosition(UP, rectangle.getX(), rectangle.getY()), 32, 32);
        }
        return null;
    }

    public Rectangle tryDown(GameMap gameMap, Rectangle rectangle) {
        if (rectangle.getY() + (TILE_SIZE * ZOOM) <= gameMap.getMapHeight() * (TILE_SIZE * ZOOM) && canWalkThisDirection(gameMap, DOWN, rectangle.getX(), rectangle.getY())) {
            return new Rectangle(rectangle.getX(), rectangle.getY() + NavigationService.getPixelsToAdjustPosition(DOWN, rectangle.getX(), rectangle.getY()), 32, 32);
        }
        return null;
    }

    public Rectangle tryLeft(GameMap gameMap, Rectangle rectangle) {
        if (rectangle.getX() >= 0 && canWalkThisDirection(gameMap, LEFT, rectangle.getX(), rectangle.getY())) {
            return new Rectangle(rectangle.getX() - NavigationService.getPixelsToAdjustPosition(LEFT, rectangle.getX(), rectangle.getY()), rectangle.getY(), 32, 32);
        }
        return null;
    }

    public Rectangle tryRight(GameMap gameMap, Rectangle rectangle) {
        if (rectangle.getX() + (TILE_SIZE * ZOOM) <= gameMap.getMapWidth() * (TILE_SIZE * ZOOM) && canWalkThisDirection(gameMap, RIGHT, rectangle.getX(), rectangle.getY())) {
            return new Rectangle(rectangle.getX() + NavigationService.getPixelsToAdjustPosition(RIGHT, rectangle.getX(), rectangle.getY()), rectangle.getY(), 32, 32);
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

    public boolean isTherePillow(GameMap gameMap, Rectangle rectangle) {
        for (MapTile pillow : gameMap.getPillows()) {
            if (rectangle.intersects(pillow)) {
                return true;
            }
        }
        return false;
    }

    private boolean isThereNpc(GameMap gameMap, Rectangle rectangle) {
        for (GameObject gameObject : gameMap.getInteractiveObjects()) {
            if (gameObject instanceof Npc) {
                return rectangle.intersects(((Npc) gameObject).getRectangle());
            }
        }
        return false;
    }

    private boolean isThereNpcSpot(GameMap gameMap, Rectangle rectangle) {
        for (GameObject gameObject : gameMap.getInteractiveObjects()) {
            if (gameObject instanceof NpcSpot) {
                return rectangle.intersects(((NpcSpot) gameObject).getRectangle());
            }
        }
        return false;
    }

    private boolean canWalkThisDirection(GameMap gameMap, Direction direction, int xPosition, int yPosition) {
        switch (direction) {
            case LEFT:
                xPosition = xPosition - Math.abs(NavigationService.getPixelsToAdjustPosition(direction, xPosition, yPosition));
                break;
            case RIGHT:
                xPosition = xPosition + Math.abs(NavigationService.getPixelsToAdjustPosition(direction, xPosition, yPosition));
                break;
            case UP:
                yPosition = yPosition - Math.abs(NavigationService.getPixelsToAdjustPosition(direction, xPosition, yPosition));
                break;
            case DOWN:
                yPosition = yPosition + Math.abs(NavigationService.getPixelsToAdjustPosition(direction, xPosition, yPosition));
                break;
        }
        return isWalkable(gameMap, xPosition, yPosition);
    }

    public boolean isWalkable(GameMap gameMap, int x, int y) {
        Rectangle rectangle = new Rectangle(x, y, 32, 32);

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
            Rectangle tileRectangle = new Rectangle(tile.getX() * (TILE_SIZE * ZOOM), tile.getY() * (TILE_SIZE * ZOOM), 32, 32);
            if (rectangle.intersects(tileRectangle)) {
                return false;
            }
        }
        return true;
    }

    private Portal getPortal(GameMap gameMap, String destination) {
        if (destination == null) {
            return null;
        }
        for (GameObject gameObject : gameMap.getInteractiveObjects()) {
            if (gameObject instanceof Portal && destination.equalsIgnoreCase(((Portal) gameObject).getDirection())) {
                return (Portal) gameObject;
            }
        }
        return null;
    }

    private boolean isTherePortal(Portal portal, Rectangle rectangle) {
        return rectangle.intersects(portal.getRectangle());
    }
}
