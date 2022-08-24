package base.gameobjects;

import base.Game;
import base.gameobjects.animals.Butterfly;
import base.gameobjects.interactionzones.InteractionZonePetHeart;
import base.graphicsservice.*;
import base.gui.HeartIcon;
import base.map.GameMap;
import base.map.MapTile;
import base.navigationservice.Direction;
import base.navigationservice.NavigationService;
import base.navigationservice.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.GREEN;
import static base.constants.ColorConstant.YELLOW;
import static base.constants.Constants.*;
import static base.constants.FilePath.IMAGES_PATH;
import static base.constants.MapConstants.CITY_MAP;
import static base.constants.MapConstants.FOREST_MAP;
import static base.gameobjects.AgeStage.ADULT;
import static base.gameobjects.AgeStage.BABY;
import static base.navigationservice.Direction.*;

public abstract class Animal implements GameObject, Walking {

    private Sprite previewSprite;
    private AnimatedSprite animatedSprite;
    private final Rectangle animalRectangle;
    private final int tileSize;
    private String fileName;

    private Direction direction;
    private int movingTicks = 0;
    private transient Route route;
    private boolean isGoingToNpc;
    private boolean isGoingAway;

    private String currentMap;
    private int speed;
    private String color;
    private String animalType;
    private String name;
    private boolean favorite;

    private AgeStage age;
    public static final int GROWING_UP_TIME = 200_000;
    private int currentAge;

    public static final int MAX_HUNGER = 30_000;
    private int currentHunger;

    public static final int MAX_THIRST = 25_000;
    private int currentThirst;

    public static final int MAX_ENERGY = 40_000;
    protected static final int MIN_ENERGY = 1;
    protected static final int SLEEPING_SPEED = 15;
    private int currentEnergy;

    private final int MAX_WAITING_TICKS = 500;
    private int currentWaitingTicks;

    private final InteractionZonePetHeart interactionZone;
    private final HeartIcon heartIcon;

    protected static final Logger logger = LoggerFactory.getLogger(Animal.class);

    protected Animal(String animalType, int startX, int startY, int tileSize) {
        this(animalType, startX, startY, 1, tileSize, MAX_HUNGER, MAX_THIRST, MAX_ENERGY, ADULT, "");
    }

    protected Animal(String animalType, int startX, int startY, int speed, int tileSize, int currentHunger, int currentThirst, int currentEnergy, AgeStage age, String name) {
        this.animalType = animalType;
        this.tileSize = tileSize;
        this.speed = speed;
        this.currentHunger = currentHunger;
        this.currentThirst = currentThirst;
        this.currentEnergy = currentEnergy;
        this.age = age;
        this.name = name;

        loadAnimatedSprite();
        setPreviewSprite();

        direction = DOWN;
        updateDirection();
        animalRectangle = new Rectangle(startX, startY, 32, 32);
        animalRectangle.generateBorder(1, GREEN);

        if (BABY.equals(age)) {
            this.speed--;
        } else {
            setCurrentAge(GROWING_UP_TIME);
        }

        route = new Route();
        interactionZone = new InteractionZonePetHeart(animalRectangle.getX() + 32, animalRectangle.getY() + 32, 50);
        heartIcon = new HeartIcon();
    }

    protected void loadAnimatedSprite() {
        animatedSprite = ImageLoader.getAnimatedSprite(IMAGES_PATH + animalType + ".png", tileSize);
    }

    protected void setPreviewSprite() {
        previewSprite = ImageLoader.getPreviewSprite(IMAGES_PATH + animalType + "-preview.png");
    }

    private void updateDirection() {
        int startSprite = getStartingSprite(direction);
        int endSprite = getEndSprite(direction);
        animatedSprite.setAnimationRange(startSprite, endSprite);
    }

