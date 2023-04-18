package base.map;

import base.gameobjects.*;
import base.gameobjects.npc.Npc;
import base.gameobjects.npc.NpcSpawnSpot;
import base.gameobjects.npc.NpcSpot;
import base.gameobjects.npc.NpcType;
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
    // every plant type separate?
    private List<Plant> plants = new CopyOnWriteArrayList<>();
    // every type separate?
    private final List<Item> items = new CopyOnWriteArrayList<>();
    // maybe water and food separate?
    private final List<FoodBowl> foodBowls = new CopyOnWriteArrayList<>();
    private final List<WaterBowl> waterBowls = new CopyOnWriteArrayList<>();
    private final List<StorageChest> storageChests = new CopyOnWriteArrayList<>();
    private final List<Feather> feathers = new CopyOnWriteArrayList<>();
    private final List<Mushroom> mushrooms = new CopyOnWriteArrayList<>();
    private final List<Wood> woods = new CopyOnWriteArrayList<>();
    private final List<Bush> bushes = new CopyOnWriteArrayList<>();
    private final List<Oak> oaks = new CopyOnWriteArrayList<>();
    private final List<Spruce> spruces = new CopyOnWriteArrayList<>();
    private List<CookingStove> cookingStoves = new CopyOnWriteArrayList<>();
    private final List<NpcSpot> npcSpots = new CopyOnWriteArrayList<>();
    private final List<NpcSpawnSpot> npcSpawnSpots = new CopyOnWriteArrayList<>();
    private transient List<Npc> npcs = new CopyOnWriteArrayList<>();

    private transient List<GameObject> interactiveObjects = new CopyOnWriteArrayList<>();
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
        if (interactiveObjects == null) {
            interactiveObjects = new CopyOnWriteArrayList<>();
        }
        if (object instanceof Portal) {
            addPortal((Portal) object);
        } else if (object instanceof Feather) {
            feathers.add((Feather) object);
        } else if (object instanceof Mushroom) {
            mushrooms.add((Mushroom) object);
        } else if (object instanceof Wood) {
            woods.add((Wood) object);
        } else if (object instanceof Bush) {
            bushes.add((Bush) object);
        } else if (object instanceof Oak) {
            oaks.add((Oak) object);
        } else if (object instanceof Spruce) {
            spruces.add((Spruce) object);
        } else if (object instanceof CookingStove) {
            cookingStoves.add((CookingStove) object);
        } else if (object instanceof NpcSpot) {
            npcSpots.add((NpcSpot) object);
        } else if (object instanceof NpcSpawnSpot) {
            npcSpawnSpots.add((NpcSpawnSpot) object);
        } else if (object instanceof Npc) {
            npcs.add((Npc) object);
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

    public void addFoodBowl(FoodBowl bowl) {
        foodBowls.add(bowl);
    }

    public void addWaterBowl(WaterBowl bowl) {
        waterBowls.add(bowl);
    }

    public void addStorageChest(StorageChest storageChest) {
        storageChests.add(storageChest);
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
            woods.removeIf(wood -> rectangle.intersects(wood.getRectangle()));
        }
        if (itemName.equalsIgnoreCase(Feather.ITEM_NAME)) {
            feathers.removeIf(feather -> rectangle.intersects(feather.getRectangle()));
        }
        if (itemName.equalsIgnoreCase(Mushroom.ITEM_NAME)) {
            mushrooms.removeIf(mushroom -> rectangle.intersects(mushroom.getRectangle()));
        }
    }

    public boolean removeObject(GameObject object) {
        if (interactiveObjects.contains(object)) {
            interactiveObjects.remove(object);
            return true;
        }
        if (object instanceof Npc) {
            if (npcs.contains(object)) {
                npcs.remove(object);
                return true;
            }
        }
        return false;
    }

    public boolean removeStorageChest(int xPosition, int yPosition) {
        Rectangle rectangle = new Rectangle(xPosition, yPosition, CELL_SIZE, CELL_SIZE);
        for (StorageChest chest : storageChests) {
            if (chest.getRectangle().intersects(rectangle)) {
                storageChests.remove(chest);
                return true;
            }
        }
        return false;
    }

    public boolean removeCookingStove(int xPosition, int yPosition) {
        Rectangle rectangle = new Rectangle(xPosition, yPosition, CELL_SIZE, CELL_SIZE);
        for (CookingStove cookingStove : cookingStoves) {
            if (cookingStove.getRectangle().intersects(rectangle)) {
                cookingStoves.remove(cookingStove);
                return true;
            }
        }
        return false;
    }

    public void removePlant(Plant plant) {
        plants.remove(plant);
    }

    // TODO: check by x and y instead of full object
    public boolean removeBowl(Bowl bowl) {
        if (foodBowls.contains(bowl)) {
            foodBowls.remove(bowl);
            return true;
        }
        if (waterBowls.contains(bowl)) {
            waterBowls.remove(bowl);
            return true;
        }
        return false;
    }

    /**
     * =================================== Getters with some logic ======================================
     */

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
        if (npcSpots.isEmpty()) {
            return null;
        }
        for (NpcSpot spot : npcSpots) {
            if (spot.getNpcType() == type) {
                return spot;
            }
        }
        return npcSpots.get(0);
    }

    public NpcSpawnSpot getNpcSpawnSpotByType(NpcType type) {
        if (npcSpawnSpots.isEmpty()) {
            return null;
        }
        for (NpcSpawnSpot spawnSpot : npcSpawnSpots) {
            if (spawnSpot.getNpcType() == type) {
                return spawnSpot;
            }
        }
        return npcSpawnSpots.get(0);
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
        if (interactiveObjects == null) {
            interactiveObjects = new CopyOnWriteArrayList<>();
        }
        return interactiveObjects;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public List<FoodBowl> getFoodBowls() {
        return foodBowls;
    }

    public List<WaterBowl> getWaterBowls() {
        return waterBowls;
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public List<StorageChest> getStorageChests() {
        return storageChests;
    }

    public List<Wood> getWoods() {
        return woods;
    }

    public List<Feather> getFeathers() {
        return feathers;
    }

    public List<Mushroom> getMushrooms() {
        return mushrooms;
    }

    public List<Bush> getBushes() {
        return bushes;
    }

    public List<Oak> getOaks() {
        return oaks;
    }

    public List<Spruce> getSpruces() {
        return spruces;
    }

    public List<CookingStove> getCookingStoves() {
        if (cookingStoves == null) {
            cookingStoves = new CopyOnWriteArrayList<>();
        }
        return cookingStoves;
    }

    public List<NpcSpot> getNpcSpots() {
        return npcSpots;
    }

    public List<Npc> getNpcs() {
        return npcs;
    }

    public List<NpcSpawnSpot> getNpcSpawnSpots() {
        return npcSpawnSpots;
    }

    public void setNpcs(List<Npc> npcs) {
        this.npcs = npcs;
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
