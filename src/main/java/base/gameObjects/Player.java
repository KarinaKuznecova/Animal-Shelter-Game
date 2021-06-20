package base.gameObjects;

import base.Game;
import base.graphicsService.AnimatedSprite;
import base.graphicsService.Rectangle;
import base.graphicsService.RenderHandler;
import base.graphicsService.Sprite;
import base.map.MapService;
import base.map.MapTile;
import base.navigationService.Direction;
import base.navigationService.KeyboardListener;

import java.util.List;

import static base.navigationService.Direction.*;

public class Player implements GameObject {

    private Sprite sprite;
    private AnimatedSprite animatedSprite = null;
    private Rectangle playerRectangle;
    private int speed = 5;
    private Direction direction;

    public Player(Sprite playerSprite, int startX, int startY) {
        this.sprite = playerSprite;

        if (playerSprite instanceof AnimatedSprite) {
            animatedSprite = (AnimatedSprite) playerSprite;
        }

        updateDirection();
        playerRectangle = new Rectangle(startX, startY, Game.TILE_SIZE, Game.TILE_SIZE);
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

    // TODO: too long, needs refactoring
    @Override
    public void update(Game game) {
        KeyboardListener keyboardListener = game.getKeyboardListener();

        boolean isMoving = false;
        Direction newDirection = direction;

//        System.out.println("Player x: " + playerRectangle.getX() + " Player Y: " + playerRectangle.getY());
        if (keyboardListener.left()) {
            if (unwalkableInThisDirection(game, LEFT)) {
                System.out.println("INTERSECTS left");
                playerRectangle.setX(playerRectangle.getX() + 1);
            } else if (playerRectangle.getX() < 1 && !game.getGameMap().getPortals().isEmpty()) {
                List<MapTile> portals = game.getGameMap().getPortals();
                if (nearPortal(portals)) {
                    playerRectangle.setX(playerRectangle.getX() - speed);
                }
            } else if (playerRectangle.getX() > 0) {
                playerRectangle.setX(playerRectangle.getX() - speed);
            }
            newDirection = LEFT;
            isMoving = true;
        }

        if (keyboardListener.right()) {
            if (unwalkableInThisDirection(game, RIGHT)) {
                System.out.println("INTERSECTS right");
                playerRectangle.setX(playerRectangle.getX() - 1);
            } else if (playerRectangle.getX() >= game.getGameMap().mapWidth * Game.TILE_SIZE * Game.ZOOM - playerRectangle.getWidth() * Game.ZOOM
                    && !game.getGameMap().getPortals().isEmpty()) {
                List<MapTile> portals = game.getGameMap().getPortals();
                if (nearPortal(portals)) {
                    playerRectangle.setX(playerRectangle.getX() + speed);
                }
            } else if (playerRectangle.getX() < (game.getGameMap().mapWidth * Game.TILE_SIZE - playerRectangle.getWidth()) * Game.ZOOM) {
                playerRectangle.setX(playerRectangle.getX() + speed);
            }
            newDirection = RIGHT;
            isMoving = true;
        }

        if (keyboardListener.up()) {
            if (unwalkableInThisDirection(game, UP)) {
                System.out.println("INTERSECTS up");
                playerRectangle.setY(playerRectangle.getY() + 1);
            } else if (playerRectangle.getY() > 0) {
                playerRectangle.setY(playerRectangle.getY() - speed);
            }
            newDirection = UP;
            isMoving = true;
        }

        if (keyboardListener.down()) {
            if (unwalkableInThisDirection(game, DOWN)) {
                System.out.println("INTERSECTS down");
                playerRectangle.setY(playerRectangle.getY() - 1);
            } else if (playerRectangle.getY() < (game.getGameMap().mapHeight * Game.TILE_SIZE - playerRectangle.getHeight()) * Game.ZOOM) {
                playerRectangle.setY(playerRectangle.getY() + speed);
            }
            newDirection = DOWN;
            isMoving = true;
        }

        if (newDirection != direction) {
            System.out.println("Direction: " + direction);
            System.out.println("New direction: " + newDirection);
            direction = newDirection;
            updateDirection();
        }

        if (animatedSprite != null && !isMoving) {
            animatedSprite.reset();
        }


        if (game.getGameMap().getPortals() != null) {
            for (MapTile tile : game.getGameMap().getPortals()) {
                if (playerRectangle.intersects(tile)) {
                    System.out.println("In portal");
                    MapService mapService = new MapService();
                    String mapFileLocation = mapService.getMapConfig(tile.getPortalDirection());
                    game.loadSecondaryMap(mapFileLocation);
                }
            }
        }
        updateCamera(game);

        if (animatedSprite != null && isMoving) {
            animatedSprite.update(game);
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
            System.out.println("Portal X: " + portal.getX() * (Game.TILE_SIZE * Game.ZOOM) + " player X: " + playerRectangle.getX());
            int diffX = portal.getX() * (Game.TILE_SIZE * Game.ZOOM) - playerRectangle.getX();
            System.out.println("diff x: " + diffX);
            int diffY = portal.getY() * (Game.TILE_SIZE * Game.ZOOM) - playerRectangle.getY();
            System.out.println("diff y: " + diffY);
            if (Math.abs(diffX) <= Game.TILE_SIZE * Game.ZOOM && Math.abs(diffY) <= Game.TILE_SIZE * Game.ZOOM) {
                return true;
            }
        }
        return false;
    }

    // When you half screen height to the end of map + bonus 128
    private boolean shouldUpdateYCamera(Game game) {
        int screenHeight = game.getHeight();
        int halfHeight = screenHeight / 2;
        int mapEnd = game.getGameMap().mapHeight * (Game.TILE_SIZE * Game.ZOOM);
        int diffToEnd = mapEnd - playerRectangle.getY();
        return diffToEnd + 96 > halfHeight && playerRectangle.getY() + (Game.TILE_SIZE * Game.ZOOM) > halfHeight;
    }

    private boolean shouldUpdateXCamera(Game game) {
        int screenWidth = game.getWidth();
        int halfWidth = screenWidth / 2;
        int mapEnd = game.getGameMap().mapWidth * (Game.TILE_SIZE * Game.ZOOM);
        int diffToEnd = mapEnd - playerRectangle.getX();
        return diffToEnd + 64 > halfWidth && playerRectangle.getX() + (Game.TILE_SIZE * Game.ZOOM) > halfWidth;
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

    @Override
    public int getLayer() {
        return 1;
    }
}
