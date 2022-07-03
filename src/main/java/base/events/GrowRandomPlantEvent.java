package base.events;

import base.Game;
import base.gameobjects.services.PlantService;
import base.map.GameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.Constants.ZOOM;
import static base.constants.MapConstants.FOREST_MAP;
import static base.constants.MapConstants.MAIN_MAP;

public class GrowRandomPlantEvent extends Event {

    protected static final Logger logger = LoggerFactory.getLogger(GrowRandomPlantEvent.class);

    public GrowRandomPlantEvent() {
        repeatable = true;
    }

    @Override
    void calculateChance(Game game) {
        chance = random.nextInt(5);
        if (!repeatable && happened) {
            chance = 0;
            return;
        }
        if (happened) {
            chance--;
        }
        logger.info(String.format("Event 'Grow Random Plant' chance is %d", chance));
    }

    @Override
    void startEvent(Game game) {
        GameMap map;
        int mapNumber = random.nextInt(4);
        if (mapNumber == 1) {
            map = game.getGameMap(MAIN_MAP);
        } else {
            map = game.getGameMap(FOREST_MAP);
        }

        int x = random.nextInt(map.getMapWidth());
        int y = random.nextInt(map.getMapHeight());
        int bigX = x  * (TILE_SIZE * ZOOM);
        int bigY = y  * (TILE_SIZE * ZOOM);
        logger.info(String.format("Random plant will appear at %d and %d", x, y));
        if (map.isThereGrassOrDirt(bigX, bigY) && map.isPlaceEmpty(1, bigX, bigY)) {
            PlantService plantService = game.getPlantService();
            int plantId = random.nextInt(plantService.plantTypes.size());
            logger.info(String.format("Place was empty, will add plant with id %d", plantId));
            map.addPlant(plantService.createPlant(plantService.plantTypes.get(plantId), bigX, bigY));
            happened = true;
        }
    }
}
