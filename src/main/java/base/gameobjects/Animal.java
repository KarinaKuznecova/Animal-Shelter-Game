package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import base.map.MapTile;
import base.navigationservice.Direction;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.Game.TILE_SIZE;
import static base.Game.ZOOM;
import static base.navigationservice.Direction.*;

public class Animal implements GameObject {

    private final Sprite sprite;
    private AnimatedSprite animatedSprite = null;
    private final Rectangle animalRectangle;
    private int speed;
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

        handleMoving(game, randomDirection);
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

    private void handleMoving(Game game, Direction direction) {
        if (unwalkableInThisDirection(game, direction)) {
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
                if (animalRectangle.getX() < (game.getGameMap().getMapWidth() * TILE_SIZE - animalRectangle.getWidth()) * Game.ZOOM) {
                    animalRectangle.setX(animalRectangle.getX() + speed);
                }
                break;
            case UP:
                if (animalRectangle.getY() > 0) {
                    animalRectangle.setY(animalRectangle.getY() - speed);
                }
                break;
            case DOWN:
                if (animalRectangle.getY() < (game.getGameMap().getMapHeight() * TILE_SIZE - animalRectangle.getHeight()) * Game.ZOOM) {
                    animalRectangle.setY(animalRectangle.getY() + speed);
                }
                break;
        }
    }

    private void handleUnwalkable(Direction direction) {
        logger.info("Animal can't walk this way");
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

    private boolean unwalkableInThisDirection(Game game, Direction direction) {
        int xPosition = animalRectangle.getX();
        int yPosition = animalRectangle.getY();

        List<MapTile> tilesOnLayer = game.getGameMap().getTilesOnLayer(getLayer());

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

    public boolean isAnimalStuck(Game game) {
        return unwalkableInThisDirection(game, LEFT)
                && unwalkableInThisDirection(game, RIGHT)
                && unwalkableInThisDirection(game, UP)
                && unwalkableInThisDirection(game, DOWN);
    }

    public void tryToMove(Game game) {
        logger.info(String.format("Animal %s is stuck, will try to move to left, 5 attempts", this));
        int attempts = 0;
        while (isAnimalStuck(game) && attempts <= 5) {
            moveAnimalTo(animalRectangle.getX() + (TILE_SIZE * ZOOM), animalRectangle.getY());
            attempts++;
        }
        if (isAnimalStuck(game)) {
            logger.info("Animal still stuck, will try to move to center");
            animalRectangle.setX(game.getWidth() / 2);
            animalRectangle.setY(game.getHeight() / 2);
        }
        if (isAnimalStuck(game)) {
            logger.error("Animal is stuck completely");
            throw new IllegalStateException();
        }
    }

    public void moveAnimalTo(int x, int y) {
        animalRectangle.setX(x);
        animalRectangle.setY(y);
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
        logger.info("Click on Animal: ");
        return false;
    }

    public String getHomeMap() {
        return homeMap;
    }
}
