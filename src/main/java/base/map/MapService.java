package base.map;

import base.gameobjects.Plant;
import base.gameobjects.Portal;
import base.navigationservice.Direction;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static base.constants.Constants.CELL_SIZE;
import static base.constants.FilePath.JSON_MAPS_DIRECTORY;
import static base.constants.FilePath.MAPS_LIST_PATH;

public class MapService {

    private final File mapListFile = new File(MAPS_LIST_PATH);
    private final Map<String, String> mapFiles = new HashMap<>();

    protected static final Logger logger = LoggerFactory.getLogger(MapService.class);

    private GameMapConverter gameMapConverter = new GameMapConverter();

    public MapService() {
        logger.info("Loading maps list");
        loadMapList();
    }

    /**
     * =================================== Load Map Config ======================================
     */

    private void loadMapList() {
        try (Scanner scanner = new Scanner(mapListFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitLine = line.split(":");
                if (splitLine.length >= 1) {
                    mapFiles.put(splitLine[0], splitLine[1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllMapsNames() {
        return new ArrayList<>(mapFiles.keySet());
    }

    /**
     * =================================== Save Map ======================================
     */

    public void saveMapToJson(GameMap gameMap) {
        Gson gson = new Gson();
        try {
            File directory = new File(JSON_MAPS_DIRECTORY);
            if (!directory.exists() && !directory.mkdirs()) {
                logger.error("Error while saving map to json file - cannot create directory");
                return;
            }
            FileWriter writer = new FileWriter(JSON_MAPS_DIRECTORY + gameMap.getMapName());
            GameMapDTO gameMapDTO = gameMapConverter.getGameMapDTO(gameMap);
            gson.toJson(gameMapDTO, writer);
            writer.flush();
            writer.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * =================================== Other ======================================
     */

    public Portal getPortalTo(GameMap gameMap, String destination) {
        for (Portal portal : gameMap.getPortals()) {
            if (portal.getDirection().equals(destination)) {
                return portal;
            }
        }
        return null;
    }

    public int getSpawnPoint(Portal portalToPrevious, boolean getX, Direction direction, GameMap gameMap) {
        int previousMapPortal;
        if (getX) {
            previousMapPortal = portalToPrevious.getRectangle().getX();
        } else {
            previousMapPortal = portalToPrevious.getRectangle().getY();
        }

        int mapSize;
        if (getX) {
            mapSize = gameMap.getMapWidth();
        } else {
            mapSize = gameMap.getMapHeight();
        }

        if (mapSize * CELL_SIZE - previousMapPortal < CELL_SIZE) {
            previousMapPortal = previousMapPortal - CELL_SIZE;
        }

        if (previousMapPortal < 0) {
            previousMapPortal = 0;
        }
        if (getX){
            if (direction == Direction.LEFT) {
                return previousMapPortal - 1;
            }
            if (direction == Direction.RIGHT) {
                return previousMapPortal + 1;
            }
        } else {
            if (direction == Direction.UP) {
                return previousMapPortal - 1;
            }
            if (direction == Direction.DOWN) {
                return previousMapPortal + 1;
            }
        }
        return previousMapPortal;
    }

    public boolean isThereGrassOrDirt(GameMap gameMap, int x, int y) {
        x = x / CELL_SIZE;
        y = y / CELL_SIZE;
        for (MapTile tile : gameMap.getLayeredTiles().get(0)) {
            if (tile.getX() == x && tile.getY() == y) {
                return getGrassTileIds().contains(tile.getId());
            }
        }
        if (getGrassTileIds().contains(gameMap.getBackGroundTileId()) && isPlaceEmpty(gameMap, 1, x, y) && isPlaceEmpty(gameMap, 2, x, y)) {
            return true;
        }
        logger.info("There is no grass");
        return false;
    }

    private List<Integer> getGrassTileIds() {
        return Arrays.asList(13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 158, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175);
    }

    public boolean isPlaceEmpty(GameMap gameMap, int layer, int x, int y) {
        if (isTherePlant(gameMap, x, y)) {
            return false;
        }
        if (gameMap.getLayeredTiles().get(layer) == null) {
            return true;
        }
        for (MapTile tile : gameMap.getLayeredTiles().get(layer)) {
            if (tile.getX() == x && tile.getY() == y) {
                logger.info("Place is taken");
                return false;
            }
        }
        return true;
    }

    public boolean isTherePlant(GameMap gameMap, int x, int y) {
        for (Plant plant : gameMap.getPlants()) {
            if (plant.getRectangle().getX() == x && plant.getRectangle().getY() == y) {
                logger.info("There is already a plant");
                return true;
            }
        }
        return false;
    }

    public boolean isInsideOfMap(GameMap gameMap, int x, int y) {
        if (x < 0 || x > gameMap.getMapWidth() || y < 0 || y > gameMap.getMapHeight()) {
            logger.info("Outside of modifiable map");
            return false;
        }
        return true;
    }

}
