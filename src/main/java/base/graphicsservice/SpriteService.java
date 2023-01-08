package base.graphicsservice;

import base.gameobjects.AnimatedSprite;

import java.util.HashMap;
import java.util.Map;

public class SpriteService {

    Map<Long, Sprite> tiles = new HashMap<>();

    /**
     * =================================== PLANTS & SEEDS ======================================
     */

    Map<String, Sprite> plantPreview = new HashMap<>();
    Map<String, AnimatedSprite> plantAnimatedSprites = new HashMap<>();
    Map<String, Sprite> seedSprites = new HashMap<>();

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

    public AnimatedSprite getPlantAnimatedSprite_bkp(String plantType) {
        return plantAnimatedSprites.get(plantType);
    }

    public Sprite getSeedSprite(String plantType) {
        return seedSprites.get(plantType);
    }

}
