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

public class PlantService {

    protected static final Logger logger = LoggerFactory.getLogger(PlantService.class);

    String carrot = "img/carrot.png";
    String carrotPreview = "img/carrot-preview.png";

    String beet = "img/beet.png";
    String beetPreview = "img/beet-preview.png";

    String tomato = "img/tomato.png";
    String tomatoPreview = "img/tomato-preview.png";

    String strawberry = "img/strawberry.png";
    String strawberryPreview = "img/strawberry-preview.png";

    String bellpepper = "img/bellpepper.png";
    String bellpepperPreview = "img/bellpepper-preview.png";

    Map<String, String> plantAnimations;
    Map<String, String> plantMapping;

    List<String> plantTypes = Arrays.asList("carrot", "beet", "tomato", "strawberry", "bellpepper");

    public PlantService() {
        plantAnimations = new HashMap<>();
        plantMapping = new HashMap<>();
        plantAnimations.put(carrotPreview, carrot);
        plantMapping.put(Carrot.NAME, carrotPreview);

        plantAnimations.put(beetPreview, beet);
        plantMapping.put(Beet.NAME, beetPreview);

        plantAnimations.put(tomatoPreview, tomato);
        plantMapping.put(Tomato.NAME, tomatoPreview);

        plantAnimations.put(strawberryPreview, strawberry);
        plantMapping.put(Strawberry.NAME, strawberryPreview);

        plantAnimations.put(bellpepperPreview, bellpepper);
        plantMapping.put(Bellpepper.NAME, bellpepperPreview);
    }

    public Plant createPlant(String plantName, int x, int y, String mapName) {
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
}
