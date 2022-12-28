package base.gameobjects.animalstates;

import base.Game;
import base.gameobjects.*;
import base.graphicsservice.Rectangle;
import base.map.GameMap;
import base.map.MapTile;
import base.navigationservice.Direction;
import base.navigationservice.NavigationService;
import base.navigationservice.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.*;
import static base.constants.MapConstants.CITY_MAP;
import static base.constants.MapConstants.FOREST_MAP;
import static base.gameobjects.Animal.*;
import static base.navigationservice.Direction.STAY;

public class WalkingState implements AnimalState {

    protected static final Logger logger = LoggerFactory.getLogger(WalkingState.class);

    private int movingTicks = 0;
    private boolean isGoingToNpc;
    private boolean isGoingAway;

    @Override
    public void update(Animal animal, Game game) {
        Direction nextDirection = animal.getDirection();
        boolean makingLastRouteMove = false;
        if (movingTicks < 1) {
            if (!animal.getRoute().isEmpty()) {
                nextDirection = animal.getRoute().getNextStep();
                movingTicks = getMovingTickToAdjustPosition(nextDirection, animal);
                logger.debug(String.format("Direction: %s, moving ticks: %d", animal.getDirection().name(), movingTicks));
                if (animal.getRoute().isEmpty()) {
                    makingLastRouteMove = true;
                }
            } else {
                nextDirection = animal.getRandomDirection();
                if (nextDirection == STAY) {
                    animal.getAnimatedSprite().reset();
                    animal.setWaitingState();
                    return;
                }
                movingTicks = animal.getRandomMovingTicks();
            }
            if (isOutsideOfMap(game.getGameMap(animal.getCurrentMap()), animal.getRectangle())) {
                moveAnimalToCenter(game.getGameMap(animal.getCurrentMap()), animal);
            }
        }

        handleMoving(game.getGameMap(animal.getCurrentMap()), nextDirection, animal);

        if (nextDirection != animal.getDirection()) {
            animal.setDirection(nextDirection);
            animal.updateDirection();
        }

        animal.getAnimatedSprite().update(game);
        animal.getInteractionZone().changePosition(animal.getRectangle().getX() + 8, animal.getRectangle().getY() + 8);

        checkIfNeedToGoToDifferentLocation(game, animal);
        checkPortal(game, animal);

        movingTicks--;

        if (isHungry(animal) && animal.getRoute().isEmpty() && !makingLastRouteMove) {
            lookForFood(animal, game);
        }
        if (isThirsty(animal) && animal.getRoute().isEmpty() && !makingLastRouteMove) {
            lookForWater(animal, game);
        }
        if (isSleepy(animal) && animal.getRoute().isEmpty() && !makingLastRouteMove) {
            if (animal.getRoute().isEmpty() && isTherePillow(game, animal)) {
                animal.setFallingAsleepState();
                return;
            }
            if (animal.getRoute().isEmpty()) {
                animal.setRoute(game.calculateRouteToPillow(animal));
            }
            if (animal.getRoute().isEmpty()) {
                animal.setFallingAsleepState();
                return;
            }
        }
        if ((isHungerLow(animal) && isNearFood(game, animal)) || (isThirstLow(animal) && isNearWater(game, animal))) {
            animal.setEatingState();
            return;
        }

        if (isGoingToNpc && animal.getRoute().isEmpty() && isArrivedToNpc(game, animal)) {
            isGoingToNpc = false;
            game.sendAnimalAway(animal);
        }
        if (isGoingAway && animal.getRoute().isEmpty()) {
            game.removeAnimal(animal);
        }
    }

    protected int getMovingTickToAdjustPosition(Direction direction, Animal animal) {
        return Math.abs(NavigationService.getPixelsToAdjustPosition(direction, animal.getCurrentX(), animal.getCurrentY())) / animal.getSpeed();
    }

    private boolean isOutsideOfMap(GameMap gameMap, Rectangle animalRectangle) {
        return animalRectangle.getX() < -10
                || animalRectangle.getY() < -10
                || animalRectangle.getX() > gameMap.getMapWidth() * CELL_SIZE + 10
                || animalRectangle.getY() > gameMap.getMapHeight() * CELL_SIZE + 10;
    }

    private void moveAnimalToCenter(GameMap gameMap, Animal animal) {
        Rectangle animalRectangle = animal.getRectangle();
        animalRectangle.setX(gameMap.getMapWidth() * CELL_SIZE / 2);
        animalRectangle.setY(gameMap.getMapHeight() * CELL_SIZE / 2);

        if (animal.isAnimalStuck(gameMap)) {
            animal.tryToMove(gameMap);
        }
    }

    private void handleMoving(GameMap gameMap, Direction direction, Animal animal) {
        if (animal.unwalkableInThisDirection(gameMap, direction, animal.getRectangle(), animal.getSpeed(), animal.getLayer())) {
            animal.setRoute(new Route());
            movingTicks = 0;
            animal.handleUnwalkable(animal.getRectangle(), direction, animal.getSpeed());
            return;
        }

        switch (direction) {
            case LEFT:
                if (animal.getRectangle().getX() > 0 || animal.nearPortal(gameMap.getPortals(), animal.getRectangle())) {
                    animal.getRectangle().setX(animal.getRectangle().getX() - animal.getSpeed());
                }
                break;
            case RIGHT:
                if (animal.getRectangle().getX() < (gameMap.getMapWidth() * CELL_SIZE - animal.getRectangle().getWidth()) || animal.nearPortal(gameMap.getPortals(), animal.getRectangle())) {
                    animal.getRectangle().setX(animal.getRectangle().getX() + animal.getSpeed());
                }
                break;
            case UP:
                if (animal.getRectangle().getY() > 0 || animal.nearPortal(gameMap.getPortals(), animal.getRectangle())) {
                    animal.getRectangle().setY(animal.getRectangle().getY() - animal.getSpeed());
                }
                break;
            case DOWN:
                if (animal.getRectangle().getY() < (gameMap.getMapHeight() * CELL_SIZE - animal.getRectangle().getHeight()) || animal.nearPortal(gameMap.getPortals(), animal.getRectangle())) {
                    animal.getRectangle().setY(animal.getRectangle().getY() + animal.getSpeed());
                }
                break;
        }
    }