    protected int getStartingSprite(Direction direction) {
        if (animatedSprite != null && direction != STAY) {
            if (direction.name().startsWith("EAT")) {
                if (animatedSprite.getSpritesSize() >= 28) {
                    return (direction.directionNumber - 5) * animatedSprite.getSpritesSize() / 4 + 3;
                } else {
                    return (direction.directionNumber - 5) * animatedSprite.getSpritesSize() / 4;
                }
            }
            if (direction.name().startsWith("SLEEP") || direction.name().startsWith("WAKEUP")) {
                return (direction.directionNumber - 9) * animatedSprite.getSpritesSize() / 4 + 7;
            }
            return direction.directionNumber * animatedSprite.getSpritesSize() / 4;
        }
        return 0;
    }

    protected int getEndSprite(Direction direction) {
        if (animatedSprite != null && direction != STAY) {
            if (direction.name().startsWith("EAT")) {
                if (animatedSprite.getSpritesSize() >= 28) {
                    return (direction.directionNumber - 5) * animatedSprite.getSpritesSize() / 4 + 6;
                } else {
                    return (direction.directionNumber - 5) * animatedSprite.getSpritesSize() / 4 + 2;
                }
            }
            if (direction.name().startsWith("SLEEP") || direction.name().startsWith("WAKEUP")) {
                return (direction.directionNumber - 9) * animatedSprite.getSpritesSize() / 4 + 10;
            }
            return direction.directionNumber * animatedSprite.getSpritesSize() / 4 + 2;
        }
        return 0;
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        int xForSprite = animalRectangle.getX();
        int yForSprite = animalRectangle.getY();
        if (BABY.equals(age) && !animalType.contains("baby") || animalType.equals("chicken-baby")) {
            zoom = 1;
            xForSprite = animalRectangle.getX() + animalRectangle.getWidth() / 2;
            yForSprite = animalRectangle.getY() + animalRectangle.getHeight() / 2;
        }
        if (animatedSprite != null) {
            renderer.renderSprite(animatedSprite, xForSprite, yForSprite, zoom, false);
        }
        if (DEBUG_MODE) {
            Rectangle rectangle = new Rectangle(xForSprite, yForSprite, animalRectangle.getWidth(), animalRectangle.getHeight());
            if (interactionZone.isPlayerInRange()) {
                rectangle.generateBorder(2, GREEN);
            } else {
                rectangle.generateBorder(1, YELLOW);
            }
            renderer.renderRectangle(rectangle, zoom, false);
            interactionZone.render(renderer, zoom);
        }
        if (interactionZone.isPlayerInRange()) {
            heartIcon.render(renderer, 1);
        }
    }

    @Override
    public void update(Game game) {
        boolean isMoving = false;
        Direction nextDirection = direction;

        if (currentWaitingTicks > 0) {
            logger.debug(String.format("%s is waiting for %d", this, currentWaitingTicks));
            currentWaitingTicks--;
        } else if (movingTicks < 1) {
            if (!route.isEmpty()) {
                nextDirection = route.getNextStep();
                movingTicks = getMovingTickToAdjustPosition(nextDirection);
                logger.debug(String.format("Direction: %s, moving ticks: %d", direction.name(), movingTicks));
            } else {
                nextDirection = getRandomDirection();
                movingTicks = getRandomMovingTicks();
            }
            if (isOutsideOfMap(game.getGameMap(getCurrentMap()))) {
                moveAnimalToCenter(game.getGameMap(getCurrentMap()));
            }
        }

        handleMoving(game.getGameMap(currentMap), nextDirection);
        if (nextDirection != STAY || this instanceof Butterfly) {
            isMoving = true;
        }

        if (nextDirection != direction) {
            direction = nextDirection;
            updateDirection();
        }
        if (animatedSprite != null) {
            if (isMoving && (isFallingAsleep() || isWakingUp() || notRelatedToSleeping())) {
                animatedSprite.update(game);
                interactionZone.changePosition(animalRectangle.getX() + 32, animalRectangle.getY() + 32);
            } else if (!isSleeping()) {
                animatedSprite.reset();
            }
        }
        checkIfNeedToGoToDifferentLocation(game);
        checkPortal(game);

        movingTicks--;
        if (!isSleeping()) {
            if (currentWaitingTicks < 1) {
                eatAndDrinkIfNeeded(game);
            }
            sleepIfNeeded(game);
        } else {
            handleSleeping();
        }

        if (BABY.equals(age)) {
            updateAge();
        }
        interactionZone.update(game);
        if (interactionZone.isPlayerInRange()) {
            updateHeart(game);
        }

        if (isGoingToNpc && route.isEmpty() && isArrivedToNpc(game)) {
            isGoingToNpc = false;
            game.sendAnimalAway(this);
        }
        if (isGoingAway && route.isEmpty()) {
            game.removeAnimal(this);
        }
    }

