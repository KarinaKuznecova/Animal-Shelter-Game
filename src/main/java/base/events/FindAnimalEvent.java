package base.events;

import base.Game;
import base.constants.MapConstants;
import base.gameobjects.Animal;
import base.gameobjects.ChestWithAnimal;
import base.gameobjects.services.AnimalService;
import base.map.GameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static base.constants.Constants.*;

public class FindAnimalEvent extends Event {

    private static final Logger logger = LoggerFactory.getLogger(FindAnimalEvent.class);

    public FindAnimalEvent() {
        repeatable = true;
    }

    @Override
    void calculateChance(Game game) {
        chance = 5;
        if (!MapConstants.BOTTOM_MAPS.contains(game.getGameMap().getMapName()) || (!repeatable && happened)) {
            chance = 0;
            return;
        }
        if (happened) {
            chance--;
        }
        if (MapConstants.BOTTOM_LEFT_MAP.equals(game.getGameMap().getMapName()) || MapConstants.BOTTOM_CENTER_MAP.equals(game.getGameMap().getMapName())) {
            chance++;
        }
        if (game.isBackpackEmpty()) {
            chance--;
        } else {
            chance++;
        }
        for (List<Animal> animalList : game.getAnimalsOnMaps().values()) {
            chance -= animalList.size();
        }
        logger.info(String.format("Event 'Find Random Animal' chance is %d", chance));
    }

    @Override
    void startEvent(Game game) {
        GameMap map = game.getGameMap();
        int x = random.nextInt(map.getMapWidth() - 1);
        int y = random.nextInt(map.getMapHeight() - 2);

        logger.info(String.format("Random chest will appear at %d and %d", x, y));

        int bigX = x * CELL_SIZE;
        int bigY = y * CELL_SIZE;
        if (game.getMapService().isPlaceEmpty(map, 2, bigX, bigY)) {
            logger.info("Place was empty, will put chest");
            AnimalService animalService = game.getAnimalService();
            String animalType = animalService.getRandomAnimalType();
            logger.info(String.format("Random animal is %s", animalType));
            Animal animal = animalService.createAnimal(bigX, bigY + 64, animalType, game.getGameMap().getMapName());
            map.addObject(new ChestWithAnimal(bigX, bigY, animal, game.getTileService().getTiles().get(36).getSprite()));
            game.saveMaps();
        }
        happened = true;
        logger.info("Event for new animal started");
    }
}
