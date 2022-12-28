package base.events;

import base.Game;
import base.gameobjects.Plant;
import base.gameobjects.services.PlantService;
import base.map.GameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.*;
import static base.constants.MapConstants.FOREST_MAP;
import static base.constants.MapConstants.MAIN_MAP;

public class GrowRandomPlantEvent extends Event {

    protected static final Logger logger = LoggerFactory.getLogger(GrowRandomPlantEvent.class);

    public GrowRandomPlantEvent() {
        repeatable = true;
    }

    @Override
    void calculateChance(Game game) {
        chance = 1 + random.nextInt(4);
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
        if (map.getWildPlants().size() > 5) {
            logger.info(String.format("There are more than 5 plants on %s map", map.getMapName()));
            return;
        }
        int x = random.nextInt(map.getMapWidth());
        int y = random.nextInt(map.getMapHeight());
        int bigX = x  * CELL_SIZE;
        int bigY = y  * CELL_SIZE;
        logger.info(String.format("Random plant will appear at %d and %d", x, y));
        if (map.isThereGrassOrDirt(bigX, bigY) && map.isPlaceEmpty(1, bigX, bigY)) {
            int plantId = random.nextInt(PlantService.plantTypes.size());
            logger.info(String.format("Place was empty, will add plant with id %d", plantId));
            Plant plant = game.getPlantService().createPlant(PlantService.plantTypes.get(plantId), bigX, bigY);
            plant.setWild(true);
            plant.setRefreshable(false);
            map.addPlant(plant);
            happened = true;
        }
    }
}
