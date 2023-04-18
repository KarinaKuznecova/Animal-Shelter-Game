package base.graphicsservice;

import base.gameobjects.AnimatedSprite;
import base.gameobjects.Feather;
import base.gameobjects.Mushroom;
import base.gameobjects.Wood;
import base.gameobjects.services.PlantService;
import base.map.TileService;

import java.util.HashMap;
import java.util.Map;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.FilePath.*;
import static base.graphicsservice.ImageLoader.getPreviewSprite;

public class SpriteService {

    Map<Long, Sprite> tiles = new HashMap<>();

    /**
     * =================================== PLANTS & SEEDS ======================================
     */

    private Map<String, Sprite> plantPreview = new HashMap<>();
    private Map<String, AnimatedSprite> plantAnimatedSprites = new HashMap<>();
    private Map<String, Sprite> seedSprites = new HashMap<>();

    public void setPlantPreview(Map<String, Sprite> plantPreview) {
        this.plantPreview = plantPreview;
    }

    public void setPlantAnimatedSprites(Map<String, AnimatedSprite> plantAnimatedSprites) {
        this.plantAnimatedSprites = plantAnimatedSprites;
    }

    public void setSeedSprites(Map<String, Sprite> seedSprites) {
        this.seedSprites = seedSprites;
    }

    public Sprite getPlantPreviewSprite(String plantType) {
        if (plantType == null) {
            return null;
        }
        if (plantType.startsWith("seed")) {
            plantType = plantType.substring(4);
            return getSeedSprite(plantType);
        }
        return plantPreview.get(plantType);
    }

    public AnimatedSprite getPlantAnimatedSprite(String plantType) {
        AnimatedSprite animatedSprite = plantAnimatedSprites.get(plantType);
        return new AnimatedSprite(animatedSprite.getSprites(), animatedSprite.getSpeed(), animatedSprite.isVertical(), animatedSprite.getEndSprite());
    }

    public Sprite getSeedSprite(String plantType) {
        return seedSprites.get(plantType);
    }

    /**
     * =================================== BOWLS ======================================
     */

    private AnimatedSprite waterBowlSprite;
    private AnimatedSprite foodBowlSprite;

    public void setBowlsSprites() {
        AnimatedSprite foodBowl = ImageLoader.getAnimatedSprite(FOOD_BOWL_PATH, TILE_SIZE);
        foodBowl.setAnimationRange(0, 3);
        foodBowl.setVertical(false);
        this.foodBowlSprite = foodBowl;

        AnimatedSprite waterBowl = ImageLoader.getAnimatedSprite(WATER_BOWL_PATH, TILE_SIZE);
        waterBowl.setAnimationRange(0, 1);
        waterBowl.setVertical(false);
        this.waterBowlSprite = waterBowl;
    }

    public AnimatedSprite getWaterBowlAnimatedSprite() {
        return new AnimatedSprite(waterBowlSprite.getSprites(), waterBowlSprite.getSpeed(), waterBowlSprite.isVertical(), waterBowlSprite.getEndSprite());
    }

    public AnimatedSprite getFoodBowlAnimatedSprite() {
        return new AnimatedSprite(foodBowlSprite.getSprites(), foodBowlSprite.getSpeed(), foodBowlSprite.isVertical(), foodBowlSprite.getEndSprite());
    }

    /**
     * =================================== STORAGE CHEST ======================================
     */

    private Sprite openChestSprite;
    private Sprite closedChestSprite;

    public void setStorageChestSprites(Sprite openChestSprite, Sprite closedChestSprite) {
        this.openChestSprite = openChestSprite;
        this.closedChestSprite = closedChestSprite;
    }

    public Sprite getOpenChestSprite() {
        return openChestSprite;
    }

    public Sprite getClosedChestSprite() {
        return closedChestSprite;
    }

    /**
     * =================================== OBJECTS FROM THE FOREST ======================================
     */

    // keep in alphabetical order
    private Sprite feather;
    private Sprite mushroom;
    private Sprite wood;

    public void setFeatherSprite(Sprite feather) {
        this.feather = feather;
    }

    public void setMushroomSprite(Sprite mushroom) {
        this.mushroom = mushroom;
    }

    public void setWoodSprite(Sprite wood) {
        this.wood = wood;
    }

    public Sprite getFeatherSprite() {
        return feather;
    }

    public Sprite getMushroomSprite() {
        return mushroom;
    }

    public Sprite getWoodSprite() {
        return wood;
    }

    /**
     * =================================== TREES ======================================
     */

    private Sprite oakSprite;
    private Sprite spruceSprite;


    public void loadOakSprite() {
        oakSprite = getPreviewSprite(OAK_IMG);
    }

    public void loadSpruceSprite() {
        spruceSprite = getPreviewSprite(SPRUCE_IMG);
    }

    public Sprite getSpruceSprite() {
        return spruceSprite;
    }

    public Sprite getOakSprite() {
        return oakSprite;
    }

    /**
     * =================================== OTHER ======================================
     */

    private Sprite bushSprite;

    public void loadBushSprite() {
        bushSprite = getPreviewSprite(BUSH_IMG);
    }

    public Sprite getBushSprite() {
        return bushSprite;
    }

    public Sprite getItemSprite(String itemType, TileService tileService) {
        if (itemType != null && itemType.startsWith("seed")) {
            itemType = itemType.substring(4);
            return getSeedSprite(itemType);
        }
        else if (PlantService.plantTypes.contains(itemType)) {
            return getPlantPreviewSprite(itemType);
        }
        else if (Wood.ITEM_NAME.equalsIgnoreCase(itemType)) {
            return tileService.getTiles().get(Wood.TILE_ID).getSprite();
        }
        else if (Feather.ITEM_NAME.equalsIgnoreCase(itemType)) {
            return tileService.getTiles().get(Feather.TILE_ID).getSprite();
        }
        else if (Mushroom.ITEM_NAME.equalsIgnoreCase(itemType)) {
            return tileService.getTiles().get(Mushroom.TILE_ID).getSprite();
        }
        return null;
    }

    private Sprite cookingStoveSprite;

    public void loadCookingStoveSprite(Sprite cookingStoveSprite) {
        this.cookingStoveSprite = cookingStoveSprite;
    }

    public Sprite getCookingStoveSprite() {
        return cookingStoveSprite;
    }
}
