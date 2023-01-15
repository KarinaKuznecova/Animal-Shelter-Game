package base.events;

import base.Game;
import base.gameobjects.Flower;
import base.graphicsservice.Sprite;
import base.map.GameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.CELL_SIZE;
import static base.constants.MapConstants.CITY_MAP;
import static base.constants.MapConstants.HOME_MAP;
import static base.constants.MultiOptionalObjects.flowerIds;

public class GrowFlowerEvent extends Event{

    protected static final Logger logger = LoggerFactory.getLogger(GrowFlowerEvent.class);

    public GrowFlowerEvent() {
        repeatable = true;
    }

    @Override
    void calculateChance(Game game) {
        chance = random.nextInt(6);
        if ((!repeatable && happened) || (HOME_MAP.equals(game.getGameMap().getMapName()) || CITY_MAP.equals(game.getGameMap().getMapName()))) {
            chance = 0;
            return;
        }
        if (happened) {
            chance--;
        }
        logger.info(String.format("Event 'Grow Random Flower' chance is %d", chance));
    }

    @Override
    void startEvent(Game game) {
        GameMap map = game.getGameMap();
        int x = random.nextInt(map.getMapWidth());
        int y = random.nextInt(map.getMapHeight());
        int bigX = x  * CELL_SIZE;
        int bigY = y  * CELL_SIZE;
        logger.info(String.format("Random flower will appear at %d and %d", x, y));
        if (game.getMapService().isThereGrassOrDirt(map, bigX, bigY) && game.getMapService().isPlaceEmpty(map, 1, bigX, bigY)) {
            int flowerId = random.nextInt(flowerIds.size());
            logger.info(String.format("Place was empty, will add flower with id %d", flowerId));
            Sprite sprite = game.getTileService().getTiles().get(flowerId).getSprite();
            game.getGameMap().addObject(new Flower(sprite, bigX, bigY, map.getMapName()));
            happened = true;
        }
    }

}
