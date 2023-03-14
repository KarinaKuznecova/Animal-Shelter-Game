package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.map.MapTile;
import base.navigationservice.Direction;
import base.navigationservice.KeyboardListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static base.constants.Constants.*;
import static base.navigationservice.Direction.*;
import static base.navigationservice.MapEdgesUtil.*;

public class Player implements GameObject {

    private final AnimatedSprite animatedSprite;
    private final Rectangle playerRectangle;
    private int speed = 5;
    private Direction direction;

    private static final Logger logger = LoggerFactory.getLogger(Player.class);

    public Player(AnimatedSprite playerSprite, int startX, int startY) {
        this.animatedSprite = playerSprite;

        updateDirection();
        playerRectangle = new Rectangle(startX, startY, PLAYER_SPRITE_SIZE, PLAYER_SPRITE_SIZE);
        playerRectangle.generateBorder(1, 123);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (animatedSprite != null) {
            renderer.renderSprite(animatedSprite, playerRectangle.getX() - 18, playerRectangle.getY() - 28, zoom, false);
        }
        if (DEBUG_MODE) {
            renderer.renderRectangle(playerRectangle, 1, false);
        }
    }

    @Override
    public void update(Game game) {
        KeyboardListener keyboardListener = game.getKeyboardListener();

        boolean isMoving = false;
        Direction newDirection = direction;

        if (keyboardListener.left()) {
            handleWalking(game, LEFT);
            newDirection = LEFT;
            isMoving = true;
        }

        if (keyboardListener.right()) {
            handleWalking(game, RIGHT);
            newDirection = RIGHT;
            isMoving = true;
        }

        if (keyboardListener.up()) {
            handleWalking(game, UP);
            newDirection = UP;
            isMoving = true;
        }

        if (keyboardListener.down()) {
            handleWalking(game, DOWN);
            newDirection = DOWN;
            isMoving = true;
        }

        if (newDirection != direction) {
            direction = newDirection;
            updateDirection();
        }

        checkPortals(game);
        updateCamera(game);

        if (animatedSprite != null) {
            if (isMoving) {
                animatedSprite.update(game);
            } else {
                animatedSprite.reset();
            }
        }
    }

    void handleWalking(Game game, Direction direction) {
        if (!nearPortal(game.getGameMap().getPortals()) && unwalkableInThisDirection(game, direction)) {
            handleUnwalkable(direction);
            return;
        }

        switch (direction) {
            case LEFT:
                if (playerRectangle.getX() >= getWestEdgeStrict() || nearPortal(game.getGameMap().getPortals())) {
                    playerRectangle.setX(playerRectangle.getX() - speed);
                }
                break;
            case RIGHT:
                if (playerRectangle.getX() <= getEastEdgeStrict(game.getGameMap().getMapWidth()) || nearPortal(game.getGameMap().getPortals())) {
                    playerRectangle.setX(playerRectangle.getX() + speed);
                }
                break;
            case UP:
                if (playerRectangle.getY() >= getNorthEdgeStrict() || nearPortal(game.getGameMap().getPortals())) {
                    playerRectangle.setY(playerRectangle.getY() - speed);
                }
                break;
            case DOWN:
                if (playerRectangle.getY() <= getSouthEdgeStrict(game.getGameMap().getMapHeight()) || nearPortal(game.getGameMap().getPortals())) {
                    playerRectangle.setY(playerRectangle.getY() + speed);
                }
                break;
        }
    }

    void handleUnwalkable(Direction direction) {
        switch (direction) {
            case LEFT:
                playerRectangle.setX(playerRectangle.getX() + 1);
                break;
            case RIGHT:
                playerRectangle.setX(playerRectangle.getX() - 1);
                break;
            case UP:
                playerRectangle.setY(playerRectangle.getY() + 1);
                break;
            case DOWN:
                playerRectangle.setY(playerRectangle.getY() - 1);
                break;
        }
    }

    private void checkPortals(Game game) {
        List<Portal> portals = game.getGameMap().getPortals();
        if (portals != null) {
            for (Portal portal : portals) {
                if (playerRectangle.intersects(portal.getRectangle())) {
                    game.loadSecondaryMap(portal.getDirection());
                }
            }
        }
    }

