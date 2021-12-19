package base.map;

import base.gameobjects.*;
import base.graphicsservice.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.Constants.ZOOM;

public class GameMap {

    private final TileService tileService;
    private final Map<Integer, List<MapTile>> layeredTiles = new ConcurrentHashMap<>();
    private final List<MapTile> portals = new ArrayList<>();
    private List<Plant> plants = new CopyOnWriteArrayList<>();
    private final List<Item> items = new CopyOnWriteArrayList<>();
    private final List<GameObject> interactiveObjects = new CopyOnWriteArrayList<>();

    int backGroundTileId = -1;
    private int mapWidth = -1;
    private int mapHeight = -1;
    int maxLayer = -1;

    private final String mapName;

    private static final Logger logger = LoggerFactory.getLogger(GameMap.class);

    public GameMap(String mapName, TileService tileService) {
        this.mapName = mapName;
        this.tileService = tileService;
    }

    public int getBackGroundTileId() {
        return backGroundTileId;
    }

    public void setBackGroundTileId(int backGroundTileId) {
        this.backGroundTileId = backGroundTileId;
    }

    public Map<Integer, List<MapTile>> getLayeredTiles() {
        return layeredTiles;
    }

    public int getMaxLayer() {
        return maxLayer;
    }

    public void setMaxLayer(int maxLayer) {
        this.maxLayer = maxLayer;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public TileService getTileService() {
        return tileService;
    }

    public void addPortal(MapTile tile) {
        portals.add(tile);
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

    public void addPlant(Plant plant) {
        plants.add(plant);
    }

    public void removePlant(Plant plant) {
        plants.remove(plant);
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public boolean isThereGrassOrDirt(int x, int y) {
        x = x / (TILE_SIZE * ZOOM);
        y = y / (TILE_SIZE * ZOOM);
        for (MapTile tile : layeredTiles.get(0)) {
            if (tile.getX() == x && tile.getY() == y) {
                return getGrassTileIds().contains(tile.getId());
            }
        }
        if (getGrassTileIds().contains(backGroundTileId) && isPlaceEmpty(1, x, y) && isPlaceEmpty(2, x, y)) {
            return true;
        }
        logger.info("There is no grass");
        return false;
    }

    private List<Integer> getGrassTileIds() {
        return Arrays.asList(13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24);
    }

    public boolean isPlaceEmpty(int layer, int x, int y) {
        if (isTherePlant(x, y)) {
            return false;
        }
        if (layeredTiles.get(layer) == null) {
            return true;
        }
        for (MapTile tile : layeredTiles.get(layer)) {
            if (tile.getX() == x && tile.getY() == y) {
                logger.info("Place is taken");
                return false;
            }
        }
        return true;
    }

    public boolean isInsideOfMap(int x, int y) {
        if (x < 0 || x > mapWidth || y < 0 || y > mapHeight) {
            logger.info("Outside of modifiable map");
            return false;
        }
        return true;
    }

    public boolean isTherePlant(int x, int y) {
        for (Plant plant : plants) {
            if (plant.getRectangle().getX() == x && plant.getRectangle().getY() == y) {
                logger.info("There is already a plant");
                return true;
            }
        }
        return false;
    }

    public boolean isTherePortal(Rectangle rectangle) {
        for (MapTile portal : getPortals()) {
            if (rectangle.intersects(portal)) {
                return true;
            }
        }
        return false;
    }

    public void addItem(Item item) {
        logger.debug("Adding item to the list");
        items.add(item);
    }

    public void removeItem(String itemName, Rectangle rectangle) {
        items.removeIf(item -> itemName.equals(item.getItemName()) && rectangle.intersects(item.getRectangle()));
    }

    public List<Item> getItems() {
        return items;
    }

    public void addObject(GameObject object) {
        interactiveObjects.add(object);
    }

    public void removeObject(GameObject object) {
        interactiveObjects.remove(object);
    }

    public List<GameObject> getInteractiveObjects() {
        return interactiveObjects;
    }

    public List<FoodBowl> getFoodBowls() {
        List<FoodBowl> bowls = new ArrayList<>();
        for (GameObject object : getInteractiveObjects()) {
            if (object instanceof FoodBowl) {
                bowls.add((FoodBowl) object);
            }
        }
        return bowls;
    }
}
