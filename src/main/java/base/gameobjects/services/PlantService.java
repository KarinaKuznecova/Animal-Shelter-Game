package base.gameobjects.services;

import base.gameobjects.AnimatedSprite;
import base.gameobjects.Plant;
import base.gameobjects.plants.*;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Sprite;
import base.graphicsservice.SpriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.FilePath.*;

public class PlantService {

    protected static final Logger logger = LoggerFactory.getLogger(PlantService.class);

    public Map<String, String> plantAnimations;
    public Map<String, String> plantMapping;
    public Map<String, String> seedMapping;

    public static List<String> plantTypes = Arrays.asList(Carrot.NAME, Beet.NAME, Tomato.NAME, Strawberry.NAME, Bellpepper.NAME, Corn.NAME, Potato.NAME);

    public PlantService() {
        plantAnimations = new HashMap<>();
        plantMapping = new HashMap<>();
        seedMapping = new HashMap<>();

        plantAnimations.put(Carrot.NAME, CARROT_IMG);
        plantMapping.put(Carrot.NAME, CARROT_PREVIEW);
        seedMapping.put(Carrot.NAME, CARROT_SEEDS);

        plantAnimations.put(Beet.NAME, BEET_IMG);
        plantMapping.put(Beet.NAME, BEET_PREVIEW);
        seedMapping.put(Beet.NAME, BEET_SEEDS);

        plantAnimations.put(Tomato.NAME, TOMATO_IMG);
        plantMapping.put(Tomato.NAME, TOMATO_PREVIEW);
        seedMapping.put(Tomato.NAME, TOMATO_SEEDS);

        plantAnimations.put(Strawberry.NAME, STRAWBERRY_IMG);
        plantMapping.put(Strawberry.NAME, STRAWBERRY_PREVIEW);
        seedMapping.put(Strawberry.NAME, STRAWBERRY_SEEDS);

        plantAnimations.put(Bellpepper.NAME, BELLPEPPER_IMG);
        plantMapping.put(Bellpepper.NAME, BELLPEPPER_PREVIEW);
        seedMapping.put(Bellpepper.NAME, BELLPEPPER_SEEDS);

        plantAnimations.put(Corn.NAME, CORN_IMG);
        plantMapping.put(Corn.NAME, CORN_PREVIEW);
        seedMapping.put(Corn.NAME, CORN_SEEDS);

        plantAnimations.put(Potato.NAME, POTATO_IMG);
        plantMapping.put(Potato.NAME, POTATO_PREVIEW);
        seedMapping.put(Potato.NAME, POTATO_SEEDS);
    }

    public Plant createPlant(SpriteService spriteService, String plantName, int x, int y) {
        Plant plant = createPlant(plantName, x, y);
        plant.setAnimatedSprite(spriteService.getPlantAnimatedSprite(plant.getPlantType()));
        plant.setPreviewSprite(spriteService.getPlantPreviewSprite(plant.getPlantType()));
        return plant;
    }

    public Plant createPlant(String plantName, int x, int y) {
        if (Corn.NAME.equals(plantName)) {
            y -= 64;
        }
        Plant newPlant;
        switch (plantName) {
            case Beet.NAME:
                newPlant = new Beet(x, y, plantName);
                break;
            case Bellpepper.NAME:
                newPlant =  new Bellpepper(x, y, plantName);
                break;
            case Carrot.NAME:
                newPlant =  new Carrot(x, y, plantName);
                break;
            case Strawberry.NAME:
                newPlant =  new Strawberry(x, y, plantName);
                break;
            case Tomato.NAME:
                newPlant =  new Tomato(x, y, plantName);
                break;
            case Corn.NAME:
                newPlant =  new Corn(x, y, plantName);
                break;
            case Potato.NAME:
                newPlant =  new Potato(x, y, plantName);
                break;
            default:
                logger.error(String.format("Unknown plant requested or plant not defined : %s", plantName));
                throw new IllegalArgumentException();
        }
        return newPlant;
    }

    public Map<String, Sprite> getPreviews() {
        Map<String, Sprite> previews = new HashMap<>();
        for (String plantType : plantTypes) {
            previews.put(plantType, ImageLoader.getPreviewSprite(plantMapping.get(plantType)));
        }
        return previews;
    }

    public Map<String, Sprite> getSeedSprites() {
        Map<String, Sprite> seedSprites = new HashMap<>();
        for (String plantType : plantTypes) {
            seedSprites.put(plantType, ImageLoader.getPreviewSprite(seedMapping.get(plantType)));
        }
        return seedSprites;
    }

    public Map<String, AnimatedSprite> getAnimatedSprites() {
        Map<String, AnimatedSprite> animatedSprites = new HashMap<>();
        for (String plantType : plantTypes) {
            AnimatedSprite animatedSprite;
            if (plantType.equalsIgnoreCase(Corn.NAME)) {
                animatedSprite = ImageLoader.getAnimatedSprite(plantAnimations.get(plantType), 32, 64);
            } else {
                animatedSprite = ImageLoader.getAnimatedSprite(plantAnimations.get(plantType), TILE_SIZE);
            }
            animatedSprite.setSpeed(0);
            animatedSprite.setAnimationRange(0, 4);
            animatedSprites.put(plantType, animatedSprite);
        }
        return animatedSprites;
    }

    @Deprecated
    public Sprite getPlantSprite(String plantName) {
        if (plantName == null) {
            return null;
        }
        if (plantName.startsWith("seed")) {
            plantName = plantName.substring(4);
            return getSeedSprite(plantName);
        }
        return ImageLoader.getPreviewSprite(plantMapping.get(plantName));
    }

    @Deprecated
    public Sprite getSeedSprite(String plantName) {
        return ImageLoader.getPreviewSprite(seedMapping.get(plantName));
    }
}
