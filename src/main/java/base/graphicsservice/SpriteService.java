package base.graphicsservice;

import base.gameobjects.AnimatedSprite;

import java.util.HashMap;
import java.util.Map;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.FilePath.FOOD_BOWL_PATH;
import static base.constants.FilePath.WATER_BOWL_PATH;

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
        foodBowl.setAnimationRange(0, 1);
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
}
