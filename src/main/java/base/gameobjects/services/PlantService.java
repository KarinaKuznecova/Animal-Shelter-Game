package base.gameobjects.services;

import base.gameobjects.AnimatedSprite;
import base.gameobjects.Plant;
import base.gameobjects.plants.*;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        plantAnimations.put(CARROT_PREVIEW, CARROT_IMG);
        plantMapping.put(Carrot.NAME, CARROT_PREVIEW);
        seedMapping.put(Carrot.NAME, CARROT_SEEDS);

        plantAnimations.put(BEET_PREVIEW, BEET_IMG);
        plantMapping.put(Beet.NAME, BEET_PREVIEW);
        seedMapping.put(Beet.NAME, BEET_SEEDS);

        plantAnimations.put(TOMATO_PREVIEW, TOMATO_IMG);
        plantMapping.put(Tomato.NAME, TOMATO_PREVIEW);
        seedMapping.put(Tomato.NAME, TOMATO_SEEDS);

        plantAnimations.put(STRAWBERRY_PREVIEW, STRAWBERRY_IMG);
        plantMapping.put(Strawberry.NAME, STRAWBERRY_PREVIEW);
        seedMapping.put(Strawberry.NAME, STRAWBERRY_SEEDS);

        plantAnimations.put(BELLPEPPER_PREVIEW, BELLPEPPER_IMG);
        plantMapping.put(Bellpepper.NAME, BELLPEPPER_PREVIEW);
        seedMapping.put(Bellpepper.NAME, BELLPEPPER_SEEDS);

        plantAnimations.put(CORN_PREVIEW, CORN_IMG);
        plantMapping.put(Corn.NAME, CORN_PREVIEW);
        seedMapping.put(Corn.NAME, CORN_SEEDS);

        plantAnimations.put(POTATO_PREVIEW, POTATO_IMG);
        plantMapping.put(Potato.NAME, POTATO_PREVIEW);
        seedMapping.put(Potato.NAME, POTATO_SEEDS);
    }

    public Plant createPlant(String plantName, int x, int y) {
        String previewPath = plantMapping.get(plantName);
        Sprite previewSprite = ImageLoader.getPreviewSprite(previewPath);
        AnimatedSprite animatedSprite;
        if (Corn.NAME.equals(plantName)) {
            animatedSprite = ImageLoader.getAnimatedSprite(plantAnimations.get(previewPath), 32, 64);
            y -= 64;
        } else {
            animatedSprite = ImageLoader.getAnimatedSprite(plantAnimations.get(previewPath), 32);
        }
        animatedSprite.setSpeed(0);
        animatedSprite.setAnimationRange(0, 4);

        switch (plantName) {
            case Beet.NAME:
                return new Beet(previewSprite, animatedSprite, x, y, plantName);
            case Bellpepper.NAME:
                return new Bellpepper(previewSprite, animatedSprite, x, y, plantName);
            case Carrot.NAME:
                return new Carrot(previewSprite, animatedSprite, x, y, plantName);
            case Strawberry.NAME:
                return new Strawberry(previewSprite, animatedSprite, x, y, plantName);
            case Tomato.NAME:
                return new Tomato(previewSprite, animatedSprite, x, y, plantName);
            case Corn.NAME:
                return new Corn(previewSprite, animatedSprite, x, y, plantName);
            case Potato.NAME:
                return new Potato(previewSprite, animatedSprite, x, y, plantName);
            default:
                logger.error(String.format("Unknown plant requested or plant not defined : %s", plantName));
                throw new IllegalArgumentException();
        }
    }

    public Map<String, Sprite> getPreviews() {
        Map<String, Sprite> previews = new HashMap<>();
        for (String plantType : plantTypes) {
            previews.put(plantType, ImageLoader.getPreviewSprite(plantMapping.get(plantType)));
        }
        return previews;
    }

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

    public Sprite getSeedSprite(String plantName) {
        return ImageLoader.getPreviewSprite(seedMapping.get(plantName));
    }
}
