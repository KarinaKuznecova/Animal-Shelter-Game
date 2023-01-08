package base.map;

import base.gameobjects.*;
import base.gameobjects.storage.StorageChest;
import base.graphicsservice.Rectangle;
import base.map.bigobjects.Bookcase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static base.constants.Constants.CELL_SIZE;
import static base.constants.Constants.PILLOW_TILE_ID;
import static base.constants.MultiOptionalObjects.bookcases;

public class GameMap {

    private static final Logger logger = LoggerFactory.getLogger(GameMap.class);

    private final String mapName;

    private int backGroundTileId = -1;
    private int mapWidth = -1;
    private int mapHeight = -1;
    private int maxLayer = -1;

    private final Map<Integer, List<MapTile>> layeredTiles = new ConcurrentHashMap<>();
    private List<Plant> plants = new CopyOnWriteArrayList<>();
    private final List<Item> items = new CopyOnWriteArrayList<>();
    private final List<GameObject> interactiveObjects = new CopyOnWriteArrayList<>();
    private final List<Portal> portals = new ArrayList<>();

    public GameMap(String mapName) {
        this.mapName = mapName;
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

    public void sortInteractiveObjects() {
        interactiveObjects.sort(Comparator.comparingInt(o -> o.getRectangle().getY()));
    }

    /**
     * =================================== Setters with some logic ======================================
     */

    public void setTile(int tileX, int tileY, int tileId, int layer, boolean regularTiles) {
        if (tileId == -1) {
            return;
        }

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

    public void addObject(GameObject object) {
        if (object instanceof Portal) {
            addPortal((Portal) object);
        } else {
            interactiveObjects.add(object);
        }
    }

    private void addPortal(Portal portal) {
        portals.add(portal);
    }

    public void addItem(Item item) {
        logger.debug("Adding item to the list");
        items.add(item);
        item.setMapName(mapName);
    }

    public void addPlant(Plant plant) {
        plants.add(plant);
    }

    /**
     * =================================== Remove object ======================================
     */

    public boolean removeTile(int tileX, int tileY, int layer, boolean regularTiles, int selectedTile) {
        if (bookcases.contains(selectedTile) && regularTiles) {
            boolean removed = false;
            for (MapTile tile : new Bookcase(tileX, tileY, bookcases.indexOf(selectedTile), 1).getObjectParts()) {
                removed = removeTile(tile.getX(), tile.getY(), tile.getLayer(), false, tile.getId());
            }
            return removed;
        }
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

    public boolean removeObject(GameObject object) {
        if (interactiveObjects.contains(object)) {
            interactiveObjects.remove(object);
            return true;
        }
        return false;
    }

    public boolean removeStorageChest(int xPosition, int yPosition) {
        Rectangle rectangle = new Rectangle(xPosition, yPosition, CELL_SIZE, CELL_SIZE);
        for (GameObject gameObject : interactiveObjects) {
            if (gameObject instanceof StorageChest && gameObject.getRectangle().intersects(rectangle)) {
                interactiveObjects.remove(gameObject);
                return true;
            }
        }
        return false;
    }

    public void removePlant(Plant plant) {
        plants.remove(plant);
    }

    /**
     * =================================== Getters with some logic ======================================
     */

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

    public NpcSpot getNpcSpot() {
        for (GameObject gameObject : getInteractiveObjects()) {
            if (gameObject instanceof NpcSpot) {
                return (NpcSpot) gameObject;
            }
        }
        return new NpcSpot(new Rectangle(100, 100, 32, 32));
    }

    public List<Plant> getWildPlants() {
        return plants.stream().filter(Plant::isWild).collect(Collectors.toList());
    }

    public List<MapTile> getTilesOnLayer(int layer) {
        return layeredTiles.get(layer);
    }

    public List<Item> getItems() {
        items.removeIf(Objects::isNull);
        return items;
    }

    /**
     * =================================== Simple getters & setters ======================================
     */

    public int getBackGroundTileId() {
        return backGroundTileId;
    }

    public void setBackGroundTileId(int backGroundTileId) {
        this.backGroundTileId = backGroundTileId;
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

    public List<GameObject> getInteractiveObjects() {
        return interactiveObjects;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public List<Portal> getPortals() {
        return portals;
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
}
