package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import base.map.GameMap;
import base.map.MapTile;
import base.navigationservice.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

import static base.Game.TILE_SIZE;
import static base.Game.ZOOM;
import static base.navigationservice.Direction.*;

public class Animal implements GameObject {

    private final Sprite sprite;
    private AnimatedSprite animatedSprite = null;
    protected final Rectangle animalRectangle;
    protected int speed;
    private Direction direction;
    private int movingTicks = 0;
    private Random random;
    private String homeMap;

    protected static final Logger logger = LoggerFactory.getLogger(Animal.class);

    public Animal(Sprite playerSprite, int startX, int startY, int speed) {
        this(playerSprite, startX, startY, speed, "MainMap");
    }

    public Animal(Sprite playerSprite, int startX, int startY, int speed, String homeMap) {
        this.sprite = playerSprite;
        this.homeMap = homeMap;
        this.speed = speed;

        if (playerSprite instanceof AnimatedSprite) {
            animatedSprite = (AnimatedSprite) playerSprite;
        }

        direction = DOWN;
        updateDirection();
        animalRectangle = new Rectangle(startX, startY, TILE_SIZE, TILE_SIZE);
        animalRectangle.generateGraphics(1, 123);

        random = new Random();
    }

    private void updateDirection() {
        if (animatedSprite != null && direction != STAY) {
//            animatedSprite.setAnimationRange(direction.directionNumber, direction.directionNumber + 12);          // if vertical
            animatedSprite.setAnimationRange((direction.directionNumber * 3), (direction.directionNumber * 3 + 2)); //if horizontal increase
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
        Direction randomDirection = direction;

        if (movingTicks < 1) {
            randomDirection = getRandomDirection();
            movingTicks = getRandomMovingTicks();
        }

        handleMoving(game.getGameMap(), randomDirection);
        if (randomDirection != STAY) {
            isMoving = true;
        }

        if (randomDirection != direction) {
            direction = randomDirection;
            updateDirection();
        }

        if (animatedSprite != null) {
            if (isMoving) {
                animatedSprite.update(game);
            } else {
                animatedSprite.reset();
            }
        }
        movingTicks--;
    }

    protected void handleMoving(GameMap gameMap, Direction direction) {
        if (unwalkableInThisDirection(gameMap, direction)) {
            handleUnwalkable(direction);
            return;
        }

        switch (direction) {
            case LEFT:
                if (animalRectangle.getX() > 0) {
                    animalRectangle.setX(animalRectangle.getX() - speed);
                }
                break;
            case RIGHT:
                if (animalRectangle.getX() < (gameMap.getMapWidth() * TILE_SIZE - animalRectangle.getWidth()) * Game.ZOOM) {
                    animalRectangle.setX(animalRectangle.getX() + speed);
                }
                break;
            case UP:
                if (animalRectangle.getY() > 0) {
                    animalRectangle.setY(animalRectangle.getY() - speed);
                }
                break;
            case DOWN:
                if (animalRectangle.getY() < (gameMap.getMapHeight() * TILE_SIZE - animalRectangle.getHeight()) * Game.ZOOM) {
                    animalRectangle.setY(animalRectangle.getY() + speed);
                }
                break;
        }
    }

    protected void handleUnwalkable(Direction direction) {
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

    protected boolean unwalkableInThisDirection(GameMap gameMap, Direction direction) {
        int xPosition = animalRectangle.getX();
        int yPosition = animalRectangle.getY();

        List<MapTile> tilesOnLayer = gameMap.getTilesOnLayer(getLayer());

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
        if (tilesOnLayer != null) {
            for (MapTile tile : tilesOnLayer) {
                if (animalRectangle.potentialIntersects(tile, xPosition, yPosition)) {
                    return true;
                }
            }
        }
        return false;

    }

    public boolean isAnimalStuck(GameMap gameMap) {
        return unwalkableInThisDirection(gameMap, LEFT)
                && unwalkableInThisDirection(gameMap, RIGHT)
                && unwalkableInThisDirection(gameMap, UP)
                && unwalkableInThisDirection(gameMap, DOWN);
    }

    public void tryToMove(GameMap gameMap) {
        logger.info(String.format("Animal %s is stuck, will try to move to nearest directions", this));
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
            logger.info("Moving to center didn't work, will try move to left up to 5 times");
            int attempts = 0;
            while (isAnimalStuck(gameMap) && attempts <= 5) {
                moveAnimalTo(LEFT);
                attempts++;
            }
            if (isAnimalStuck(gameMap)) {
                logger.error("Animal is stuck completely");
//                throw new IllegalStateException();
            }
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
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
        logger.info("Click on Animal: ");
        return false;
    }

    public String getHomeMap() {
        return homeMap;
    }

    public void setHomeMap(String homeMap) {
        this.homeMap = homeMap;
    }

    public Sprite getSprite() {
        if (animatedSprite != null) {
            return animatedSprite.getStartSprite();
        }
        return null;
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
}
