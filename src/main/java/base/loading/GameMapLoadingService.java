package base.loading;

import base.Game;
import base.gameobjects.Animal;
import base.map.GameMap;
import base.map.GameMapConverter;
import base.map.GameMapDTO;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static base.constants.Constants.TEST_MAP_MODE;
import static base.constants.FilePath.JSON_MAPS_DIRECTORY;
import static base.constants.MapConstants.MAIN_MAP;
import static base.constants.MapConstants.TEST_MAP;

public class GameMapLoadingService {

    private static final Logger logger = LoggerFactory.getLogger(GameMapLoadingService.class);

    private final MapMigrator mapMigrator = new MapMigrator();
    private final GameMapConverter gameMapConverter = new GameMapConverter();

    public GameMap loadMap(Game game) {
        GameMap gameMap;
        if (TEST_MAP_MODE) {
            gameMap = loadMap(game, TEST_MAP);
            game.getGameMaps().put(TEST_MAP, gameMap);
            return gameMap;
        } else {
            gameMap = loadMap(game, MAIN_MAP);
        }
        if (!TEST_MAP_MODE) {
            initialCacheMaps(game);
        }

        return gameMap;
    }

    public GameMap loadMap(Game game, String mapName) {
        logger.info("Game map loading started");

        GameMap gameMap = loadGameMapFromJson(mapName);

        game.getStorageService().loadStorageChests(gameMap);
        game.getLoadingService().getSpritesLoadingService().setSpritesToGameMapObjects(game, gameMap);
        loadAnimalsOnMaps(game);
        game.getStorageService().cleanUpDisconnectedChests();

        logger.info("Game map loaded");

        return gameMap;
    }

    private GameMap loadGameMapFromJson(String mapName) {
        GameMapDTO gameMapDTO;
        GameMap gameMap = null;
        File directory = new File(JSON_MAPS_DIRECTORY);
        if (directory.listFiles() == null || directory.listFiles().length == 0) {
            throw new RuntimeException();
        }
        try {
            Gson gson = new Gson();
            Reader reader = new FileReader(JSON_MAPS_DIRECTORY + mapName);
            gameMapDTO = gson.fromJson(reader, GameMapDTO.class);
            gameMap = gameMapConverter.getGameMap(gameMapDTO);
            reader.close();
            mapMigrator.checkMigration(gameMap, gameMapDTO.getGameVersion());
            gameMap.sortGameObjects();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gameMap;
    }

    private void loadAnimalsOnMaps(Game game) {
        List<String> mapNames = game.getMapService().getAllMapsNames();
        for (String mapName : mapNames) {
            game.getAnimalsOnMaps().put(mapName, new CopyOnWriteArrayList<>());
        }
        List<Animal> animals = game.getAnimalService().loadAllAnimals();
        for (Animal animal : animals) {
            if (game.getAnimalsOnMaps().get(animal.getCurrentMap()) != null) {
                game.getAnimalsOnMaps().get(animal.getCurrentMap()).add(animal);
            } else {
                List<Animal> listForMap = new CopyOnWriteArrayList<>();
                listForMap.add(animal);
                game.getAnimalsOnMaps().put(animal.getCurrentMap(), listForMap);
            }
        }
    }

    private void initialCacheMaps(Game game) {
        for (String mapName : game.getMapService().getAllMapsNames()) {
            GameMap map = loadGameMapFromJson(mapName);
            game.getLoadingService().getSpritesLoadingService().setSpritesToGameMapObjects(game, map);
            game.getStorageService().loadStorageChests(map);
            game.getGameMaps().put(mapName, map);
        }
    }
}
