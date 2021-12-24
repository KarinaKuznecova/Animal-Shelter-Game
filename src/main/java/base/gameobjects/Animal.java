package base.gameobjects;

import base.Game;
import base.gameobjects.animals.Butterfly;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import base.map.GameMap;
import base.map.MapTile;
import base.navigationservice.Direction;
import base.navigationservice.NavigationService;
import base.navigationservice.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

import static base.constants.Constants.*;
import static base.constants.FilePath.IMAGES_PATH;
import static base.navigationservice.Direction.*;

public abstract class Animal implements GameObject {

    private Sprite previewSprite;
    private Sprite sprite;
    private AnimatedSprite animatedSprite = null;
    private final Rectangle animalRectangle;
    private final Random random;
    private final int tileSize;
    private String fileName;

    private Direction direction;
    private int movingTicks = 0;
    private Route route;
    private String homeMap;
    private int speed;
    private String color;
    private final String animalName;

    protected static final int MAX_HUNGER = 30000;
    protected static final int MIN_HUNGER = 1;
    private int currentHunger;

    protected static final Logger logger = LoggerFactory.getLogger(Animal.class);

    protected Animal(String animalName, int startX, int startY, int speed, int tileSize, int currentHunger) {
        this(animalName, startX, startY, speed, MAIN_MAP, tileSize, currentHunger);
    }

    protected Animal(String animalName, int startX, int startY, int speed, String homeMap, int tileSize, int currentHunger) {
        this.animalName = animalName;
        this.tileSize = tileSize;
        this.homeMap = homeMap;
        this.speed = speed;
        this.currentHunger = currentHunger;

        setSprite();
        setPreviewSprite();

        direction = DOWN;
        updateDirection();
        animalRectangle = new Rectangle(startX, startY, tileSize, tileSize);

        route = new Route();
        random = new Random();
    }

    private void setSprite() {
        sprite = ImageLoader.getAnimatedSprite(IMAGES_PATH + animalName + ".png", tileSize);
        if (sprite != null) {
            animatedSprite = (AnimatedSprite) sprite;
        }
    }

    private void setPreviewSprite() {
        previewSprite = ImageLoader.getPreviewSprite(IMAGES_PATH + animalName + "-preview.png");
    }

    private void updateDirection() {
        if (animatedSprite != null && direction != STAY) {
//            animatedSprite.setAnimationRange(direction.directionNumber, direction.directionNumber + 12);          // if vertical
            int startSprite = direction.directionNumber * animatedSprite.getSpritesSize() / 4;
            int endSprite = direction.directionNumber * animatedSprite.getSpritesSize() / 4 + 2;
            if (direction.name().startsWith("EAT")) {
                if (animatedSprite.getSpritesSize() == 28) {
                    startSprite = (direction.directionNumber - 5) * animatedSprite.getSpritesSize() / 4 + 3;
                    endSprite = startSprite + 3;
                } else {
                    startSprite = (direction.directionNumber - 5) * animatedSprite.getSpritesSize() / 4;
                    endSprite = (direction.directionNumber - 5) * animatedSprite.getSpritesSize() / 4 + 2;
                }
            }
            animatedSprite.setAnimationRange(startSprite, endSprite); //if horizontal increase
        }
    }

    @Override
    public void render(RenderHandler renderer, int xZoom, int yZoom) {
        if (animatedSprite != null) {
            renderer.renderSprite(animatedSprite, animalRectangle.getX(), animalRectangle.getY(), xZoom, yZoom, false);
        } else if (sprite != null) {
            renderer.renderSprite(sprite, animalRectangle.getX(), animalRectangle.getY(), xZoom, yZoom, false);
        } else {
            renderer.renderRectangle(animalRectangle, xZoom, yZoom, false);
        }
    }

