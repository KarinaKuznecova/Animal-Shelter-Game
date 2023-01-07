package base.map;

import base.gameobjects.*;
import base.gameobjects.storage.StorageChest;
import base.graphicsservice.Rectangle;
import base.map.bigobjects.BigObject;
import base.map.bigobjects.Bookcase;
import base.map.bigobjects.Bush;
import base.navigationservice.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static base.constants.Constants.*;
import static base.constants.MultiOptionalObjects.bookcases;

public class GameMap {

    private final TileService tileService;
    private final Map<Integer, List<MapTile>> layeredTiles = new ConcurrentHashMap<>();
    private final List<MapTile> portalsOldWay = new ArrayList<>();
    private List<Plant> plants = new CopyOnWriteArrayList<>();
    private final List<Item> items = new CopyOnWriteArrayList<>();
    private final List<GameObject> interactiveObjects = new CopyOnWriteArrayList<>();
    private final List<Portal> portals = new ArrayList<>();
    private final List<BigObject> bigObjects = new CopyOnWriteArrayList<>();

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

    @Deprecated
    public void addPortalOldWay(MapTile tile) {
        portalsOldWay.add(tile);
    }

    @Deprecated
    public List<MapTile> getPortalsOldWay() {
        return portalsOldWay;
    }

    private void addPortal(Portal portal) {
        portals.add(portal);
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public List<MapTile> getTilesOnLayer(int layer) {
        return layeredTiles.get(layer);
    }

    public void setTile(int tileX, int tileY, int tileId, boolean regularTiles) {
        if (tileId == -1) {
            return;
        }
        if (tileId == 66 && !regularTiles) {
            addBigObject(new Bush(tileX, tileY));
            return;
        }

        int layer = tileService.getLayerById(tileId, regularTiles);

        if (bookcases.contains(tileId) && regularTiles && !isThereAPortal(tileX, tileY)) {

            Bookcase bookcase = new Bookcase(tileX, tileY, bookcases.indexOf(tileId), 1);
            if (placeIsTaken(tileX, tileY, layer, bookcase)) {
                return;
            }
            for (MapTile tile : bookcase.getObjectParts()) {
                if (layeredTiles.get(tile.getLayer()) == null) {
                    layeredTiles.put(tile.getLayer(), new CopyOnWriteArrayList<>());
                }
                layeredTiles.get(tile.getLayer()).add(tile);
            }
            return;
        }

        if (isThereAPortal(tileX, tileY) && regularTiles) {
            logger.debug("Can't place regular tile on portal");
            return;
        }

        MapTile existingTile = getExistingTile(layer, tileX, tileY);
        if (existingTile != null) {
            if (!existingTile.isRegularTile() && regularTiles) {
                logger.debug("Attempt to modify terrain tile when regular tile is selected");
            } else {
                existingTile.setId(tileId);
            }
        } else {
            MapTile tile = new MapTile(layer, tileId, tileX, tileY, regularTiles);
            if (layeredTiles.get(tile.getLayer()) == null) {
                layeredTiles.put(tile.getLayer(), new CopyOnWriteArrayList<>());
            }
            layeredTiles.get(tile.getLayer()).add(tile);
        }
        if (maxLayer < layer) {
            maxLayer = layer;
        }
    }

    private boolean placeIsTaken(int tileX, int tileY, int layer, Bookcase bookcase) {
        for (MapTile tile : bookcase.getObjectParts()) {
            MapTile existingTile = getExistingTile(layer, tileX, tileY);
            if (isThereAPortal(tile.getX(), tile.getY()) || (existingTile != null && !existingTile.isRegularTile())) {
                return true;
            }
        }
        return false;
    }

    private MapTile getExistingTile(int layer, int tileX, int tileY) {
        if (layeredTiles.get(layer) != null) {
            for (MapTile tile : layeredTiles.get(layer)) {
                if (tile.getX() == tileX && tile.getY() == tileY) {
                    return tile;
                }
            }
        }
        return null;
    }

    public boolean removeTile(int tileX, int tileY, int layer, boolean regularTiles, int selectedTile) {
//        if (bookcases.contains(selectedTile) && regularTiles) {
//            for (MapTile tile : new Bookcase(tileX, tileY, bookcases.indexOf(selectedTile), 1).getObjectParts()) {
//                removeTile(tile.getX(), tile.getY(), tile.getLayer(), false, tile.getId());
//            }
//            return;
//        }
        if (layeredTiles.get(layer) != null && !isThereAPortal(tileX, tileY)) {
            return tileRemoved(tileX, tileY, layer, regularTiles, selectedTile);
        }
        return false;
    }

    private boolean tileRemoved(int tileX, int tileY, int layer, boolean regularTiles, int selectedTile) {
        for (MapTile mapTile : layeredTiles.get(layer)) {
            if (mapTile.getX() == tileX && mapTile.getY() == tileY && mapTile.isRegularTile() == regularTiles && mapTile.getId() == selectedTile) {
                layeredTiles.get(layer).remove(mapTile);
                return true;
            }
        }
        return false;
    }

    boolean isThereAPortal(int x, int y) {
        for (Portal portal : getPortals()) {
            int portalX = portal.getRectangle().getX() / CELL_SIZE;
            int portalY = portal.getRectangle().getY() / CELL_SIZE;
            if (portalX == x && portalY == y) {
                return true;
            }
        }
        return false;
    }

    public Portal getPortalTo(String destination) {
        for (Portal portal : getPortals()) {
            if (portal.getDirection().equals(destination)) {
                return portal;
            }
        }
        return null;
    }

    public int getSpawnPoint(Portal portalToPrevious, boolean getX, Direction direction) {
        int previousMapPortal;
        if (getX) {
            previousMapPortal = portalToPrevious.getRectangle().getX();
        } else {
            previousMapPortal = portalToPrevious.getRectangle().getY();
        }

        int mapSize;
        if (getX) {
            mapSize = mapWidth;
        } else {
            mapSize = mapHeight;
        }

        if (mapSize * CELL_SIZE - previousMapPortal < CELL_SIZE) {
            previousMapPortal = previousMapPortal - CELL_SIZE;
        }

        if (previousMapPortal < 0) {
            previousMapPortal = 0;
        }
        if (!getX){
            if (direction == Direction.LEFT) {
                return previousMapPortal - 7;
            }
            if (direction == Direction.RIGHT) {
                return previousMapPortal + 7;
            }
        } else {
            if (direction == Direction.UP) {
                return previousMapPortal - 7;
            }
            if (direction == Direction.DOWN) {
                // TODO ?
                return previousMapPortal + 14;
            }
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

    public List<Plant> getWildPlants() {
        return plants.stream().filter(Plant::isWild).collect(Collectors.toList());
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public boolean isThereGrassOrDirt(int x, int y) {
        x = x / CELL_SIZE;
        y = y / CELL_SIZE;
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
        return Arrays.asList(13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 158, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173);
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

    public boolean isTherePortal(Rectangle rectangle, String destination) {
        for (Portal portal : getPortals()) {
            if (portal.getDirection().equals(destination) && rectangle.intersects(portal.getRectangle())) {
                return true;
            }
        }
        return false;
    }

    public boolean isThereWaterTile(Rectangle rectangle) {
        for (MapTile tile : layeredTiles.get(2)) {
            if (tile.isRegularTile()) {
                continue;
            }
            if (rectangle.intersects(tile)) {
                return getWaterTileIds().contains(tile.getId());
            }
        }
        return false;
    }

    private List<Integer> getWaterTileIds() {
        return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 71, 72);
    }

    public List<Integer> getWaterCornerTiles() {
        return Arrays.asList(0, 2, 6, 8, 71, 72, 1, 3, 5, 7);
    }

    public void addItem(Item item) {
        logger.debug("Adding item to the list");
        items.add(item);
        item.setMapName(mapName);
    }

    //TODO: refactor not to mention every item type separately
    public void removeItem(String itemName, Rectangle rectangle) {
        items.removeIf(item -> itemName.equals(item.getItemName()) && rectangle.intersects(item.getRectangle()));

        if (itemName.equalsIgnoreCase(Wood.ITEM_NAME)) {
            interactiveObjects.removeIf(item -> item instanceof Wood && rectangle.intersects((item.getRectangle())));
        }
        if (itemName.equalsIgnoreCase(Feather.ITEM_NAME)) {
            interactiveObjects.removeIf(item -> item instanceof Feather && rectangle.intersects((item.getRectangle())));
        }
        if (itemName.equalsIgnoreCase(Mushroom.ITEM_NAME)) {
            interactiveObjects.removeIf(item -> item instanceof Mushroom && rectangle.intersects((item.getRectangle())));
        }
    }

    public List<Item> getItems() {
        items.removeIf(Objects::isNull);
        return items;
    }

    public void addObject(GameObject object) {
        interactiveObjects.add(object);
        if (object instanceof Portal) {
            addPortal((Portal) object);
        }
    }

    public void sortInteractiveObjects() {
        interactiveObjects.sort(Comparator.comparingInt(o -> o.getRectangle().getY()));
    }

    public boolean removeObject(GameObject object) {
        if (interactiveObjects.contains(object)) {
            interactiveObjects.remove(object);
            return true;
        }
        return false;
    }

    public boolean removeStorageChest(int xPosition, int yPosition) {
        Rectangle rectangle = new Rectangle(xPosition, yPosition, TILE_SIZE, TILE_SIZE);
        for (GameObject gameObject : interactiveObjects) {
            if (gameObject instanceof StorageChest && gameObject.getRectangle().intersects(rectangle)) {
                interactiveObjects.remove(gameObject);
                return true;
            }
        }
        return false;
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

    public List<WaterBowl> getWaterBowls() {
        List<WaterBowl> bowls = new ArrayList<>();
        for (GameObject object : getInteractiveObjects()) {
            if (object instanceof WaterBowl) {
                bowls.add((WaterBowl) object);
            }
        }
        return bowls;
    }

    public List<MapTile> getPillows() {
        List<MapTile> pillows = new ArrayList<>();
        if (layeredTiles.get(1) == null) {
            return pillows;
        }
        for (MapTile tile : layeredTiles.get(1)) {
            if (tile.getId() == PILLOW_TILE_ID) {
                pillows.add(tile);
            }
        }
        return pillows;
    }

    public List<StorageChest> getStorages() {
        List<StorageChest> storages = new ArrayList<>();
        for (GameObject object : getInteractiveObjects()) {
            if (object instanceof StorageChest) {
                storages.add((StorageChest) object);
            }
        }
        return storages;
    }

    public List<Wood> getWoods() {
        List<Wood> woods = new ArrayList<>();
        for (GameObject object : getInteractiveObjects()) {
            if (object instanceof Wood) {
                woods.add((Wood) object);
            }
        }
        return woods;
    }

    public List<Feather> getFeathers() {
        List<Feather> feathers = new ArrayList<>();
        for (GameObject object : getInteractiveObjects()) {
            if (object instanceof Feather) {
                feathers.add((Feather) object);
            }
        }
        return feathers;
    }

    public List<Mushroom> getMushrooms() {
        List<Mushroom> mushrooms = new ArrayList<>();
        for (GameObject object : getInteractiveObjects()) {
            if (object instanceof Mushroom) {
                mushrooms.add((Mushroom) object);
            }
        }
        return mushrooms;
    }

    public List<BigObject> getBigObjects() {
        return bigObjects;
    }

    public void addBigObject(BigObject bigObject) {
        bigObjects.add(bigObject);
    }

    public void removeBigObject(BigObject bigObject) {
        bigObjects.remove(bigObject);
    }

    public NpcSpot getNpcSpot() {
        for (GameObject gameObject : getInteractiveObjects()) {
            if (gameObject instanceof NpcSpot) {
                return (NpcSpot) gameObject;
            }
        }
        return new NpcSpot(new Rectangle(100, 100, 32, 32));
    }
}
