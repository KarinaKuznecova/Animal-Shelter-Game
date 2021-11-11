package base.map;

import base.gameobjects.Animal;
import base.gameobjects.AnimalService;
import base.gameobjects.GameObject;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;

import static base.Game.TILE_SIZE;
import static base.Game.ZOOM;

public class GameMap {

    private final TileService tileService;
    private final AnimalService animalService = new AnimalService();

    private final File mapFile;
    private final Map<Integer, List<MapTile>> layeredTiles = new HashMap<>();
    private final List<MapTile> portals = new ArrayList<>();
    private final List<Animal> allAnimals;

    int backGroundTileId = -1;      //background of walkable part of the map
    int alphaBackground = -1;       //outside the walkable part
    private int mapWidth = -1;
    private int mapHeight = -1;
    int maxLayer = -1;

    private String mapName;

    protected static final Logger logger = LoggerFactory.getLogger(GameMap.class);

    public GameMap(File mapFile, TileService tileService) {
        this.tileService = tileService;
        this.mapFile = mapFile;
        loadMapFromFile();
        allAnimals = animalService.loadAnimalsFromFile(getMapName());
        logger.info(String.format("There will be %d animals on map %s", allAnimals.size(), getMapName()));
    }

    private void loadMapFromFile() {
        try (Scanner scanner = new Scanner(mapFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (handleConfigLines(line)) {
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
                    if (maxLayer < layer) {
                        maxLayer = layer;
                        logger.info(String.format("max layer: %d", maxLayer));
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
                    checkIfPortal(splitLine, tile);
                    tiles.add(tile);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean handleConfigLines(String line) {
        if (line.startsWith("Fill:")) {
            String[] splitLine = line.split(":");
            backGroundTileId = Integer.parseInt(splitLine[1]);
            return true;
        }
        if (line.startsWith("AlphaFill:")) {
            String[] splitLine = line.split(":");
            alphaBackground = Integer.parseInt(splitLine[1]);
            return true;
        }
        if (line.startsWith("Size:")) {
            defineMapSize(line);
            return true;
        }
        if (line.startsWith("//")) {        //just a comment
            return true;
        }
        if (line.startsWith("Name:")) {
            String[] splitLine = line.split(":");
            mapName = String.valueOf(splitLine[1]);
            return true;
        }
        if (line.startsWith("Animals:")) {
            // ignore, now animals are saved differently
            return true;
        }
        return false;
    }

    private void defineMapSize(String line) {
        String[] splitLine = line.split(":");
        mapWidth = Integer.parseInt(splitLine[1]);
        if (mapWidth < 20) {
            mapWidth = 20;
        }
        mapHeight = Integer.parseInt(splitLine[2]);
        if (mapHeight < 20) {
            mapHeight = 20;
        }
        logger.info(String.format("Size of the map is %d by %d tiles", mapWidth, mapHeight));
    }

    private void checkIfPortal(String[] splitLine, MapTile tile) {
        if (splitLine.length == 5) {
            String lastPiece = splitLine[4];
            if (lastPiece.length() > 1) {
                logger.info("Found portal as in old map config");
                tile.setPortal(true);
                tile.setPortalDirection(splitLine[4]);
                portals.add(tile);
            }
        }
        else if (splitLine.length >= 6) {
            logger.info("Found portal");
            tile.setPortal(true);
            tile.setPortalDirection(splitLine[5]);
            portals.add(tile);
        }
    }

    public void renderMap(RenderHandler renderer, List<GameObject> gameObjects) {
        int tileWidth = TILE_SIZE * ZOOM;
        int tileHeight = TILE_SIZE * ZOOM;

        adjustMaxLayer(gameObjects);

        renderFixedSizeMap(renderer, gameObjects, tileWidth, tileHeight);
    }

    private void adjustMaxLayer(List<GameObject> gameObjects) {
        for (GameObject gameObject : gameObjects) {
            if (maxLayer < gameObject.getLayer()) {
                maxLayer = gameObject.getLayer();
                logger.info(String.format("max layer: %d", maxLayer));
            }
        }
        for (Animal animal : allAnimals) {
            if (maxLayer < animal.getLayer()) {
                maxLayer = animal.getLayer();
                logger.info(String.format("max layer: %d", maxLayer));
            }
        }
    }

    private void renderFixedSizeMap(RenderHandler renderer, List<GameObject> gameObjects, int tileWidth, int tileHeight) {

        renderBackground(renderer, tileWidth, tileHeight);

        for (int i = 0; i <= maxLayer; i++) {
            List<MapTile> tiles = layeredTiles.get(i);
            if (tiles != null) {
                // with for-each loop there is ConcurrentModificationException often, but with this loop everything works fine
                for (int j = 0; j < tiles.size(); j++) {
                    MapTile mappedTile = tiles.get(j);
                    if (mappedTile.getLayer() == i) {
                        renderTile(renderer, tileWidth, tileHeight, mappedTile);
                    }
                }
            }
            renderGameObjects(renderer, gameObjects, i);
        }
    }

    private void renderBackground(RenderHandler renderer, int tileWidth, int tileHeight) {
        if (alphaBackground >= 0) {
            renderInSightOfCamera(renderer, tileWidth, tileHeight, alphaBackground);
        }
        if (backGroundTileId >= 0) {
            for (int i = 0; i < mapHeight * tileHeight; i += tileHeight) {
                for (int j = 0; j < mapWidth * tileWidth; j += tileWidth) {
                    tileService.renderTerrainTile(backGroundTileId, renderer, j, i, ZOOM, ZOOM);
                }
            }
        }
    }

    private void renderInSightOfCamera(RenderHandler renderer, int tileWidth, int tileHeight, int tileId) {
        Rectangle camera = renderer.getCamera();

        for (int i = camera.getY() - tileHeight - (camera.getY() % tileHeight); i < camera.getY() + camera.getHeight(); i += tileHeight) {
            for (int j = camera.getX() - tileWidth - (camera.getX() % tileWidth); j < camera.getX() + camera.getWidth(); j += tileWidth) {
                tileService.renderTerrainTile(tileId, renderer, j, i, ZOOM, ZOOM);
            }
        }
    }

    private void renderGameObjects(RenderHandler renderer, List<GameObject> gameObjects, int layer) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getLayer() == layer) {
                gameObject.render(renderer, ZOOM, ZOOM);
            }
        }
        for (Animal animal : allAnimals) {
            if (!animal.getHomeMap().equals(mapName)) {
                logger.error(String.format("Animal %s doesn't belong to map %s", animal, getMapName()));
                continue;
            }
            if (animal.getLayer() == layer) {
                animal.render(renderer, ZOOM, ZOOM);
            }
        }
        animalService.fixStuckAnimals(this, getAnimals());
    }

    private void renderTile(RenderHandler renderer, int tileWidth, int tileHeight, MapTile mappedTile) {
        int xPosition = mappedTile.getX() * tileWidth;
        int yPosition = mappedTile.getY() * tileHeight;
        if (xPosition <= mapWidth * tileWidth && yPosition <= mapHeight * tileHeight) {
            if (mappedTile.isRegularTile()) {
                tileService.renderTile(mappedTile.getId(), renderer, xPosition, yPosition, ZOOM, ZOOM);
            } else {
                tileService.renderTerrainTile(mappedTile.getId(), renderer, xPosition, yPosition, ZOOM, ZOOM);
            }
        }
    }

    public List<MapTile> getPortals() {
        return portals;
    }


    public List<MapTile> getTilesOnLayer(int layer) {
        return layeredTiles.get(layer);
    }

    public void setTile(int tileX, int tileY, int tileId, boolean regularTiles) {
        if (tileId == -1) {
            return;
        }
        int layer = tileService.getLayerById(tileId, regularTiles);

        if (isThereAPortal(tileX, tileY) && regularTiles) {
            logger.debug("Can't place regular tile on portal");
            return;
        }

        boolean foundTile = false;
        if (layeredTiles.get(layer) != null) {
            for (MapTile tile : layeredTiles.get(layer)) {
                if (tile.getX() == tileX && tile.getY() == tileY) {
                    if (!tile.isRegularTile() && regularTiles) {
                        logger.debug("Attempt to modify terrain tile when regular tile is selected");
                        if (tile.getLayer() == layer) {
                            foundTile = true;
                        }
                        break;
                    }
                    tile.setId(tileId);
                    foundTile = true;
                    break;
                }
            }
        } else {
            List<MapTile> tiles = new ArrayList<>();
            tiles.add(new MapTile(layer, tileId, tileX, tileY, regularTiles));
            layeredTiles.put(layer, tiles);
        }
        if (!foundTile) {
            layeredTiles.get(layer).add(new MapTile(layer, tileId, tileX, tileY, regularTiles));
        }
        if (maxLayer < layer) {
            maxLayer = layer;
        }
    }

    public void removeTile(int tileX, int tileY, int layer, boolean regularTiles) {
        if (layeredTiles.get(layer) != null && !isThereAPortal(tileX, tileY)) {
            layeredTiles.get(layer).removeIf(tile -> tile.getX() == tileX && tile.getY() == tileY && tile.isRegularTile() == regularTiles);
        }
    }

    boolean isThereAPortal(int x, int y) {
        for (MapTile tile : getPortals()) {
            if (tile.getX() == x && tile.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public void saveMap() {
        logger.info("Saving map");
        try {
            if (mapFile.exists()) {
                Files.deleteIfExists(mapFile.toPath());
            }
            if (!mapFile.createNewFile()) {
                logger.error(String.format("Unable to create file: %s", mapFile));
                throw new IllegalArgumentException();
            }

            PrintWriter printWriter = new PrintWriter(mapFile);

            printWriter.println("Name:" + mapName);
            printWriter.println("Size:" + mapWidth + ":" + mapHeight);
            if (backGroundTileId >= 0) {
                printWriter.println("Fill:" + backGroundTileId);
            }
            if (alphaBackground >= 0) {
                printWriter.println("AlphaFill:" + alphaBackground);
            }
            saveAnimals();
            printWriter.println("//layer,tileId,xPos,yPos,regularTile,portalDirection");
            for (List<MapTile> layer : layeredTiles.values()) {
                for (MapTile tile : layer) {
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

    public void saveAnimals() {
        if (!allAnimals.isEmpty()) {
            deleteAnimalFilesForCurrentMap();
            for (Animal animal : allAnimals) {
                animalService.saveAnimalToFile(animal);
            }
        }
    }

    private void deleteAnimalFilesForCurrentMap() {
        logger.info("Deleting all animal files related to current map");
        File animalsDirectory = new File("animals/");
        if (animalsDirectory.listFiles() != null && animalsDirectory.listFiles().length > 0) {
            for (File file : Objects.requireNonNull(animalsDirectory.listFiles())) {
                if (file.getName().startsWith(getMapName())) {
                    file.delete();
                }
            }
        }
    }

    public MapTile getPortalTo(String destination) {
        for (MapTile tile : getPortals()) {
            if (tile.getPortalDirection().equals(destination)) {
                return tile;
            }
        }
        return null;
    }

    public int getSpawnPoint(MapTile portalToPrevious, boolean getX) {
        int previousMapPortal;
        if (getX) {
            previousMapPortal = portalToPrevious.getX() * (TILE_SIZE * ZOOM);
        } else {
            previousMapPortal = portalToPrevious.getY() * (TILE_SIZE * ZOOM);
        }

        int mapSize;
        if (getX) {
            mapSize = mapWidth;
        } else {
            mapSize = mapHeight;
        }

        if (previousMapPortal == mapSize * (TILE_SIZE * ZOOM)) {
            previousMapPortal = previousMapPortal - (TILE_SIZE * ZOOM);
        }

        if (previousMapPortal < 0) {
            previousMapPortal = 0;
        }
        return previousMapPortal;
    }

    public String getMapName() {
        return mapName;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public List<Animal> getAnimals() {
        return allAnimals;
    }

    public Animal addAnimal(int x, int y, int animalId) {
        logger.info("Creating new animal");
        String animalName = animalService.getAnimalNameById(animalId);
        Animal newAnimal = animalService.createAnimal(animalName, x, y, getMapName());

        allAnimals.add(newAnimal);
        return newAnimal;
    }

    public void removeAnimal(int existingAnimalId) {
        allAnimals.remove(existingAnimalId);
    }
}