    private boolean isArrivedToNpc(Game game) {
        if (!animalRectangle.intersects(game.getNpc().getRectangle())) {
            route = game.calculateRouteToNpc(this);
            return false;
        } else {
            return true;
        }
    }

    private void updateHeart(Game game) {
        int xPosition = animalRectangle.getX() - game.getRenderer().getCamera().getX() + 16;
        int yPosition = animalRectangle.getY() - game.getRenderer().getCamera().getY() - 16;
        Position heartPosition = new Position(xPosition, yPosition);
        heartIcon.changePosition(heartPosition);
    }

    private void checkIfNeedToGoToDifferentLocation(Game game) {
        if (route.isEmpty() && (getCurrentMap().equalsIgnoreCase(FOREST_MAP) || getCurrentMap().equalsIgnoreCase(CITY_MAP))) {
            route = game.calculateRouteToOtherMap(this, NavigationService.getNextPortalToGetToCenter(getCurrentMap()));
        }
    }

    private void checkPortal(Game game) {
        Portal portal = getPortalTile(game, currentMap, animalRectangle);
        if (portal != null && (!portal.getDirection().equalsIgnoreCase(FOREST_MAP) || !portal.getDirection().equalsIgnoreCase(CITY_MAP))) {
            game.moveAnimalToAnotherMap(this, portal);
        }
    }

    protected void updateAge() {
        incrementAge();
        if (isTimeToGrowUp()) {
            setAge(ADULT);
            speed++;
        }
    }

    protected boolean isTimeToGrowUp() {
        return currentAge >= GROWING_UP_TIME;
    }

    protected void incrementAge() {
        currentAge++;
    }

    private void handleSleeping() {
        currentEnergy += SLEEPING_SPEED;
        if (currentEnergy >= MAX_ENERGY) {
            wakeUp();
        }
    }

    private boolean notRelatedToSleeping() {
        return direction.directionNumber < 9;
    }

    private boolean isFallingAsleep() {
        return isSleeping() && animatedSprite.getCurrentSprite() < animatedSprite.getEndSprite();
    }

    private boolean isWakingUp() {
        return direction.name().startsWith("WAKEUP") && animatedSprite.getCurrentSprite() < animatedSprite.getEndSprite();
    }

    private void sleepIfNeeded(Game game) {
        decreaseEnergyLevel();

        if (!(this instanceof Butterfly) && isSleepy() && !isSleeping()) {
            if (route.isEmpty() && isTherePillow(game)) {
                sleep();
                return;
            }
            if (route.isEmpty()) {
                route = game.calculateRouteToPillow(this);
            }
            if (route.isEmpty()) {
                sleep();
            }
        }
    }

