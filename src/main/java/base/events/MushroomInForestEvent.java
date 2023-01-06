package base.events;

import base.Game;
import base.gameobjects.Mushroom;
import base.graphicsservice.Sprite;
import base.map.GameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.CELL_SIZE;
import static base.constants.MapConstants.FOREST_MAP;

public class MushroomInForestEvent extends Event {

    protected static final Logger logger = LoggerFactory.getLogger(MushroomInForestEvent.class);

    public MushroomInForestEvent() {
        repeatable = true;
    }

    @Override
    void calculateChance(Game game) {
        chance = random.nextInt(6);
        if ((!repeatable && happened) || game.getGameMap(FOREST_MAP).getMushrooms().size() > 5) {
            chance = 0;
            return;
        }
        if (happened) {
            chance--;
        }
    }

    @Override
    void startEvent(Game game) {
        GameMap map = game.getGameMap(FOREST_MAP);
        int x = random.nextInt(map.getMapWidth());
        int y = random.nextInt(map.getMapHeight());
        int bigX = x  * CELL_SIZE;
        int bigY = y  * CELL_SIZE;
        logger.info(String.format("Random mushroom will appear at %d and %d", x, y));
        if (map.isPlaceEmpty(1, bigX, bigY)) {
            logger.info("Place was empty, will add mushroom");
            Sprite sprite = game.getTileService().getTiles().get(Mushroom.TILE_ID).getSprite();
            map.addObject(new Mushroom(sprite, bigX, bigY));
            happened = true;
        }
    }
}