    private void checkIfNeedToGoToDifferentLocation(Game game, Animal animal) {
        if (animal.getRoute().isEmpty() && (animal.getCurrentMap().equalsIgnoreCase(FOREST_MAP) || animal.getCurrentMap().equalsIgnoreCase(CITY_MAP))) {
            animal.setRoute(game.calculateRouteToOtherMap(animal, NavigationService.getNextPortalToGetToCenter(animal.getCurrentMap())));
        }
    }

    private void checkPortal(Game game, Animal animal) {
        Portal portal = animal.getPortalTile(game, animal.getCurrentMap(), animal.getRectangle());
        if (portal != null && !(portal.getDirection().equalsIgnoreCase(FOREST_MAP) || portal.getDirection().equalsIgnoreCase(CITY_MAP))) {
            game.moveAnimalToAnotherMap(animal, portal);
        }
    }

    private boolean isArrivedToNpc(Game game, Animal animal) {
        if (!animal.getRectangle().intersects(game.getAdoptionNpc().getRectangle())) {
            animal.setRoute(game.calculateRouteToNpc(animal));
            return false;
        } else {
            return true;
        }
    }

    public void sendToNpc(Animal animal, Route route) {
        isGoingToNpc = true;
        animal.setRoute(route);
        logger.info("Animal is going to NPC");
    }

    public void goAway(Animal animal, Route route) {
        isGoingAway = true;
        animal.setRoute(route);
        logger.info("SENDING Animal AWAY");
    }

    private boolean isThirsty(Animal animal) {
        return animal.getCurrentThirst() < MAX_THIRST / 100 * 25 && animal.getRoute().isEmpty();
    }

    private boolean isHungry(Animal animal) {
        return animal.getCurrentHunger() < MAX_HUNGER / 100 * 25 && animal.getRoute().isEmpty();
    }

    private void lookForWater(Animal animal, Game game) {
        animal.setRoute(game.calculateRouteToWater(animal));
        if (animal.getRoute().isEmpty()) {
            String mapWithWater = game.getNearestMapWithWater(animal.getCurrentMap());
            if (!mapWithWater.equalsIgnoreCase(animal.getCurrentMap())) {
                logger.info(String.format("%s is going to %s to get water", animal, mapWithWater));
                animal.setRoute(game.calculateRouteToOtherMap(animal, mapWithWater));
            }
        }
        if (animal.getRoute().isEmpty()) {
            animal.setWaitingState();
        }
    }

    private void lookForFood(Animal animal, Game game) {
        animal.setRoute(game.calculateRouteToFood(animal));
        if (animal.getRoute().isEmpty()) {
            String mapWithFood = game.getNearestMapWithFood(animal.getCurrentMap());
            if (!mapWithFood.equalsIgnoreCase(animal.getCurrentMap())) {
                logger.info(String.format("%s is going to %s to get food", animal, mapWithFood));
                Route routeToOtherMap = game.calculateRouteToOtherMap(animal, mapWithFood);
                if (routeToOtherMap.isEmpty()) {
                    logger.info("Route to other map was empty");
                    animal.setWaitingState();
                } else {
                    animal.setRoute(routeToOtherMap);
                }
            }
        }
        if (animal.getRoute().isEmpty()) {
            animal.setWaitingState();
        }
    }

    private boolean isSleepy(Animal animal) {
        return animal.getCurrentEnergy() < MAX_ENERGY / 100 * 25;
    }

    private boolean isTherePillow(Game game, Animal animal) {
        for (MapTile pillow : game.getGameMap(animal.getCurrentMap()).getPillows()) {
            if (animal.getRectangle().intersects(pillow)) {
                return true;
            }
        }
        return false;
    }

    private boolean isHungerLow(Animal animal) {
        return animal.getCurrentHunger() < MAX_HUNGER / 100 * 70;
    }

    private boolean isThirstLow(Animal animal) {
        return animal.getCurrentThirst() < MAX_THIRST / 100 * 70;
    }

    private boolean isNearFood(Game game, Animal animal) {
        for (Item item : game.getGameMap(animal.getCurrentMap()).getItems()) {
            if (item != null && animal.getRectangle().intersects(item.getRectangle())) {
                return true;
            }
        }
        for (FoodBowl bowl : game.getGameMap(animal.getCurrentMap()).getFoodBowls()) {
            if (bowl.isFull() && animal.getRectangle().intersects(bowl.getRectangle())) {
                return true;
            }
        }
        return false;
    }

    private boolean isNearWater(Game game, Animal animal) {
        for (WaterBowl bowl : game.getGameMap(animal.getCurrentMap()).getWaterBowls()) {
            if (bowl.isFull() && animal.getRectangle().intersects(bowl.getRectangle())) {
                return true;
            }
        }
        if (game.isNearWater(animal)) {
            return true;
        }
        return false;
    }
}
