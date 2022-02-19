package base.map;

import base.gameobjects.*;
import base.gameobjects.plants.Corn;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static base.constants.Constants.*;
import static base.constants.FilePath.MAPS_LIST_PATH;

public class MapService {

    private File mapListFile = new File(MAPS_LIST_PATH);
    private Map<String, String> mapFiles = new HashMap<>();
    private PlantService plantService = new PlantService();

    protected static final Logger logger = LoggerFactory.getLogger(MapService.class);

    public MapService() {
        logger.info("Loading maps list");
        loadMapList();
    }

    private void loadMapList() {
        try {
            Scanner scanner = new Scanner(mapListFile);
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

    public String getMapConfig(String mapName) {
        return mapFiles.get(mapName);
    }

    public List<String> getAllMapsNames() {
        return new ArrayList<>(mapFiles.keySet());
    }

    public GameMap loadGameMap(String mapName, TileService tileService) {
        GameMap gameMap = new GameMap(mapName, tileService);
        try (Scanner scanner = new Scanner(new File(getMapConfig(mapName)))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (handleConfigLines(gameMap, line)) {
                    continue;
                }

                if (loadObjects(gameMap, line)) {
                    continue;
                }

                String[] splitLine = line.split(",");
                if (splitLine.length >= 4) {
                    int layer = Integer.parseInt(splitLine[0]);
                    List<MapTile> tiles;
                    if (gameMap.getLayeredTiles().containsKey(layer)) {
                        tiles = gameMap.getLayeredTiles().get(layer);
                    } else {
                        tiles = new ArrayList<>();
                        gameMap.getLayeredTiles().put(layer, tiles);
                    }
                    if (gameMap.getMaxLayer() < layer) {
                        gameMap.setMaxLayer(layer);
                        logger.debug(String.format("max layer: %d", gameMap.getMaxLayer()));
                    }
                    int tileId = Integer.parseInt(splitLine[1]);
                    int xPosition = Integer.parseInt(splitLine[2]);
                    int yPosition = Integer.parseInt(splitLine[3]);
                    boolean isRegular = true;
                    if (splitLine.length >= 5) {
                        String terrain = splitLine[4];
                        if (terrain.length() == 1) {
                            isRegular = "y".equals(terrain);
                        }
                    }
                    MapTile tile = new MapTile(layer, tileId, xPosition, yPosition, isRegular);
                    checkIfPortal(gameMap, splitLine, tile);
                    tiles.add(tile);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return gameMap;
    }

    private boolean handleConfigLines(GameMap gameMap, String line) {
        if (line.startsWith("Fill:")) {
            String[] splitLine = line.split(":");
            gameMap.setBackGroundTileId(Integer.parseInt(splitLine[1]));
            return true;
        }
        if (line.startsWith("Size:")) {
            defineMapSize(gameMap, line);
            return true;
        }
        //just a comment
        return line.startsWith("//");
    }

    public void defineMapSize(GameMap gameMap, String line) {
        String[] splitLine = line.split(":");
        gameMap.setMapWidth(Integer.parseInt(splitLine[1]));
        if (gameMap.getMapWidth() < 20) {
            gameMap.setMapWidth(20);
        }
        gameMap.setMapHeight(Integer.parseInt(splitLine[2]));
        if (gameMap.getMapHeight() < 20) {
            gameMap.setMapHeight(20);
        }
        logger.info(String.format("Size of the map is %d by %d tiles", gameMap.getMapWidth(), gameMap.getMapHeight()));
    }

    private boolean loadObjects(GameMap gameMap, String line) {
        if (line.startsWith("plant")) {
            String[] splitLine = line.split(",");
            String plantId = splitLine[0];
            String plantType = plantId.split("-")[1];
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            int growingStage = Integer.parseInt(splitLine[3]);
            int growingTicks = 0;
            if (splitLine.length > 4) {
                growingTicks = Integer.parseInt(splitLine[4]);
            }

            Plant plant = plantService.createPlant(plantType, x, y);
            plant.setGrowingStage(growingStage);
            plant.setGrowingTicks(growingTicks);
            gameMap.addPlant(plant);
            return true;
        }
        if (line.startsWith("item")) {
            String[] splitLine = line.split(",");
            String itemId = splitLine[0];
            String itemName = itemId.split("-")[1];
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            Sprite sprite = plantService.getPreviews().get(itemName);
            Item item = new Item(x, y, itemName, sprite);
            gameMap.addItem(item);
            return true;
        }
        if (line.startsWith("bowl")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            boolean shouldBeFull = Boolean.parseBoolean(splitLine[3]);
            FoodBowl foodBowl = new FoodBowl(x, y);
            if (shouldBeFull) {
                foodBowl.fillBowl();
            }
            gameMap.addObject(foodBowl);
            return true;
        }
        if (line.startsWith("water-bowl")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            boolean shouldBeFull = Boolean.parseBoolean(splitLine[3]);
            WaterBowl waterBowl = new WaterBowl(x, y);
            if (shouldBeFull) {
                waterBowl.fillBowl();
            }
            gameMap.addObject(waterBowl);
            return true;
        }

        return false;
    }

    private void checkIfPortal(GameMap gameMap, String[] splitLine, MapTile tile) {
        if (splitLine.length == 5) {
            String lastPiece = splitLine[4];
            if (lastPiece.length() > 1) {
                logger.debug("Found portal as in old map config");
                tile.setPortal(true);
                tile.setPortalDirection(splitLine[4]);
                gameMap.addPortal(tile);
            }
        } else if (splitLine.length >= 6) {
            logger.debug("Found portal");
            tile.setPortal(true);
            tile.setPortalDirection(splitLine[5]);
            gameMap.addPortal(tile);
        }
    }

    public void saveMap(GameMap gameMap) {
        logger.info("Saving map");
        File mapFile = new File(getMapConfig(gameMap.getMapName()));
        try {
            if (mapFile.exists()) {
                Files.deleteIfExists(mapFile.toPath());
            }
            if (!mapFile.createNewFile()) {
                logger.error(String.format("Unable to create file: %s", mapFile));
                throw new IllegalArgumentException();
            }

            PrintWriter printWriter = new PrintWriter(mapFile);

            printWriter.println("Game version: " + CURRENT_GAME_VERSION);

            printWriter.println("Size:" + gameMap.getMapWidth() + ":" + gameMap.getMapHeight());
            if (gameMap.getBackGroundTileId() >= 0) {
                printWriter.println("Fill:" + gameMap.getBackGroundTileId());
            }
            savePlants(gameMap, printWriter);
            saveItems(gameMap, printWriter);
            printWriter.println("//layer,tileId,xPos,yPos,regularTile,portalDirection");
            for (List<MapTile> layer : gameMap.getLayeredTiles().values()) {
                for (MapTile tile : layer) {
                    if (tile.isRegularTile() && (tile.getId() == BOWL_TILE_ID || tile.getId() == WATER_BOWL_TILE_ID)) {
                        continue;
                    }
                    String isRegular = tile.isRegularTile() ? "y" : "n";
                    if (tile.getPortalDirection() != null) {
                        printWriter.println(tile.getLayer() + "," + tile.getId() + "," + tile.getX() + "," + tile.getY() + "," + isRegular + "," + tile.getPortalDirection());
                    } else {
                        printWriter.println(tile.getLayer() + "," + tile.getId() + "," + tile.getX() + "," + tile.getY() + "," + isRegular);
                    }
                }
            }
            printWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePlants(GameMap gameMap, PrintWriter printWriter) {
        if (gameMap.getPlants().isEmpty()) {
            return;
        }
        printWriter.println("//Plants");
        printWriter.println("//type, xPosition, yPosition, growingStage, growingTicks");
        for (Plant plant : gameMap.getPlants()) {
            String plantType = plant.getPlantType();
            int plantX = plant.getRectangle().getX();
            int plantY = plant.getRectangle().getY();
            int growingStage = plant.getGrowingStage();
            int growingTicks = plant.getGrowingTicks();
            if (Corn.NAME.equals(plant.getPlantType())) {
                plantY += TILE_SIZE * ZOOM;
            }
            printWriter.println("plant-" + plantType + "," + plantX + "," + plantY + "," + growingStage + "," + growingTicks);
        }
    }

    private void saveItems(GameMap gameMap, PrintWriter printWriter) {
        if (gameMap.getItems().isEmpty() && gameMap.getInteractiveObjects().isEmpty()) {
            return;
        }
        printWriter.println("//Items");
        printWriter.println("//type, xPosition, yPosition");
        for (Item item : gameMap.getItems()) {
            printWriter.println("item-" + item.getItemName() + "," + item.getRectangle().getX() + "," + item.getRectangle().getY());
        }
        for (FoodBowl foodBowl : gameMap.getFoodBowls()) {
            printWriter.println("bowl," + foodBowl.getRectangle().getX() + "," + foodBowl.getRectangle().getY() + "," + foodBowl.isFull());
        }
        for (WaterBowl waterBowl : gameMap.getWaterBowls()) {
            printWriter.println("water-bowl," + waterBowl.getRectangle().getX() + "," + waterBowl.getRectangle().getY() + "," + waterBowl.isFull());
        }
    }

    public List<Plant> getOnlyPlantsFromMap(String mapName) {
        List<Plant> plants = new CopyOnWriteArrayList<>();
        try (Scanner scanner = new Scanner(new File(getMapConfig(mapName)))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("plant")) {
                    String[] splitLine = line.split(",");
                    String plantId = splitLine[0];
                    String plantType = plantId.split("-")[1];
                    int x = Integer.parseInt(splitLine[1]);
                    int y = Integer.parseInt(splitLine[2]);
                    int growingStage = Integer.parseInt(splitLine[3]);

                    Plant plant = plantService.createPlant(plantType, x, y);
                    plant.setGrowingStage(growingStage);
                    plants.add(plant);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return plants;
    }

}
