package base.map;

import base.gameobjects.*;
import base.gameobjects.npc.*;
import base.gameobjects.storage.StorageChest;
import base.gameobjects.tree.Oak;
import base.gameobjects.tree.Spruce;
import base.gameobjects.tree.Tree;
import base.graphicsservice.Rectangle;
import base.map.bigobjects.Bookcase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static base.constants.Constants.*;
import static base.constants.MultiOptionalObjects.bookcases;

public class GameMap {

    private static final Logger logger = LoggerFactory.getLogger(GameMap.class);

    private final String mapName;
    private String mapVersion;

    private int backGroundTileId = -1;
    private int mapWidth = -1;
    private int mapHeight = -1;
    private int maxLayer = -1;

    private Map<Integer, List<MapTile>> layeredTiles = new ConcurrentHashMap<>();
    private final List<GameObject> gameMapObjects = new CopyOnWriteArrayList<>();

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

    public MapTile getExistingTile(int tileX, int tileY) {
        for (List<MapTile> mapTiles : layeredTiles.values()) {
            for (MapTile tile : mapTiles) {
                if (tile.getX() == tileX && tile.getY() == tileY) {
                    return tile;
                }
            }
        }
        return null;
    }

    public void addObject(GameObject object) {
        gameMapObjects.add(object);
    }

    public void addItem(Item item) {
        logger.debug("Adding item to the list");
        item.setMapName(mapName);
        gameMapObjects.add(item);
    }

    public void addPlant(Plant plant) {
        gameMapObjects.add(plant);
    }

    public void addFoodBowl(FoodBowl bowl) {
        gameMapObjects.add(bowl);
    }

    public void addWaterBowl(WaterBowl bowl) {
        gameMapObjects.add(bowl);
    }

