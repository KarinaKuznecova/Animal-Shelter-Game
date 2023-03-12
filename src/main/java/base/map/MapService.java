package base.map;

import base.gameobjects.*;
import base.gameobjects.plants.Corn;
import base.gameobjects.services.ItemService;
import base.gameobjects.services.PlantService;
import base.gameobjects.storage.StorageCell;
import base.gameobjects.storage.StorageChest;
import base.graphicsservice.Rectangle;
import base.navigationservice.Direction;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static base.constants.Constants.*;
import static base.constants.FilePath.*;
import static base.constants.MapConstants.MAIN_MAP;
import static base.constants.MapConstants.TOP_CENTER_MAP;

public class MapService {

    private final File mapListFile = new File(MAPS_LIST_PATH);
    private final Map<String, String> mapFiles = new HashMap<>();
    private final PlantService plantService = new PlantService();
    private final ItemService itemService = new ItemService();
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

    // TODO: migration, if there is no json, but only normal file, them immediately save?
    public GameMap loadGameMapFromJson(String mapName, TileService tileService) {
        File directory = new File(JSON_MAPS_DIRECTORY);
        if (directory.listFiles() == null || directory.listFiles().length == 0) {
            logger.info(String.format("No json map for %s, will load old way", mapName));
            return loadGameMap(mapName, tileService);
        }
        try {
            Gson gson = new Gson();
            Reader reader = new FileReader(JSON_MAPS_DIRECTORY + mapName);
            GameMap gameMap = gson.fromJson(reader, GameMap.class);
            reader.close();
            return gameMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadGameMap(mapName, tileService);
    }

    public GameMap loadGameMap(String mapName, TileService tileService) {
        GameMap gameMap = new GameMap(mapName);
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
            if (splitLine.length > 6) {
                boolean isWild = Boolean.parseBoolean(splitLine[5]);
                boolean isRefreshable = Boolean.parseBoolean(splitLine[6]);
                plant.setWild(isWild);
                plant.setRefreshable(isRefreshable);
            }
            gameMap.addPlant(plant);
            return true;
        }
        if (line.startsWith("item")) {
            String[] splitLine = line.split(",");
            String itemId = splitLine[0];
            String itemName = itemId.split("-")[1];
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            Item item = itemService.createNewItem(itemName, x, y);
            gameMap.addItem(item);
            return true;
        }
        if (line.startsWith("bowl")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            boolean shouldBeFull = Boolean.parseBoolean(splitLine[3]);
            FoodBowl foodBowl = new FoodBowl(x, y, shouldBeFull);
            gameMap.addFoodBowl(foodBowl);
            return true;
        }
        if (line.startsWith("water-bowl")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            boolean shouldBeFull = Boolean.parseBoolean(splitLine[3]);
            WaterBowl waterBowl = new WaterBowl(x, y, shouldBeFull);
            gameMap.addWaterBowl(waterBowl);
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
            gameMap.addStorageChest(new StorageChest(x, y, filename));
            return true;
        }
        if (line.startsWith("wood")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            gameMap.addObject(new Wood(x, y));
            return true;
        }
        if (line.startsWith("feather")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            gameMap.addObject(new Feather(x, y));
            return true;
        }
        if (line.startsWith("mushroom")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            gameMap.addObject(new Mushroom(x, y));
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

    public void loadStorageChest(String fileName, StorageChest chest) {
        File mapFile = new File(STORAGES_DIRECTORY + fileName);
        try (Scanner scanner = new Scanner(mapFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitLine = line.split(":");
                String itemName = splitLine[0];
                int qty = Integer.parseInt(splitLine[1]);
                if (!itemName.startsWith(fileName) && qty > 0) {
                    chest.getStorage().addItem(itemName, qty);
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("Storage chest file not found");
            e.printStackTrace();
        }
    }

    /**
     * =================================== Save Map ======================================
     */

    public void saveMapToJson(GameMap gameMap) {
        Gson gson = new Gson();
        try {
            File directory = new File(JSON_MAPS_DIRECTORY);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    logger.error("Error while saving map to json file - cannot create directory");
                    return;
                }
            }
            FileWriter writer = new FileWriter(JSON_MAPS_DIRECTORY + gameMap.getMapName());
            gson.toJson(gameMap, writer);
            writer.flush();
            writer.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Deprecated // use saveMapToJson
    public void saveMap(GameMap gameMap) {
        logger.info(String.format("Saving map %s", gameMap.getMapName()));
        saveMapToJson(gameMap);
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
        printWriter.println("//type, xPosition, yPosition, growingStage, growingTicks, isWild, isRefreshable");
        for (Plant plant : gameMap.getPlants()) {
            String plantType = plant.getPlantType();
            int plantX = plant.getRectangle().getX();
            int plantY = plant.getRectangle().getY();
            int growingStage = plant.getGrowingStage();
            int growingTicks = plant.getGrowingTicks();
            boolean isWild = plant.isWild();
            boolean isRefreshable = plant.isRefreshable();
            if (Corn.NAME.equals(plant.getPlantType())) {
                plantY += CELL_SIZE;
            }
            printWriter.println("plant-" + plantType + "," + plantX + "," + plantY + "," + growingStage + "," + growingTicks + "," + isWild + "," + isRefreshable);
        }
    }

    private void saveItems(GameMap gameMap, PrintWriter printWriter) {
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
        for (Portal portal : gameMap.getPortals()) {
            printWriter.println("portal," + portal.getRectangle().getX() + "," + portal.getRectangle().getY() + "," + portal.getDirection());
        }
        for (StorageChest storageChest : gameMap.getStorageChests()) {
            printWriter.println(("storagechest," + storageChest.getRectangle().getX() + "," + storageChest.getRectangle().getY() + "," + storageChest.getFileName()));
            saveStorageChest(storageChest);
        }
        for (Feather feather : gameMap.getFeathers()) {
            printWriter.println("feather," + feather.getRectangle().getX() + "," + feather.getRectangle().getY());
        }
        for (Mushroom mushroom : gameMap.getMushrooms()) {
            printWriter.println("mushroom," + mushroom.getRectangle().getX() + "," + mushroom.getRectangle().getY());
        }
        for (Wood wood : gameMap.getWoods()) {
            printWriter.println("wood," + wood.getRectangle().getX() + "," + wood.getRectangle().getY());
        }
        for (Bush bush : gameMap.getBushes()) {
            printWriter.println("bush," + bush.getX() + "," + bush.getY());
        }
        for (Oak oak : gameMap.getOaks()) {
            printWriter.println("oak," + oak.getOriginalRectangle().getX() + "," + oak.getOriginalRectangle().getY());
        }
        for (Spruce spruce : gameMap.getSpruces()) {
            printWriter.println("spruce," + spruce.getOriginalRectangle().getX() + "," + spruce.getOriginalRectangle().getY());
        }
        for (NpcSpot npcSpot : gameMap.getNpcSpots()) {
            printWriter.println("npc-spot," + npcSpot.getRectangle().getX() + "," + npcSpot.getRectangle().getY());
        }
    }

    private void saveStorageChest(StorageChest chest) {
        logger.info("Saving storage chest");
        File file = new File(STORAGES_DIRECTORY + chest.getFileName());
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
        Portal portal = new Portal(new Rectangle(portalX, portalY, CELL_SIZE, CELL_SIZE), direction);
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
        if (version.startsWith("1.3")) {
            return false;
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
        return Arrays.asList(13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 158, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173);
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
