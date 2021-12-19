package base.gameobjects;

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

    Map<String, String> plantAnimations;
    Map<String, String> plantMapping;

    List<String> plantTypes = Arrays.asList("carrot", "beet", "tomato", "strawberry", "bellpepper");

    public PlantService() {
        plantAnimations = new HashMap<>();
        plantMapping = new HashMap<>();
        plantAnimations.put(CARROT_PREVIEW, CARROT);
        plantMapping.put(Carrot.NAME, CARROT_PREVIEW);

        plantAnimations.put(BEET_PREVIEW, BEET);
        plantMapping.put(Beet.NAME, BEET_PREVIEW);

        plantAnimations.put(TOMATO_PREVIEW, TOMATO);
        plantMapping.put(Tomato.NAME, TOMATO_PREVIEW);

        plantAnimations.put(STRAWBERRY_PREVIEW, STRAWBERRY);
        plantMapping.put(Strawberry.NAME, STRAWBERRY_PREVIEW);

        plantAnimations.put(BELLPEPPER_PREVIEW, BELLPEPPER);
        plantMapping.put(Bellpepper.NAME, BELLPEPPER_PREVIEW);
    }

    public Plant createPlant(String plantName, int x, int y) {
        String previewPath = plantMapping.get(plantName);
        Sprite previewSprite = ImageLoader.getPreviewSprite(previewPath);
        AnimatedSprite animatedSprite = ImageLoader.getAnimatedSprite(plantAnimations.get(previewPath), 32);
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
        return ImageLoader.getPreviewSprite(plantMapping.get(plantName));
    }
}
