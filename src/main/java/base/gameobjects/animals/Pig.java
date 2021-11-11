package base.gameobjects.animals;

import static base.Game.TILE_SIZE;

import base.Game;
import base.gameobjects.Animal;
import base.graphicsservice.Rectangle;
import base.graphicsservice.Sprite;
import base.map.GameMap;
import base.navigationservice.Direction;

public class Pig extends Animal{

	public Pig(Sprite playerSprite, int startX, int startY, int speed) {
		super(playerSprite, startX, startY, speed);
	}

	@Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
        logger.info("Click on Animal: rat");
        return false;
    }
	
	protected void handleMoving(GameMap gameMap, Direction direction) {
        if (unwalkableInThisDirection(gameMap, direction)) {
            handleUnwalkable(direction);
            return;
        }

        switch (direction) {
            case RIGHT:
                if (animalRectangle.getX() > 0) {
                    animalRectangle.setX(animalRectangle.getX() - speed);
                }
                break;
            case LEFT:
                if (animalRectangle.getX() < (gameMap.getMapWidth() * TILE_SIZE - animalRectangle.getWidth()) * Game.ZOOM) {
                    animalRectangle.setX(animalRectangle.getX() + speed);
                }
                break;
            case DOWN:
                if (animalRectangle.getY() > 0) {
                    animalRectangle.setY(animalRectangle.getY() - speed);
                }
                break;
            case UP:
                if (animalRectangle.getY() < (gameMap.getMapHeight() * TILE_SIZE - animalRectangle.getHeight()) * Game.ZOOM) {
                    animalRectangle.setY(animalRectangle.getY() + speed);
                }
                break;
        }
    }
}
