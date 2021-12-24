package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import base.map.MapService;
import base.map.MapTile;
import base.navigationservice.Direction;
import base.navigationservice.KeyboardListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.Constants.ZOOM;
import static base.navigationservice.Direction.*;

public class Player implements GameObject {

    private final Sprite sprite;
    private AnimatedSprite animatedSprite = null;
    private final Rectangle playerRectangle;
    private int speed = 5;
    private Direction direction;

    private static final Logger logger = LoggerFactory.getLogger(Player.class);

    public Player(Sprite playerSprite, int startX, int startY) {
        this.sprite = playerSprite;

        if (playerSprite instanceof AnimatedSprite) {
            animatedSprite = (AnimatedSprite) playerSprite;
        }

        updateDirection();
        playerRectangle = new Rectangle(startX, startY, TILE_SIZE, TILE_SIZE);
        playerRectangle.generateGraphics(1, 123);
    }

    @Override
    public void render(RenderHandler renderer, int xZoom, int yZoom) {
        if (animatedSprite != null) {
            renderer.renderSprite(animatedSprite, playerRectangle.getX(), playerRectangle.getY(), xZoom, yZoom, false);
        } else if (sprite != null) {
            renderer.renderSprite(sprite, playerRectangle.getX(), playerRectangle.getY(), xZoom, yZoom, false);
        } else {
            renderer.renderRectangle(playerRectangle, xZoom, yZoom, false);
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
        if (unwalkableInThisDirection(game, direction)) {
            handleUnwalkable(direction);
            return;
        }

        switch (direction) {
            case LEFT:
                if (playerRectangle.getX() > 0 || nearPortal(game.getGameMap().getPortals())) {
                    playerRectangle.setX(playerRectangle.getX() - speed);
                }
                break;
            case RIGHT:
                if (playerRectangle.getX() < (game.getGameMap().getMapWidth() * TILE_SIZE - playerRectangle.getWidth()) * ZOOM || nearPortal(game.getGameMap().getPortals())) {
                    playerRectangle.setX(playerRectangle.getX() + speed);
                }
                break;
            case UP:
                if (playerRectangle.getY() > 0 || nearPortal(game.getGameMap().getPortals())) {
                    playerRectangle.setY(playerRectangle.getY() - speed);
                }
                break;
            case DOWN:
                if (playerRectangle.getY() < (game.getGameMap().getMapHeight() * TILE_SIZE - playerRectangle.getHeight()) * ZOOM || nearPortal(game.getGameMap().getPortals())) {
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
        if (game.getGameMap().getPortals() != null) {
            for (MapTile tile : game.getGameMap().getPortals()) {
                if (playerRectangle.intersects(tile)) {
                    game.loadSecondaryMap(tile.getPortalDirection());
                }
            }
        }
    }

    private boolean unwalkableInThisDirection(Game game, Direction direction) {
        int xPosition = playerRectangle.getX();
        int yPosition = playerRectangle.getY();

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
                if (playerRectangle.potentialIntersects(tile, xPosition, yPosition)) {
                    return true;
                }
            }
        }
        return false;

    }

    private boolean nearPortal(List<MapTile> portals) {
        for (MapTile portal : portals) {
            logger.debug(String.format("Portal X: %d player X: %d", portal.getX() * (TILE_SIZE * ZOOM), playerRectangle.getX()));
            int diffX = portal.getX() * (TILE_SIZE * ZOOM) - playerRectangle.getX();
            logger.debug(String.format("diff x: %d", diffX));
            int diffY = portal.getY() * (TILE_SIZE * ZOOM) - playerRectangle.getY();
            logger.debug(String.format("diff y: %d", diffY));
            if (Math.abs(diffX) <= TILE_SIZE * ZOOM && Math.abs(diffY) <= TILE_SIZE * ZOOM) {
                return true;
            }
        }
        return false;
    }

    // When you half screen height to the end of map + bonus 128
    private boolean shouldUpdateYCamera(Game game) {
        int screenHeight = game.getHeight();
        int halfHeight = screenHeight / 2;
        int mapEnd = game.getGameMap().getMapHeight() * (TILE_SIZE * ZOOM);
        int diffToEnd = mapEnd - playerRectangle.getY();
        return diffToEnd + 96 > halfHeight && playerRectangle.getY() + (TILE_SIZE * ZOOM) > halfHeight;
    }

    private boolean shouldUpdateXCamera(Game game) {
        int screenWidth = game.getWidth();
        int halfWidth = screenWidth / 2;
        int mapEnd = game.getGameMap().getMapWidth() * (TILE_SIZE * ZOOM);
        int diffToEnd = mapEnd - playerRectangle.getX();
        return diffToEnd + (TILE_SIZE * ZOOM) > halfWidth && playerRectangle.getX() + (TILE_SIZE * ZOOM) > halfWidth;
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

    public Rectangle getPlayerRectangle() {
        return playerRectangle;
    }

    @Override
    public int getLayer() {
        return 2;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom, Game game) {
        return false;
    }

    public void teleportToCenter(Game game) {
        teleportTo(game.getWidth() / 2, game.getHeight() / 2);
    }

    public void teleportTo(int x, int y) {
        playerRectangle.setX(x);
        playerRectangle.setY(y);
    }
}