    private boolean unwalkableInThisDirection(Game game, Direction direction) {
        int xPosition = playerRectangle.getX();
        int yPosition = playerRectangle.getY();

        switch (direction) {
            case LEFT:
                xPosition = xPosition - (speed + 2);
                break;
            case RIGHT:
                xPosition = xPosition + (speed + 2);
                break;
            case UP:
                yPosition = yPosition - (speed + 2);
                break;
            case DOWN:
                yPosition = yPosition + (speed + 2);
                break;
        }
        List<MapTile> tilesOnLayer = game.getGameMap().getTilesOnLayer(getLayer());
        if (tilesOnLayer != null) {
            for (MapTile tile : tilesOnLayer) {
                if (playerRectangle.potentialIntersects(tile, xPosition, yPosition)) {
                    return true;
                }
            }
        }
        List<GameObject> gameObjects = new ArrayList<>();
        gameObjects.addAll(game.getGameMap().getSpruces());
        gameObjects.addAll(game.getGameMap().getOaks());
        gameObjects.addAll(game.getGameMap().getItems());
        gameObjects.addAll(game.getGameMap().getPlants());
        gameObjects.addAll(game.getGameMap().getFoodBowls());
        gameObjects.addAll(game.getGameMap().getStorageChests());
        gameObjects.addAll(game.getGameMap().getNpcSpots());
        gameObjects.addAll(game.getGameMap().getBushes());
        if (!gameObjects.isEmpty()) {
            Rectangle potentialRectangle = new Rectangle(xPosition, yPosition, playerRectangle.getWidth(), playerRectangle.getHeight());
            for (GameObject gameObject : gameObjects) {
                if (gameObject.getLayer() == getLayer() && potentialRectangle.intersects(gameObject.getRectangle())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean nearPortal(List<Portal> portals) {
        for (Portal portal : portals) {
            logger.debug(String.format("Portal X: %d player X: %d", portal.getRectangle().getX(), playerRectangle.getX()));
            int diffX = portal.getRectangle().getX() - playerRectangle.getX();
            logger.debug(String.format("diff x: %d", diffX));
            int diffY = portal.getRectangle().getY() - playerRectangle.getY();
            logger.debug(String.format("diff y: %d", diffY));
            if (Math.abs(diffX) <= CELL_SIZE + 5 && Math.abs(diffY) <= CELL_SIZE + 5) {
                return true;
            }
        }
        return false;
    }

    // When you half screen height to the end of map + bonus 128
    private boolean shouldUpdateYCamera(Game game) {
        int screenHeight = game.getHeight();
        int halfHeight = screenHeight / 2;
        int mapEnd = game.getGameMap().getMapHeight() * CELL_SIZE;
        int diffToEnd = mapEnd - playerRectangle.getY();
        return diffToEnd + 96 > halfHeight && playerRectangle.getY() + CELL_SIZE > halfHeight;
    }

    private boolean shouldUpdateXCamera(Game game) {
        int screenWidth = game.getWidth();
        int halfWidth = screenWidth / 2;
        int mapEnd = game.getGameMap().getMapWidth() * CELL_SIZE;
        int diffToEnd = mapEnd - playerRectangle.getX();
        return diffToEnd + CELL_SIZE > halfWidth && playerRectangle.getX() + CELL_SIZE > halfWidth;
    }

    public void updateCamera(Game game) {
        if (shouldUpdateXCamera(game)) {
            updateCameraX(game.getRenderer().getCamera());
        }
        if (shouldUpdateYCamera(game)) {
            updateCameraY(game.getRenderer().getCamera());
        }
    }

    public void updateCameraX(Rectangle camera) {
        camera.setX(playerRectangle.getX() - (camera.getWidth() / 2));
    }

    public void updateCameraY(Rectangle camera) {
        camera.setY(playerRectangle.getY() - (camera.getHeight() / 2));
    }

    private void updateDirection() {
        if (animatedSprite != null && direction != null) {
            animatedSprite.setAnimationRange(direction.directionNumber, direction.directionNumber + 12);
//            animatedSprite.setAnimationRange((direction * 4), (direction * 4 + 4)); //if horizontal increase
        }
    }

    public Rectangle getRectangle() {
        return playerRectangle;
    }

    @Override
    public int getLayer() {
        return 2;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        return false;
    }

    public void teleportToCenter(Game game) {
        teleportTo(game.getWidth() / 2, game.getHeight() / 2);
    }

    public void teleportTo(int x, int y) {
        playerRectangle.setX(x);
        playerRectangle.setY(y);
    }

    public Direction getDirection() {
        return direction;
    }
}