    public void addStorageChest(StorageChest storageChest) {
        gameMapObjects.add(storageChest);
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
                return layeredTiles.get(layer).remove(mapTile);
            }
        }
        return false;
    }

    public void removeItem(String itemName, Rectangle rectangle) {
        List<GameObject> gameObjects = getGameObjectFromPosition(rectangle);
        for (GameObject gameObject : gameObjects) {
            if (!CHEATS_MODE &&
                    (gameObject instanceof Spruce || gameObject instanceof Oak || gameObject instanceof Bush)) {
                continue;
            }
            if ((gameObject instanceof Item && ((Item) gameObject).getItemName().equalsIgnoreCase(itemName))
                    || (gameObject instanceof Wood || gameObject instanceof Feather || gameObject instanceof Mushroom)) {
                gameMapObjects.remove(gameObject);
                break;
            }
        }
    }

    public boolean removeObject(GameObject object) {
        return gameMapObjects.remove(object);
    }

    public boolean removeStorageChest(int xPosition, int yPosition) {
        List<GameObject> gameObjects = getGameObjectFromPosition(xPosition, yPosition);
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof StorageChest) {
                return gameMapObjects.remove(gameObject);
            }
        }
        return false;
    }

    public boolean removeCookingStove(int xPosition, int yPosition) {
        List<GameObject> gameObjects = getGameObjectFromPosition(xPosition, yPosition);
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof CookingStove) {
                return gameMapObjects.remove(gameObject);
            }
        }
        return false;
    }

    public boolean removeFridge(int xPosition, int yPosition) {
        List<GameObject> gameObjects = getGameObjectFromPosition(xPosition, yPosition);
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Fridge) {
                return gameMapObjects.remove(gameObject);
            }
        }
        return false;
    }

    public void removePlant(Plant plant) {
        gameMapObjects.remove(plant);
    }

    public boolean removeBowl(Bowl bowl) {
        return gameMapObjects.remove(bowl);
    }

    /**
     * =================================== Getters with some logic ======================================
     */

    public List<GameObject> getGameObjectsNearPosition(int xPosition, int yPosition) {
        Rectangle rectangle = new Rectangle(xPosition - (CELL_SIZE), yPosition - (CELL_SIZE * 2), CELL_SIZE * 2, CELL_SIZE * 4);
        return getGameObjectFromPosition(rectangle);
    }

    public List<GameObject> getGameObjectFromPosition(int xPosition, int yPosition) {
        Rectangle rectangle = new Rectangle(xPosition, yPosition, CELL_SIZE, CELL_SIZE);
        return getGameObjectFromPosition(rectangle);
    }

    public List<GameObject> getGameObjectFromPosition(Rectangle rectangle) {
        List<GameObject> objectsInPosition = new ArrayList<>();
        for (GameObject gameObject : gameMapObjects) {
            if (gameObject.getRectangle().intersects(rectangle)) {
                objectsInPosition.add(gameObject);
            }
        }
        return objectsInPosition;
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

    public NpcSpot getNpcSpot(NpcType type) {
        for (GameObject gameObject : gameMapObjects) {
                if (gameObject instanceof NpcSpot) {
                    return (NpcSpot) gameObject;
                }
            }
        return null;
    }

    public NpcSpawnSpot getNpcSpawnSpotByType(NpcType type) {
        for (GameObject gameObject : gameMapObjects) {
                if (gameObject instanceof NpcSpawnSpot && ((NpcSpawnSpot) gameObject).getNpcType() == type) {
                    return (NpcSpawnSpot) gameObject;
                }
            }
        return null;
    }

    public List<Plant> getWildPlants() {
        return gameMapObjects.stream()
                    .filter(Plant.class::isInstance)
                    .filter(gameObject -> ((Plant) gameObject).isWild())
                    .map(Plant.class::cast)
                    .collect(Collectors.toList());
    }

    public List<MapTile> getTilesOnLayer(int layer) {
        return layeredTiles.get(layer);
    }

    public List<Item> getItems() {
        return gameMapObjects.stream()
                    .filter(Item.class::isInstance)
                    .map(Item.class::cast)
                    .collect(Collectors.toList());
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

    public List<GameObject> getGameMapObjects() {
        return gameMapObjects;
    }

    public List<Plant> getPlants() {
        return gameMapObjects.stream()
                    .filter(Plant.class::isInstance)
                    .map(Plant.class::cast)
                    .collect(Collectors.toList());
    }

    public List<FoodBowl> getFoodBowls() {
        return gameMapObjects.stream()
                    .filter(FoodBowl.class::isInstance)
                    .map(FoodBowl.class::cast)
                    .collect(Collectors.toList());
    }

    public List<WaterBowl> getWaterBowls() {
        return gameMapObjects.stream()
                    .filter(WaterBowl.class::isInstance)
                    .map(WaterBowl.class::cast)
                    .collect(Collectors.toList());
    }

    public List<Portal> getPortals() {
        return gameMapObjects.stream()
                    .filter(Portal.class::isInstance)
                    .map(Portal.class::cast)
                    .collect(Collectors.toList());
    }

    public List<StorageChest> getStorageChests() {
        return gameMapObjects.stream()
                    .filter(StorageChest.class::isInstance)
                    .map(StorageChest.class::cast)
                    .collect(Collectors.toList());
    }

    public List<Wood> getWoods() {
        return gameMapObjects.stream()
                    .filter(Wood.class::isInstance)
                    .map(Wood.class::cast)
                    .collect(Collectors.toList());
    }

    public List<Feather> getFeathers() {
        return gameMapObjects.stream()
                    .filter(Feather.class::isInstance)
                    .map(Feather.class::cast)
                    .collect(Collectors.toList());
    }

    public List<Mushroom> getMushrooms() {
        return gameMapObjects.stream()
                    .filter(Mushroom.class::isInstance)
                    .map(Mushroom.class::cast)
                    .collect(Collectors.toList());
    }

    public List<Bush> getBushes() {
        return gameMapObjects.stream()
                    .filter(Bush.class::isInstance)
                    .map(Bush.class::cast)
                    .collect(Collectors.toList());
    }

    public List<Oak> getOaks() {
        return gameMapObjects.stream()
                    .filter(Oak.class::isInstance)
                    .map(Oak.class::cast)
                    .collect(Collectors.toList());
    }

    public List<Spruce> getSpruces() {
        return gameMapObjects.stream()
                    .filter(Spruce.class::isInstance)
                    .map(Spruce.class::cast)
                    .collect(Collectors.toList());
    }

    public List<CookingStove> getCookingStoves() {
        return gameMapObjects.stream()
                    .filter(CookingStove.class::isInstance)
                    .map(CookingStove.class::cast)
                    .collect(Collectors.toList());
    }

    public List<Fridge> getFridges() {
        return gameMapObjects.stream()
                    .filter(Fridge.class::isInstance)
                    .map(Fridge.class::cast)
                    .collect(Collectors.toList());
    }

    public List<NpcSpot> getNpcSpots() {
        return gameMapObjects.stream()
                    .filter(NpcSpot.class::isInstance)
                    .map(NpcSpot.class::cast)
                    .collect(Collectors.toList());
    }

    public NpcAdoption getAdoptionNpc() {
        return getNpcs().stream()
                .filter(NpcAdoption.class::isInstance)
                .map(NpcAdoption.class::cast)
                .findFirst()
                .orElse(null);
    }

    public List<Npc> getNpcs() {
        return gameMapObjects.stream()
                .filter(Npc.class::isInstance)
                .map(Npc.class::cast)
                .collect(Collectors.toList());
    }

    public List<NpcSpawnSpot> getNpcSpawnSpots() {
        return gameMapObjects.stream()
                .filter(NpcSpawnSpot.class::isInstance)
                .map(NpcSpawnSpot.class::cast)
                .collect(Collectors.toList());
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

    public void setLayeredTiles(Map<Integer, List<MapTile>> layeredTiles) {
        this.layeredTiles = layeredTiles;
    }

    public void addGameObjects(List<? extends GameObject> gameObjects) {
        gameMapObjects.addAll(gameObjects);
    }

    public String getMapVersion() {
        return mapVersion;
    }

    public void setMapVersion(String mapVersion) {
        this.mapVersion = mapVersion;
    }

    public void sortGameObjects() {
        gameMapObjects.sort(Comparator.comparingInt(o -> (o.getRectangle().getY() + (o.getRectangle().getHeight() * ZOOM))));
    }
}