    @Override
    public void update(Game game) {
        boolean isMoving = false;
        Direction nextDirection = direction;

        if (route.isEmpty() && getHomeMap().startsWith("Bottom")) {
            route = game.calculateRouteToMap(this, NavigationService.getNextPortalToGetToCenter(getHomeMap()));
        }
        if (route.isEmpty() && this instanceof Butterfly && HOME_MAPS.contains(getHomeMap())) {
            route = game.calculateRouteToMap(this, NavigationService.getNextPortalToOutside(getHomeMap()));
        }

        if (movingTicks < 1) {
            if (!route.isEmpty()) {
                nextDirection = route.getNextStep();
                movingTicks = getMovingTickToAdjustPosition(nextDirection);
            } else {
                nextDirection = getRandomDirection();
                movingTicks = getRandomMovingTicks();
            }
        }

        handleMoving(game.getGameMap(homeMap), nextDirection);
        if (nextDirection != STAY || this instanceof Butterfly) {
            isMoving = true;
        }

        if (nextDirection != direction) {
            direction = nextDirection;
            updateDirection();
        }

        if (animatedSprite != null) {
            if (isMoving) {
                animatedSprite.update(game);
            } else {
                animatedSprite.reset();
            }
        }

        checkPortals(game);

        movingTicks--;
        decreaseHungerLevel();
        if (!(this instanceof Butterfly) && currentHunger < MAX_HUNGER / 100 * 25 && route.isEmpty()) {
            route = game.calculateRouteToFood(this);
        }
        if (!(this instanceof Butterfly) && isHungerLow() && checkForFood(game)) {
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
            movingTicks = getRandomMovingTicks();
        }
    }

    private boolean isHungerLow() {
        return currentHunger < MAX_HUNGER / 100 * 70;
    }

    private void decreaseHungerLevel() {
        if (currentHunger > MIN_HUNGER) {
            currentHunger--;
        }
        if (currentHunger != 0 && currentHunger % (MAX_HUNGER / 10) == 0) {
            logger.debug(String.format("Hunger level for %s is %d percent", animalName, currentHunger / (MAX_HUNGER / 100)));
        }
        if (currentHunger < MAX_HUNGER / 100 * 10) {
            speed = 1;
        }
    }

    private boolean checkForFood(Game game) {
        List<Item> items;
        if (game.getGameMap().getMapName().equalsIgnoreCase(getHomeMap())) {
            items = game.getGameMap().getItems();
        } else {
            items = game.getGameMap(homeMap).getItems();
        }
        for (Item item : items) {
            if (animalRectangle.intersects(item.getRectangle())) {
                logger.info(String.format("%s ate food", animalName));
                currentHunger = MAX_HUNGER;
                speed = 3;
                game.getGameMap(homeMap).removeItem(item.getItemName(), item.getRectangle());
                logger.debug(String.format("Hunger level for %s is 100 percent", animalName));
                return true;
            }
        }
        List<FoodBowl> bowls;
        if (game.getGameMap().getMapName().equalsIgnoreCase(getHomeMap())) {
            bowls = game.getGameMap().getFoodBowls();
        } else {
            bowls = game.getGameMap(homeMap).getFoodBowls();
        }
        for (FoodBowl bowl : bowls) {
            if (bowl.isFull() && animalRectangle.intersects(bowl.getRectangle())) {
                logger.info(String.format("%s ate food", animalName));
                currentHunger = MAX_HUNGER;
                speed = 3;
                bowl.emptyBowl();
                logger.debug(String.format("Hunger level for %s is 100 percent", animalName));
                return true;
            }
        }
        return false;
    }

    private void handleMoving(GameMap gameMap, Direction direction) {
        if (unwalkableInThisDirection(gameMap, direction)) {
            route = new Route();
            handleUnwalkable(direction);
            return;
        }

        switch (direction) {
            case LEFT:
                if (animalRectangle.getX() > 0 || nearPortal(gameMap.getPortals())) {
                    animalRectangle.setX(animalRectangle.getX() - speed);
                }
                break;
            case RIGHT:
                if (animalRectangle.getX() < (gameMap.getMapWidth() * TILE_SIZE - animalRectangle.getWidth()) * ZOOM || nearPortal(gameMap.getPortals())) {
                    animalRectangle.setX(animalRectangle.getX() + speed);
                }
                break;
            case UP:
                if (animalRectangle.getY() > 0 || nearPortal(gameMap.getPortals())) {
                    animalRectangle.setY(animalRectangle.getY() - speed);
                }
                break;
            case DOWN:
                if (animalRectangle.getY() < (gameMap.getMapHeight() * TILE_SIZE - animalRectangle.getHeight()) * ZOOM || nearPortal(gameMap.getPortals())) {
                    animalRectangle.setY(animalRectangle.getY() + speed);
                }
                break;
        }
    }

    private void handleUnwalkable(Direction direction) {
        movingTicks = 0;
        switch (direction) {
            case LEFT:
                animalRectangle.setX(animalRectangle.getX() + speed);
                break;
            case RIGHT:
                animalRectangle.setX(animalRectangle.getX() - speed);
                break;
            case UP:
                animalRectangle.setY(animalRectangle.getY() + speed);
                break;
            case DOWN:
                animalRectangle.setY(animalRectangle.getY() - speed);
                break;
        }
    }