    private boolean isTherePillow(Game game) {
        for (MapTile pillow : game.getGameMap(currentMap).getPillows()) {
            if (animalRectangle.intersects(pillow)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSleepy() {
        return currentEnergy < MAX_ENERGY / 100 * 25;
    }

    private void sleep() {
        logger.info(String.format("%s will sleep now", this));
        if (direction == UP || direction == LEFT) {
            direction = SLEEP_LEFT;
        } else {
            direction = SLEEP_RIGHT;
        }
        updateDirection();
        movingTicks = MAX_ENERGY;
    }

    private void wakeUp() {
        currentEnergy = MAX_ENERGY;
        if (direction == SLEEP_LEFT) {
            direction = WAKEUP_LEFT;
        }
        if (direction == SLEEP_RIGHT) {
            direction = WAKEUP_RIGHT;
        }
        updateDirection();
        movingTicks = 3 * animatedSprite.getSpeed();
    }

    private void decreaseEnergyLevel() {
        if (!isSleeping() && currentEnergy > MIN_ENERGY) {
            currentEnergy--;
        }
        if (currentEnergy != 0 && currentEnergy % (MAX_ENERGY / 10) == 0) {
            logger.debug(String.format("Energy level for %s is %d percent", this, currentEnergy / (MAX_ENERGY / 100)));
        }
    }

    private boolean isSleeping() {
        return direction.name().startsWith("SLEEP");
    }

    private void eatAndDrinkIfNeeded(Game game) {
        decreaseHungerLevel();
        if (!(this instanceof Butterfly) && currentHunger < MAX_HUNGER / 100 * 25 && route.isEmpty()) {
            route = game.calculateRouteToFood(this);
            if (route.isEmpty()) {
                String mapWithFood = game.getNearestMapWithFood(currentMap);
                if (!mapWithFood.equalsIgnoreCase(currentMap)) {
                    logger.info(String.format("%s is going to %s to get food", this, mapWithFood));
                    route = game.calculateRouteToOtherMap(this, mapWithFood);
                }
            }
            if (route.isEmpty() && currentWaitingTicks <= 1) {
                currentWaitingTicks = MAX_WAITING_TICKS;
            }
        }

        decreaseThirstLevel();
        if (!(this instanceof Butterfly) && currentThirst < MAX_THIRST / 100 * 25 && route.isEmpty()) {
            route = game.calculateRouteToWater(this);
            if (route.isEmpty()) {
                String mapWithWater = game.getNearestMapWithWater(currentMap);
                if (!mapWithWater.equalsIgnoreCase(currentMap)) {
                    logger.info(String.format("%s is going to %s to get food", this, mapWithWater));
                    route = game.calculateRouteToOtherMap(this, mapWithWater);
                }
            }
            if (route.isEmpty() && currentWaitingTicks <= 1) {
                currentWaitingTicks = MAX_WAITING_TICKS;
            }
        }
        if (!(this instanceof Butterfly) && (isHungerLow() && isEating(game)) || (isThirstLow() && isDrinking(game))) {
            updateEatingDirection();
            movingTicks = getRandomMovingTicks();
        }
    }

    private void updateEatingDirection() {
        if (direction == UP) {
            direction = EAT_UP;
        }
        if (direction == DOWN) {
            direction = EAT_DOWN;
        }
        if (direction == LEFT) {
            direction = EAT_LEFT;
        }
        if (direction == RIGHT) {
            direction = EAT_RIGHT;
        }
        if (animatedSprite.getSpritesSize() < 28) {
            direction = STAY;
        }
        updateDirection();
    }

    private boolean isHungerLow() {
        return currentHunger < MAX_HUNGER / 100 * 70;
    }

    private boolean isThirstLow() {
        return currentThirst < MAX_THIRST / 100 * 70;
    }

    private void decreaseHungerLevel() {
        if (currentHunger > 0) {
            currentHunger--;
        }
        if (currentHunger != 0 && currentHunger % (MAX_HUNGER / 10) == 0) {
            logger.debug(String.format("Hunger level for %s is %d percent", this, currentHunger / (MAX_HUNGER / 100)));
        }
        if (currentHunger < MAX_HUNGER / 100 * 10) {
            speed = 1;
        }
    }

    private void decreaseThirstLevel() {
        if (currentThirst > 0) {
            currentThirst--;
        }
        if (currentThirst != 0 && currentThirst % (MAX_THIRST / 10) == 0) {
            logger.debug(String.format("Thirst level for %s is %d percent", this, currentThirst / (MAX_THIRST / 100)));
        }
        if (currentThirst < MAX_THIRST / 100 * 10) {
            speed = 1;
        }
    }

    private boolean isEating(Game game) {
        for (Item item : game.getGameMap(currentMap).getItems()) {
            if (item != null && animalRectangle.intersects(item.getRectangle())) {
                eatFood();
                game.getGameMap(currentMap).removeItem(item.getItemName(), item.getRectangle());
                return true;
            }
        }
        for (FoodBowl bowl : game.getGameMap(currentMap).getFoodBowls()) {
            if (bowl.isFull() && animalRectangle.intersects(bowl.getRectangle())) {
                eatFood();
                bowl.emptyBowl();
                return true;
            }
        }
        return false;
    }

    private void eatFood() {
        logger.info(String.format("%s ate food", this));
        currentHunger = MAX_HUNGER;
        resetSpeedToDefault();
        logger.debug(String.format("Hunger level for %s is 100 percent", this));
    }

    private boolean isDrinking(Game game) {
        for (WaterBowl bowl : game.getGameMap(currentMap).getWaterBowls()) {
            if (bowl.isFull() && animalRectangle.intersects(bowl.getRectangle())) {
                drink();
                bowl.emptyBowl();
                return true;
            }
        }
        if (game.isNearWater(this)) {
            moveTheSameWay();
            drink();
            return true;
        }
        return false;
    }

    private void moveTheSameWay() {
        switch (direction) {
            case LEFT:
                animalRectangle.setX(animalRectangle.getX() - (speed + 4));
                break;
            case RIGHT:
                animalRectangle.setX(animalRectangle.getX() + (speed + 4));
                break;
            case UP:
                animalRectangle.setY(animalRectangle.getY() - (speed + 4));
                break;
            case DOWN:
                animalRectangle.setY(animalRectangle.getY() + (speed + 4));
                break;
        }
    }

    private void drink() {
        logger.info(String.format("%s drank water", this));
        currentThirst = MAX_THIRST;
        resetSpeedToDefault();
        logger.debug(String.format("Thirst level for %s is 100 percent", this));
    }

    protected void resetSpeedToDefault() {
        if (BABY.equals(age)) {
            setSpeed(2);
        } else {
            setSpeed(3);
        }
    }

    private void handleMoving(GameMap gameMap, Direction direction) {
        if (unwalkableInThisDirection(gameMap, direction, animalRectangle, speed, getLayer())) {
            route = new Route();
            movingTicks = 0;
            handleUnwalkable(animalRectangle, direction, speed);
            return;
        }

        switch (direction) {
            case LEFT:
                if (animalRectangle.getX() > 0 || nearPortal(gameMap.getPortals(), animalRectangle)) {
                    animalRectangle.setX(animalRectangle.getX() - speed);
                }
                break;
            case RIGHT:
                if (animalRectangle.getX() < (gameMap.getMapWidth() * TILE_SIZE - animalRectangle.getWidth()) * ZOOM || nearPortal(gameMap.getPortals(), animalRectangle)) {
                    animalRectangle.setX(animalRectangle.getX() + speed);
                }
                break;
            case UP:
                if (animalRectangle.getY() > 0 || nearPortal(gameMap.getPortals(), animalRectangle)) {
                    animalRectangle.setY(animalRectangle.getY() - speed);
                }
                break;
            case DOWN:
                if (animalRectangle.getY() < (gameMap.getMapHeight() * TILE_SIZE - animalRectangle.getHeight()) * ZOOM || nearPortal(gameMap.getPortals(), animalRectangle)) {
                    animalRectangle.setY(animalRectangle.getY() + speed);
                }
                break;
        }
    }

    protected int getMovingTickToAdjustPosition(Direction direction) {
        return Math.abs(NavigationService.getPixelsToAdjustPosition(direction, getCurrentX(), getCurrentY())) / speed;
    }

    public boolean isAnimalStuck(GameMap gameMap) {
        return unwalkableInThisDirection(gameMap, LEFT, animalRectangle, speed, getLayer())
                && unwalkableInThisDirection(gameMap, RIGHT, animalRectangle, speed, getLayer())
                && unwalkableInThisDirection(gameMap, UP, animalRectangle, speed, getLayer())
                && unwalkableInThisDirection(gameMap, DOWN, animalRectangle, speed, getLayer());
    }

    public void tryToMove(GameMap gameMap) {
        logger.info(String.format("Animal %s is stuck, will try to move to nearest directions", this));
        route = new Route();
        for (Direction potentialDirection : Direction.values()) {
            if (!unwalkableInThisDirection(gameMap, potentialDirection, animalRectangle, speed, getLayer())) {
                moveAnimalTo(potentialDirection);
            }
        }
        if (isAnimalStuck(gameMap)) {
            logger.info("Animal still stuck, will try to move to center");
            moveAnimalToCenter(gameMap);
        }
        if (isAnimalStuck(gameMap)) {
            logger.error("Animal is stuck completely");
        }
    }

    public void moveAnimalTo(Direction direction) {
        switch (direction) {
            case LEFT:
                animalRectangle.setX(animalRectangle.getX() - (TILE_SIZE * ZOOM));
                break;
            case RIGHT:
                animalRectangle.setX(animalRectangle.getX() + (TILE_SIZE * ZOOM));
                break;
            case UP:
                animalRectangle.setY(animalRectangle.getY() - (TILE_SIZE * ZOOM));
                break;
            case DOWN:
                animalRectangle.setY(animalRectangle.getY() + (TILE_SIZE * ZOOM));
                break;
        }
    }

    @Override
    public int getLayer() {
        return 2;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (mouseRectangle.intersects(animalRectangle)) {
            logger.info("Click on Animal: ");
            moveAnimalToCenter(game.getGameMap());
            return true;
        }
        return false;
    }

    private boolean isOutsideOfMap(GameMap gameMap) {
        return animalRectangle.getX() < -10
                || animalRectangle.getY() < -10
                || animalRectangle.getX() > gameMap.getMapWidth() * (TILE_SIZE * ZOOM) + 10
                || animalRectangle.getY() > gameMap.getMapHeight() * (TILE_SIZE * ZOOM) + 10;
    }

    private void moveAnimalToCenter(GameMap gameMap) {
        animalRectangle.setX(gameMap.getMapWidth() * TILE_SIZE * ZOOM / 2);
        animalRectangle.setY(gameMap.getMapHeight() * TILE_SIZE * ZOOM / 2);

        if (isAnimalStuck(gameMap)) {
            tryToMove(gameMap);
        }
    }

    public String getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(String currentMap) {
        this.currentMap = currentMap;
    }

    public Sprite getPreviewSprite() {
        if (previewSprite != null) {
            return previewSprite;
        }
        return animatedSprite.getStartSprite();
    }

    public void teleportAnimalTo(int x, int y) {
        animalRectangle.setX(x);
        animalRectangle.setY(y);
    }

    public void sendToNpc(Route route) {
        isGoingToNpc = true;
        this.route = route;
        logger.info("Animal is going to NPC");
    }

    public void goAway(Route route) {
        isGoingAway = true;
        this.route = route;
        logger.info("SENDING Animal AWAY");
    }

    @Override
    public String toString() {
        return animalType + " named " + name;
    }

    /**
     * =================================== GETTERS & SETTERS ======================================
     */

    public int getSpeed() {
        return speed;
    }

    public int getCurrentX() {
        return animalRectangle.getX();
    }

    public int getCurrentY() {
        return animalRectangle.getY();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCurrentHunger() {
        return currentHunger;
    }

    public int getCurrentThirst() {
        return currentThirst;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public void setEnergy(int energy) {
        logger.debug(String.format("Setting current energy to %d percent", energy));
        this.currentEnergy = MAX_ENERGY / 100 * energy;
    }

    public void setThirst(int thirst) {
        logger.debug(String.format("Setting current thirst to %d percent", thirst));
        this.currentThirst = MAX_THIRST / 100 * thirst;
    }

    public void setHunger(int hunger) {
        logger.debug(String.format("Setting current hunger to %d percent", hunger));
        this.currentHunger = MAX_HUNGER / 100 * hunger;
    }

    public int getCurrentHungerInPercent() {
        return currentHunger / (MAX_HUNGER / 100);
    }

    public int getCurrentThirstInPercent() {
        return currentThirst / (MAX_THIRST / 100);
    }

    public int getCurrentEnergyInPercent() {
        return currentEnergy / (MAX_ENERGY / 100);
    }

    public String getAnimalType() {
        return animalType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public AgeStage getAge() {
        return age;
    }

    public void setAge(AgeStage age) {
        this.age = age;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    public int getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(int currentAge) {
        this.currentAge = currentAge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rectangle getRectangle() {
        return animalRectangle;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
