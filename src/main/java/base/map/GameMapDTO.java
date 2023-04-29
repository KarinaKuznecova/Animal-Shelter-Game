package base.map;

import base.gameobjects.*;
import base.gameobjects.npc.NpcSpawnSpot;
import base.gameobjects.npc.NpcSpot;
import base.gameobjects.storage.StorageChest;
import base.gameobjects.tree.Oak;
import base.gameobjects.tree.Spruce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMapDTO {

    private String gameVersion;
    private String mapName;

    private int backGroundTileId = -1;
    private int mapWidth = -1;
    private int mapHeight = -1;
    private int maxLayer = -1;

    private Map<Integer, List<MapTile>> layeredTiles = new HashMap<>();

    private List<Plant> plants = new ArrayList<>();
    private List<Item> items = new ArrayList<>();
    private List<FoodBowl> foodBowls = new ArrayList<>();
    private List<WaterBowl> waterBowls = new ArrayList<>();
    private List<StorageChest> storageChests = new ArrayList<>();
    private List<Feather> feathers = new ArrayList<>();
    private List<Mushroom> mushrooms = new ArrayList<>();
    private List<Wood> woods = new ArrayList<>();
    private List<Bush> bushes = new ArrayList<>();
    private List<Oak> oaks = new ArrayList<>();
    private List<Spruce> spruces = new ArrayList<>();
    private List<CookingStove> cookingStoves = new ArrayList<>();
    private List<Fridge> fridges = new ArrayList<>();
    private List<NpcSpot> npcSpots = new ArrayList<>();
    private List<NpcSpawnSpot> npcSpawnSpots = new ArrayList<>();
    private List<Portal> portals = new ArrayList<>();

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public int getBackGroundTileId() {
        return backGroundTileId;
    }

    public void setBackGroundTileId(int backGroundTileId) {
        this.backGroundTileId = backGroundTileId;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public int getMaxLayer() {
        return maxLayer;
    }

    public void setMaxLayer(int maxLayer) {
        this.maxLayer = maxLayer;
    }

    public Map<Integer, List<MapTile>> getLayeredTiles() {
        return layeredTiles;
    }

    public void setLayeredTiles(Map<Integer, List<MapTile>> layeredTiles) {
        this.layeredTiles = layeredTiles;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<FoodBowl> getFoodBowls() {
        return foodBowls;
    }

    public void setFoodBowls(List<FoodBowl> foodBowls) {
        this.foodBowls = foodBowls;
    }

    public List<WaterBowl> getWaterBowls() {
        return waterBowls;
    }

    public void setWaterBowls(List<WaterBowl> waterBowls) {
        this.waterBowls = waterBowls;
    }

    public List<StorageChest> getStorageChests() {
        return storageChests;
    }

    public void setStorageChests(List<StorageChest> storageChests) {
        this.storageChests = storageChests;
    }

    public List<Feather> getFeathers() {
        return feathers;
    }

    public void setFeathers(List<Feather> feathers) {
        this.feathers = feathers;
    }

    public List<Mushroom> getMushrooms() {
        return mushrooms;
    }

    public void setMushrooms(List<Mushroom> mushrooms) {
        this.mushrooms = mushrooms;
    }

    public List<Wood> getWoods() {
        return woods;
    }

    public void setWoods(List<Wood> woods) {
        this.woods = woods;
    }

    public List<Bush> getBushes() {
        return bushes;
    }

    public void setBushes(List<Bush> bushes) {
        this.bushes = bushes;
    }

    public List<Oak> getOaks() {
        return oaks;
    }

    public void setOaks(List<Oak> oaks) {
        this.oaks = oaks;
    }

    public List<Spruce> getSpruces() {
        return spruces;
    }

    public void setSpruces(List<Spruce> spruces) {
        this.spruces = spruces;
    }

    public List<CookingStove> getCookingStoves() {
        return cookingStoves;
    }

    public void setCookingStoves(List<CookingStove> cookingStoves) {
        this.cookingStoves = cookingStoves;
    }

    public List<Fridge> getFridges() {
        return fridges;
    }

    public void setFridges(List<Fridge> fridges) {
        this.fridges = fridges;
    }

    public List<NpcSpot> getNpcSpots() {
        return npcSpots;
    }

    public void setNpcSpots(List<NpcSpot> npcSpots) {
        this.npcSpots = npcSpots;
    }

    public List<NpcSpawnSpot> getNpcSpawnSpots() {
        return npcSpawnSpots;
    }

    public void setNpcSpawnSpots(List<NpcSpawnSpot> npcSpawnSpots) {
        this.npcSpawnSpots = npcSpawnSpots;
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public void setPortals(List<Portal> portals) {
        this.portals = portals;
    }
}