    Direction getRandomDirection() {
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

    private int getRandomMovingTicks() {
        return random.nextInt(20) + 64;
    }

    private int getMovingTickToAdjustPosition(Direction direction) {
        switch (direction) {
            case DOWN:
                return (((TILE_SIZE * ZOOM) - (getCurrentY() % (TILE_SIZE * ZOOM))) / speed) + speed;
            case UP:
                return (((TILE_SIZE * ZOOM) + (getCurrentY() % (TILE_SIZE * ZOOM))) / speed) - speed;
            case RIGHT:
                return (((TILE_SIZE * ZOOM) - (getCurrentX() % (TILE_SIZE * ZOOM))) / speed) + speed;
            case LEFT:
                return (((TILE_SIZE * ZOOM) + (getCurrentX() % (TILE_SIZE * ZOOM))) / speed) - speed;
            default:
                return (TILE_SIZE * ZOOM) / speed;
        }
    }

    private boolean unwalkableInThisDirection(GameMap gameMap, Direction direction) {
        int xPosition = animalRectangle.getX();
        int yPosition = animalRectangle.getY();

        List<MapTile> tilesOnLayer = gameMap.getTilesOnLayer(getLayer());

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
                if (animalRectangle.potentialIntersects(tile, xPosition, yPosition)) {
                    return true;
                }
            }
        }

        List<MapTile> portals = gameMap.getPortals();
        if (portals != null) {
            for (MapTile portal : portals) {
                if (this instanceof Butterfly && HOME_MAPS.contains(portal.getPortalDirection()) && animalRectangle.potentialIntersects(portal, xPosition, yPosition)) {
                    return true;
                }
                if (MAIN_MAP.equals(getHomeMap()) && portal.getPortalDirection().startsWith("Bottom") && animalRectangle.potentialIntersects(portal, xPosition, yPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean nearPortal(List<MapTile> portals) {
        for (MapTile portal : portals) {
            logger.debug(String.format("Portal X: %d player X: %d", portal.getX() * (TILE_SIZE * ZOOM), animalRectangle.getX()));
            int diffX = portal.getX() * (TILE_SIZE * ZOOM) - animalRectangle.getX();
            logger.debug(String.format("diff x: %d", diffX));
            int diffY = portal.getY() * (TILE_SIZE * ZOOM) - animalRectangle.getY();
            logger.debug(String.format("diff y: %d", diffY));
            if (Math.abs(diffX) <= TILE_SIZE * ZOOM && Math.abs(diffY) <= TILE_SIZE * ZOOM) {
                return true;
            }
        }
        return false;
    }

    private void checkPortals(Game game) {
        if (game.getGameMap(homeMap).getPortals() != null) {
            for (MapTile tile : game.getGameMap(homeMap).getPortals()) {
                if (animalRectangle.intersects(tile)) {
                    game.moveAnimalToAnotherMap(this, tile);
                }
            }
        }
    }

    public boolean isAnimalStuck(GameMap gameMap) {
        return unwalkableInThisDirection(gameMap, LEFT)
                && unwalkableInThisDirection(gameMap, RIGHT)
                && unwalkableInThisDirection(gameMap, UP)
                && unwalkableInThisDirection(gameMap, DOWN);
    }

    public void tryToMove(GameMap gameMap) {
        logger.info(String.format("Animal %s is stuck, will try to move to nearest directions", this));
        route = new Route();
        for (Direction potentialDirection : Direction.values()) {
            if (!unwalkableInThisDirection(gameMap, potentialDirection)) {
                moveAnimalTo(potentialDirection);
            }
        }
        if (isAnimalStuck(gameMap)) {
            logger.info("Animal still stuck, will try to move to center");
            animalRectangle.setX(gameMap.getMapWidth() * TILE_SIZE * ZOOM / 2);
            animalRectangle.setY(gameMap.getMapHeight() * TILE_SIZE * ZOOM / 2);
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
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom, Game game) {
        if (mouseRectangle.intersects(animalRectangle)) {
            logger.info("Click on Animal: ");
            tryToMove(game.getGameMap());
            return true;
        }
        return false;
    }

    public String getHomeMap() {
        return homeMap;
    }

    public void setHomeMap(String homeMap) {
        this.homeMap = homeMap;
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

    public String getAnimalName() {
        return animalName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
