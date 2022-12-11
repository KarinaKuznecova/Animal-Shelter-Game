package base.map;

import base.gameobjects.*;
import base.gameobjects.plants.Corn;
import base.gameobjects.services.ItemService;
import base.gameobjects.services.PlantService;
import base.gameobjects.storage.StorageCell;
import base.gameobjects.storage.StorageChest;
import base.graphicsservice.Rectangle;
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
import static base.constants.MapConstants.MAIN_MAP;
import static base.constants.MapConstants.TOP_CENTER_MAP;

public class MapService {

    private final File mapListFile = new File(MAPS_LIST_PATH);
    private final Map<String, String> mapFiles = new HashMap<>();
    private final PlantService plantService = new PlantService();
    private final ItemService itemService = new ItemService(plantService);
    private final MapMigrator mapMigrator = new MapMigrator();

    protected static final Logger logger = LoggerFactory.getLogger(MapService.class);

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

    public String getMapConfig(String mapName) {
        return mapFiles.get(mapName);
    }

    public List<String> getAllMapsNames() {
        return new ArrayList<>(mapFiles.keySet());
    }

    /**
     * =================================== Load Map ======================================
     */

    public GameMap loadGameMap(String mapName, TileService tileService) {
        GameMap gameMap = new GameMap(mapName, tileService);
        boolean migrationChecked = false;
        boolean migrationNeeded = false;
        File mapFile = new File(getMapConfig(mapName));
        try (Scanner scanner = new Scanner(mapFile)) {
            if (!scanner.hasNextLine()) {
                useBackupFile(mapFile);
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (!migrationChecked) {
                    if ((MAIN_MAP.equalsIgnoreCase(mapName) || TOP_CENTER_MAP.equalsIgnoreCase(mapName)) && isMigrationNeeded(line, mapName)) {
                        migrationNeeded = true;
                        break;
                    }
                    migrationChecked = true;
                }

                if (handleConfigLines(gameMap, line)) {
                    continue;
                }

                if (loadObjects(gameMap, line, tileService)) {
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
                    if (isPortal(splitLine, tile)) {
                        migratePortal(gameMap, tile);
                    } else {
                        tiles.add(tile);
                    }
                    if (tileId == CHEST_TILE_ID) {
                        StorageChest storageChest = new StorageChest(xPosition * CELL_SIZE, yPosition * CELL_SIZE, tileService.getTiles().get(36).getSprite(), tileService.getTiles().get(37).getSprite());
                        gameMap.addObject(storageChest);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (migrationNeeded) {
            migrate(gameMap);
            return loadGameMap(mapName, tileService);
        }
        gameMap.sortInteractiveObjects();
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

    private boolean loadObjects(GameMap gameMap, String line, TileService tileService) {
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
            Item item = itemService.creteNewItem(itemName, x, y);
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
        if (line.startsWith("npc-spot")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            gameMap.addObject(new NpcSpot(new Rectangle(x, y, TILE_SIZE, TILE_SIZE)));
            return true;
        }
        if (line.startsWith("portal")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            String direction = splitLine[3];
            gameMap.addObject(new Portal(new Rectangle(x, y, CELL_SIZE, CELL_SIZE), direction));
            return true;
        }
        if (line.startsWith("bush")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            gameMap.addObject(new Bush(x, y, gameMap.getMapName()));
            return true;
        }
        if (line.startsWith("oak")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            gameMap.addObject(new Oak(x, y));
            return true;
        }
        if (line.startsWith("spruce")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            gameMap.addObject(new Spruce(x, y));
            return true;
        }
        if (line.startsWith("storagechest")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            String filename = splitLine[3];
            gameMap.addObject(new StorageChest(x, y, tileService.getTiles().get(36).getSprite(), tileService.getTiles().get(37).getSprite(), filename));
            return true;
        }

        return false;
    }

    private boolean isPortal(String[] splitLine, MapTile tile) {
        if (splitLine.length == 5) {
            String lastPiece = splitLine[4];
            if (lastPiece.length() > 1) {
                logger.info("Found portal as in old map config");
                tile.setPortal(true);
                tile.setPortalDirection(splitLine[4]);
                return true;
            }
        } else if (splitLine.length >= 6) {
            logger.info("Found portal");
            tile.setPortal(true);
            tile.setPortalDirection(splitLine[5]);
            return true;
        }
        return false;
    }

    /**
     * =================================== Save Map ======================================
     */

    public void saveMap(GameMap gameMap) {
        logger.info("Saving map");
        File mapFile = new File(getMapConfig(gameMap.getMapName()));
        try {
            if (mapFile.exists()) {
                File mapFileBackup = new File(mapFile.getName() + "-backup");
                if (mapFileBackup.exists()) {
                    Files.deleteIfExists(mapFileBackup.toPath());
                }
                mapMigrator.renameFile(mapFile.getPath(), mapFile.getPath() + "-backup");
                Files.deleteIfExists(mapFile.toPath());
            }
            if (!mapFile.createNewFile()) {
                logger.error(String.format("Unable to create file: %s", mapFile));
                throw new IllegalArgumentException();
            }

            PrintWriter printWriter = new PrintWriter(mapFile);

            printWriter.println("Game version:" + CURRENT_GAME_VERSION);

            printWriter.println("Size:" + gameMap.getMapWidth() + ":" + gameMap.getMapHeight());
            if (gameMap.getBackGroundTileId() >= 0) {
                printWriter.println("Fill:" + gameMap.getBackGroundTileId());
            }
            savePlants(gameMap, printWriter);
            saveItems(gameMap, printWriter);
            printWriter.println("//layer,tileId,xPos,yPos,regularTile,portalDirection");
            for (List<MapTile> layer : gameMap.getLayeredTiles().values()) {
                for (MapTile tile : layer) {
                    if (tile.isRegularTile() && (tile.getId() == BOWL_TILE_ID || tile.getId() == WATER_BOWL_TILE_ID || tile.getId() == CHEST_TILE_ID)) {
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
            logger.error("Error while saving map, will use backup");
            e.printStackTrace();

            useBackupFile(mapFile);
        }
    }

    private void useBackupFile(File mapFile) {
        try {
            if (mapFile.exists()) {
                File mapFileBackup = new File(mapFile.getName() + "-backup");
                if (mapFileBackup.exists()) {
                    mapMigrator.renameFile(mapFile.getPath() + "-backup", mapFile.getPath());
                }
                Files.deleteIfExists(mapFile.toPath());
            }
        } catch (IOException e) {
            logger.error("something went wrong while trying to use backup");
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
                plantY += CELL_SIZE;
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
            if (item != null) {
                printWriter.println("item-" + item.getItemName() + "," + item.getRectangle().getX() + "," + item.getRectangle().getY());
            }
        }
        for (FoodBowl foodBowl : gameMap.getFoodBowls()) {
            printWriter.println("bowl," + foodBowl.getRectangle().getX() + "," + foodBowl.getRectangle().getY() + "," + foodBowl.isFull());
        }
        for (WaterBowl waterBowl : gameMap.getWaterBowls()) {
            printWriter.println("water-bowl," + waterBowl.getRectangle().getX() + "," + waterBowl.getRectangle().getY() + "," + waterBowl.isFull());
        }
        for (GameObject gameObject : gameMap.getInteractiveObjects()) {
            if (gameObject instanceof NpcSpot) {
                printWriter.println("npc-spot," + gameObject.getRectangle().getX() + "," + gameObject.getRectangle().getY());
            }
            if (gameObject instanceof Portal) {
                printWriter.println("portal," + gameObject.getRectangle().getX() + "," + gameObject.getRectangle().getY() + "," + ((Portal) gameObject).getDirection());
            }
            if (gameObject instanceof Bush) {
                printWriter.println("bush," + ((Bush) gameObject).getX() + "," + ((Bush) gameObject).getY());
            }
            if (gameObject instanceof Oak) {
                printWriter.println("oak," + ((Oak) gameObject).getOriginalRectangle().getX() + "," + ((Oak) gameObject).getOriginalRectangle().getY());
            }
            if (gameObject instanceof Spruce) {
                printWriter.println("spruce," + ((Spruce) gameObject).getOriginalRectangle().getX() + "," + ((Spruce) gameObject).getOriginalRectangle().getY());
            }
            if (gameObject instanceof StorageChest) {
                printWriter.println(("storagechest," + gameObject.getRectangle().getX() + "," + gameObject.getRectangle().getY() + "," + ((StorageChest) gameObject).getFileName()));
                saveStorageChest((StorageChest) gameObject);
            }
        }
    }

    private void saveStorageChest(StorageChest chest) {
        logger.info("Saving storage chest");
        File file = new File("maps/storages/" + chest.getFileName());
        try {
            if (file.exists()) {
                Files.deleteIfExists(file.toPath());
            }
            if (!file.createNewFile()) {
                logger.error(String.format("Unable to create file: %s", file));
                throw new IllegalArgumentException();
            }
            PrintWriter printWriter = new PrintWriter(file);
            for (StorageCell cell : chest.getStorage().getCells()) {

                printWriter.println(cell.getItemName() + ":" + cell.getObjectCount());
            }
            printWriter.close();

        } catch (IOException e) {
            logger.error("Error while saving storage chest");
            e.printStackTrace();
        }
    }

    /**
     * =================================== Other ======================================
     */

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

    /**
     * =================================== Migration ======================================
     */

    private void migratePortal(GameMap gameMap, MapTile tile) {
        logger.info("Migrating old portal");
        int portalX = tile.getX() * CELL_SIZE;
        int portalY = tile.getY() * CELL_SIZE;
        String direction = tile.getPortalDirection();
        if ("TopCenterMap".equals(direction)) {
            direction = "Home";
        }
        Portal portal = new Portal(new Rectangle(portalX, portalY, 64, 64), direction);
        gameMap.addObject(portal);
    }

    private boolean isMigrationNeeded(String line, String mapName) {
        if (MAIN_MAP.equalsIgnoreCase(mapName)) {
            String version = getGameVersionLine(line);
            if (migrationNeeded(version)) {
                logger.info(String.format("old map version was : %s, should be %s", version, CURRENT_GAME_VERSION));
                return true;
            }
        } else if (TOP_CENTER_MAP.equalsIgnoreCase(mapName)) {
            File homeFile = new File("maps/Home.txt");
            return !homeFile.exists();
        }
        logger.info("Migration not needed");
        return false;
    }

    private String getGameVersionLine(String line) {
        if (line.startsWith("Game version")) {
            String[] splitLine = line.split(":");
            return splitLine[1];
        }
        return null;
    }

    private boolean migrationNeeded(String version) {
        if (version != null) {
            version = version.trim();
        }
        return !CURRENT_GAME_VERSION.equalsIgnoreCase(version);
    }

    private void migrate(GameMap gameMap) {
        logger.info("Migrating game map");

        if (MAIN_MAP.equalsIgnoreCase(gameMap.getMapName())) {
            logger.info("Migrating Main Map");
            String mapPath = getMapConfig(gameMap.getMapName());
            logger.info("Migrating Main Map - Renaming old map file");
            mapMigrator.renameFile(mapPath, "maps/GameMap-backup.txt");
            logger.info("Migrating Main Map - Renaming new map file");
            mapMigrator.renameFile("maps/MainMap.txt", mapPath);
            logger.info("Migrating Main Map - DONE");
        }
        if (TOP_CENTER_MAP.equalsIgnoreCase(gameMap.getMapName())) {
            mapMigrator.migrate("TopCenterMap", "maps/TopCenterMap.txt", "TopRightMap", "maps/TopRightMap.txt", "maps/Home.txt");
            // reload
        }
    }

}
