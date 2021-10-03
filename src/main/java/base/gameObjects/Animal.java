package base.gameObjects;

import base.Game;
import base.graphicsService.Rectangle;
import base.graphicsService.RenderHandler;
import base.graphicsService.Sprite;
import base.map.MapTile;
import base.navigationService.Direction;

import java.util.List;
import java.util.Random;

import static base.navigationService.Direction.*;

public class Animal implements GameObject{

    private Sprite sprite;
    private AnimatedSprite animatedSprite = null;
    private Rectangle animalRectangle;
    private int speed = 1;
    private Direction direction;
    private int movingTicks = 0;
    private Random random;

    public Animal(Sprite playerSprite, int startX, int startY) {
        this.sprite = playerSprite;

        if (playerSprite instanceof AnimatedSprite) {
            animatedSprite = (AnimatedSprite) playerSprite;
        }

        direction = DOWN;
        updateDirection();
        animalRectangle = new Rectangle(startX, startY, Game.TILE_SIZE, Game.TILE_SIZE);
        animalRectangle.generateGraphics(1, 123);
    }

    private void updateDirection() {
        if (animatedSprite != null && direction != null) {
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

//        System.out.println("base.gameObjects.Player x: " + playerRectangle.getX() + " base.gameObjects.Player Y: " + playerRectangle.getY());
        if (LEFT == randomDirection) {
            if (unwalkableInThisDirection(game, LEFT)) {
                System.out.println("Animal can't walk this way");
                movingTicks = 0;
                animalRectangle.setX(animalRectangle.getX() + 1);
            } else if (animalRectangle.getX() > 0) {
                animalRectangle.setX(animalRectangle.getX() - speed);
            }
            newDirection = LEFT;
            isMoving = true;
        }

        if (RIGHT == randomDirection) {
            if (unwalkableInThisDirection(game, RIGHT)) {
                System.out.println("Animal can't walk this way");
                animalRectangle.setX(animalRectangle.getX() - 1);
                movingTicks = 0;
            } else if (animalRectangle.getX() < (game.getGameMap().mapWidth * Game.TILE_SIZE - animalRectangle.getWidth()) * Game.ZOOM) {
                animalRectangle.setX(animalRectangle.getX() + speed);
            }
            newDirection = RIGHT;
            isMoving = true;
        }

        if (UP == randomDirection) {
            if (unwalkableInThisDirection(game, UP)) {
                System.out.println("Animal can't walk this way");
                animalRectangle.setY(animalRectangle.getY() + 1);
                movingTicks = 0;
            } else if (animalRectangle.getY() > 0) {
                animalRectangle.setY(animalRectangle.getY() - speed);
            }
            newDirection = UP;
            isMoving = true;
        }

        if (DOWN == randomDirection) {
            if (unwalkableInThisDirection(game, DOWN)) {
                System.out.println("Animal can't walk this way");
                animalRectangle.setY(animalRectangle.getY() - 1);
                movingTicks = 0;
            } else if (animalRectangle.getY() < (game.getGameMap().mapHeight * Game.TILE_SIZE - animalRectangle.getHeight()) * Game.ZOOM) {
                animalRectangle.setY(animalRectangle.getY() + speed);
            }
            newDirection = DOWN;
            isMoving = true;
        }

        if (newDirection != direction) {
//            System.out.println("Animal Direction: " + direction);
//            System.out.println("Animal New direction: " + newDirection);
            direction = newDirection;
            updateDirection();
        }

        if (animatedSprite != null && !isMoving) {
            animatedSprite.reset();
        }

        if (animatedSprite != null && isMoving) {
            movingTicks--;
            animatedSprite.update(game);
        }

    }

    Direction getRandomDirection() {
        random = new Random();
        int result = random.nextInt(5);
//        System.out.println("random : " + result);
        switch (result) {
            case 0: return DOWN;
            case 1: return LEFT;
            case 2: return UP;
            case 3: return RIGHT;
            default:
                return null;
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
        System.out.println("Click on animal " + this);
        return false;
    }
}
