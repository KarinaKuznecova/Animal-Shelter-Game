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

import static base.navigationservice.Direction.*;

public class Animal implements GameObject{

    private Sprite sprite;
    private AnimatedSprite animatedSprite = null;
    private Rectangle animalRectangle;
    private int speed = 1;
    private Direction direction;
    private int movingTicks = 0;
    private Random random;
    private String homeMap;

    protected static final Logger logger = LoggerFactory.getLogger(Animal.class);

    public Animal(Sprite playerSprite, int startX, int startY) {
        this(playerSprite, startX, startY, "MainMap");
    }

    public Animal(Sprite playerSprite, int startX, int startY, String homeMap) {
        this.sprite = playerSprite;
        this.homeMap = homeMap;

        if (playerSprite instanceof AnimatedSprite) {
            animatedSprite = (AnimatedSprite) playerSprite;
        }

        direction = DOWN;
        updateDirection();
        animalRectangle = new Rectangle(startX, startY, Game.TILE_SIZE, Game.TILE_SIZE);
        animalRectangle.generateGraphics(1, 123);
    }

    private void updateDirection() {
        if (animatedSprite != null && direction != STAY) {
//            System.out.println("Direction now is " + direction);
//            System.out.println("range will be : " + (direction.directionNumber * 3) + " and : " + (direction.directionNumber * 3 + 2));

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

    //TODO: refactor this
    @Override
    public void update(Game game) {
        boolean isMoving = false;

        Direction newDirection = direction;

        Direction randomDirection = direction;

        if (movingTicks < 1) {
            randomDirection = getRandomDirection();
            movingTicks = 64;
        }

        if (LEFT == randomDirection) {
            if (unwalkableInThisDirection(game, LEFT)) {
                handleUnwalkable(LEFT);
            } else if (animalRectangle.getX() > 0) {
                animalRectangle.setX(animalRectangle.getX() - speed);
            }
            newDirection = LEFT;
            isMoving = true;
        }

        if (RIGHT == randomDirection) {
            if (unwalkableInThisDirection(game, RIGHT)) {
                handleUnwalkable(RIGHT);
            } else if (animalRectangle.getX() < (game.getGameMap().mapWidth * Game.TILE_SIZE - animalRectangle.getWidth()) * Game.ZOOM) {
                animalRectangle.setX(animalRectangle.getX() + speed);
            }
            newDirection = RIGHT;
            isMoving = true;
        }

        if (UP == randomDirection) {
            if (unwalkableInThisDirection(game, UP)) {
                handleUnwalkable(UP);
            } else if (animalRectangle.getY() > 0) {
                animalRectangle.setY(animalRectangle.getY() - speed);
            }
            newDirection = UP;
            isMoving = true;
        }

        if (DOWN == randomDirection) {
            if (unwalkableInThisDirection(game, DOWN)) {
                handleUnwalkable(DOWN);
            } else if (animalRectangle.getY() < (game.getGameMap().mapHeight * Game.TILE_SIZE - animalRectangle.getHeight()) * Game.ZOOM) {
                animalRectangle.setY(animalRectangle.getY() + speed);
            }
            newDirection = DOWN;
            isMoving = true;
        }

        if (STAY == randomDirection) {
            newDirection = STAY;
        }

        if (newDirection != direction) {
            direction = newDirection;
            updateDirection();
        }

        if (animatedSprite != null && !isMoving) {
            animatedSprite.reset();
            movingTicks--;
        }

        if (animatedSprite != null && isMoving) {
            movingTicks--;
            animatedSprite.update(game);
        }

    }

    private void handleUnwalkable(Direction direction) {
        logger.info("Animal can't walk this way");
        movingTicks = 0;
        switch (direction) {
            case LEFT:
                animalRectangle.setX(animalRectangle.getX() + 1);
                break;
            case RIGHT:
                animalRectangle.setX(animalRectangle.getX() - 1);
                break;
            case UP:
                animalRectangle.setY(animalRectangle.getY() + 1);
                break;
            case DOWN:
                animalRectangle.setY(animalRectangle.getY() - 1);
                break;
        }
    }

    Direction getRandomDirection() {
        random = new Random();
        int result = random.nextInt(5);
        switch (result) {
            case 0: return DOWN;
            case 1: return LEFT;
            case 2: return UP;
            case 3: return RIGHT;
            default:
                return STAY;
        }
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

    public void setHomeMap(String homeMap) {
        this.homeMap = homeMap;
    }
}
