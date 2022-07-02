package base.map;

import base.gameobjects.*;
import base.gameobjects.plants.Corn;
import base.gameobjects.services.PlantService;
import base.graphicsservice.Rectangle;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import static base.constants.Constants.*;

public class MapMigrator {
    protected static final Logger logger = LoggerFactory.getLogger(base.map.MapMigrator.class);

    private String firstMapName;
    private String secondMapName;

    private int fillTileId = -1;

    private int firstMapWidth;
    private int firstMapHeight;

    private int secondMapWidth;
    private int secondMapHeight;

    private List<Plant> plants = new ArrayList<>();
    private List<Item> items = new ArrayList<>();
    private List<FoodBowl> foodBowls = new ArrayList<>();
    private List<WaterBowl> waterBowls = new ArrayList<>();
    private List<NpcSpot> npcSpots = new ArrayList<>();
    private List<Portal> portals = new ArrayList<>();

    private Map<Integer, List<MapTile>> layeredTiles = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        MapMigrator mapMigrator = new MapMigrator();

//        mapMigrator.migrate("TopCenterMap", "maps/TopCenterMap.txt", "TopRightMap", "maps/TopRightMap.txt", "maps/migratedMap.txt");

//        mapMigrator.backupFile("maps/TopRightMap.txt");
    }

    public void migrate(String firstName, String firstFilePath, String secondName, String secondFilePath, String resultPath) {
        firstMapName = firstName;
        secondMapName = secondName;

        loadFirstMap(firstFilePath);
        loadSecondMap(secondFilePath);

        createMapFile(resultPath);
    }

    /**
     * =================================== Load Map 1 & 2 ======================================
     */

    private void loadFirstMap(String mapPath) {
        logger.info("Loading first map");
        try (Scanner scanner = new Scanner(new File(mapPath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith("Fill:")) {
                    String[] splitLine = line.split(":");
                    fillTileId = Integer.parseInt(splitLine[1]);
                    continue;
                }
                if (line.startsWith("Size:")) {
                    String[] splitLine = line.split(":");
                    firstMapWidth = Integer.parseInt(splitLine[1]);
                    firstMapHeight = Integer.parseInt(splitLine[2]);
                    logger.info(String.format("First map size is : %d x %d", firstMapWidth, firstMapHeight));
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }

                if (loadObjects(line, false)) {
                    continue;
                }

                String[] splitLine = line.split(",");
                if (splitLine.length >= 4) {
                    int layer = Integer.parseInt(splitLine[0]);
                    List<MapTile> tiles;
                    if (layeredTiles.containsKey(layer)) {
                        tiles = layeredTiles.get(layer);
                    } else {
                        tiles = new ArrayList<>();
                        layeredTiles.put(layer, tiles);
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
                        migratePortal(tile);
                    } else {
                        tiles.add(tile);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadSecondMap(String mapPath) {
        try (Scanner scanner = new Scanner(new File(mapPath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith("Fill:") || line.startsWith("//")) {
                    continue;
                }
                if (line.startsWith("Size:")) {
                    String[] splitLine = line.split(":");
                    secondMapWidth = Integer.parseInt(splitLine[1]);
                    secondMapHeight = Integer.parseInt(splitLine[2]);
                    logger.info(String.format("Second map size is : %d x %d", secondMapWidth, secondMapHeight));
                    continue;
                }
                if (loadObjects(line, true)) {
                    continue;
                }

                String[] splitLine = line.split(",");
                if (splitLine.length >= 4) {
                    int layer = Integer.parseInt(splitLine[0]);
                    List<MapTile> tiles;
                    if (layeredTiles.containsKey(layer)) {
                        tiles = layeredTiles.get(layer);
                    } else {
                        tiles = new ArrayList<>();
                        layeredTiles.put(layer, tiles);
                    }
                    int tileId = Integer.parseInt(splitLine[1]);
                    int xPosition = Integer.parseInt(splitLine[2]) + firstMapWidth + 1;
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
                        migratePortal(tile);
                    } else {
                        tiles.add(tile);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean loadObjects(String line, boolean second) {
        PlantService plantService = new PlantService();
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

            if (second) {
                x = x + (firstMapWidth * TILE_SIZE * ZOOM);
            }
            Plant plant = plantService.createPlant(plantType, x, y);
            plant.setGrowingStage(growingStage);
            plant.setGrowingTicks(growingTicks);
            plants.add(plant);
            return true;
        }
        if (line.startsWith("item")) {
            String[] splitLine = line.split(",");
            String itemId = splitLine[0];
            String itemName = itemId.split("-")[1];
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            Sprite sprite = plantService.getPreviews().get(itemName);
            if (second) {
                x = x + (firstMapWidth * TILE_SIZE * ZOOM);
            }
            Item item = new Item(x, y, itemName, sprite);
            items.add(item);
            return true;
        }
        if (line.startsWith("bowl")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            boolean shouldBeFull = Boolean.parseBoolean(splitLine[3]);
            if (second) {
                x = x + (firstMapWidth * TILE_SIZE * ZOOM);
            }
            FoodBowl foodBowl = new FoodBowl(x, y);
            if (shouldBeFull) {
                foodBowl.fillBowl();
            }
            foodBowls.add(foodBowl);
            return true;
        }
        if (line.startsWith("water-bowl")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            boolean shouldBeFull = Boolean.parseBoolean(splitLine[3]);
            if (second) {
                x = x + (firstMapWidth * TILE_SIZE * ZOOM);
            }
            WaterBowl waterBowl = new WaterBowl(x, y);
            if (shouldBeFull) {
                waterBowl.fillBowl();
            }
            waterBowls.add(waterBowl);
            return true;
        }
        if (line.startsWith("npc-spot")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            if (second) {
                x = x + (firstMapWidth * TILE_SIZE * ZOOM);
            }
            npcSpots.add(new NpcSpot(new Rectangle(x, y, TILE_SIZE, TILE_SIZE)));
            return true;
        }
        if (line.startsWith("portal")) {
            String[] splitLine = line.split(",");
            int x = Integer.parseInt(splitLine[1]);
            int y = Integer.parseInt(splitLine[2]);
            String direction = splitLine[3];
            if (second) {
                x = x + (firstMapWidth * TILE_SIZE * ZOOM);
            }
            if (direction.equalsIgnoreCase(firstMapName) || direction.equalsIgnoreCase(secondMapName) || direction.equalsIgnoreCase("Home")) {
                return true;
            }
            portals.add(new Portal(new Rectangle(x, y, TILE_SIZE, TILE_SIZE), direction));
            return true;
        }

        return false;
    }

    private boolean isPortal(String[] splitLine, MapTile tile) {
        if (splitLine.length == 5) {
            String lastPiece = splitLine[4];
            if (lastPiece.length() > 1) {
                logger.debug("Found portal as in old map config");
                tile.setPortal(true);
                tile.setPortalDirection(splitLine[4]);
                return true;
            }
        } else if (splitLine.length >= 6) {
            logger.debug("Found portal");
            tile.setPortal(true);
            tile.setPortalDirection(splitLine[5]);
            return true;
        }
        return false;
    }

    private void migratePortal(MapTile tile) {
        logger.info("Migrating old portal");
        String tilePortalDirection = tile.getPortalDirection();
        if (tilePortalDirection.equalsIgnoreCase(firstMapName) || tilePortalDirection.equalsIgnoreCase(secondMapName) || "Home".equalsIgnoreCase(tilePortalDirection)) {
            logger.info(String.format("Skipping portal to %s", tilePortalDirection));
            return;
        }
        int portalX = tile.getX() * (TILE_SIZE * ZOOM);
        int portalY = tile.getY() * (TILE_SIZE * ZOOM);
        Portal portal = new Portal(new Rectangle(portalX, portalY, 32, 32), tilePortalDirection);
        portals.add(portal);
    }

    /**
     * =================================== Save Map ======================================
     */

    private void createMapFile(String newMapPath) {
        logger.info("Saving map");
        File mapFile = new File(newMapPath);
        try {
            if (mapFile.exists()) {
                Files.deleteIfExists(mapFile.toPath());
            }
            if (!mapFile.createNewFile()) {
                logger.error(String.format("Unable to create file: %s", mapFile));
                throw new IllegalArgumentException();
            }

            PrintWriter printWriter = new PrintWriter(mapFile);
            printWriter.println("Game version:" + CURRENT_GAME_VERSION);

            int finalWidth = firstMapWidth + secondMapWidth + 1;
            int finalHeight = Math.max(firstMapHeight, secondMapHeight);
            logger.info(String.format("Final map size is : %d x %d", finalWidth, finalHeight));
            printWriter.println("Size:" + finalWidth + ":" + finalHeight);
            if (fillTileId >= 0) {
                printWriter.println("Fill:" + fillTileId);
            }
            savePlants(printWriter);
            saveItems(printWriter);
            printWriter.println("//layer,tileId,xPos,yPos,regularTile,portalDirection");
            logger.info("Saving tiles");
            for (List<MapTile> layer : layeredTiles.values()) {
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

    private void savePlants(PrintWriter printWriter) {
        logger.info("Saving plants");
        if (plants.isEmpty()) {
            return;
        }
        printWriter.println("//Plants");
        printWriter.println("//type, xPosition, yPosition, growingStage, growingTicks");
        for (Plant plant : plants) {
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

    private void saveItems(PrintWriter printWriter) {
        logger.info("Saving items");
        printWriter.println("//Items");
        printWriter.println("//type, xPosition, yPosition");
        for (Item item : items) {
            printWriter.println("item-" + item.getItemName() + "," + item.getRectangle().getX() + "," + item.getRectangle().getY());
        }
        for (FoodBowl foodBowl : foodBowls) {
            printWriter.println("bowl," + foodBowl.getRectangle().getX() + "," + foodBowl.getRectangle().getY() + "," + foodBowl.isFull());
        }
        for (WaterBowl waterBowl : waterBowls) {
            printWriter.println("water-bowl," + waterBowl.getRectangle().getX() + "," + waterBowl.getRectangle().getY() + "," + waterBowl.isFull());
        }
        for (NpcSpot npcSpot : npcSpots) {
            printWriter.println("npc-spot," + npcSpot.getRectangle().getX() + "," + npcSpot.getRectangle().getY());
        }
        for (Portal portal : portals) {
            printWriter.println("portal," + portal.getRectangle().getX() + "," + portal.getRectangle().getY() + "," + portal.getDirection());
        }
    }

    /**
     * =================================== Other ======================================
     */

    public void backupFile(String oldMapPath) {
        logger.info(String.format("Creating backup for file %s", oldMapPath));
        String newMapPath = getNewMapPath(oldMapPath);
        if (newMapPath == null) return;

        File newFile = new File(newMapPath);
        try {
            PrintWriter printWriter = new PrintWriter(newFile);
            File source = new File(oldMapPath);
            Scanner scanner = new Scanner(source);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                printWriter.println(line);
            }
            scanner.close();
            printWriter.close();
        } catch (FileNotFoundException e) {
            logger.error(String.format("Error with writing file %s", newMapPath));
            e.printStackTrace();
        }
    }

    private String getNewMapPath(String mapPath) {
        String[] splitMapPath = mapPath.split("\\.");
        if (splitMapPath.length != 2) {
            logger.info("Something went wrong with file backup");
            return null;
        }
        return splitMapPath[0] + "-backup." + splitMapPath[1];
    }

    public void deleteFile(String filePath) {
        File fileToDelete = new File(filePath);
        if (fileToDelete.exists()) {
            boolean deleted = fileToDelete.delete();
            logger.info(String.valueOf(deleted));
        }
    }

    public void renameFile(String filePath, String newPath) {
        File fileToRename = new File(filePath);
        boolean renamed = fileToRename.renameTo(new File(newPath));
        logger.info(String.valueOf(renamed));
    }
}
