package base.gameobjects;

import base.graphicsservice.ImageLoader;
import base.graphicsservice.Sprite;
import base.graphicsservice.SpriteSheet;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static base.Game.TILE_SIZE;

public class PlantService {

    ImageLoader imageLoader = new ImageLoader();

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
    Map<Integer, String> plantMapping;

    public PlantService() {
        plantAnimations = new HashMap<>();
        plantMapping = new HashMap<>();
        plantAnimations.put(carrotPreview, carrot);
        plantMapping.put(0, carrotPreview);

        plantAnimations.put(beetPreview, beet);
        plantMapping.put(1, beetPreview);

        plantAnimations.put(tomatoPreview, tomato);
        plantMapping.put(2, tomatoPreview);

        plantAnimations.put(strawberryPreview, strawberry);
        plantMapping.put(3, strawberryPreview);

        plantAnimations.put(bellpepperPreview, bellpepper);
        plantMapping.put(4, bellpepperPreview);
    }

    public Plant createPlant(int id, int x, int y) {
        String previewPath = plantMapping.get(id);
        Sprite previewSprite = getPreviewSprite(previewPath);
        AnimatedSprite animatedSprite = getAnimatedSprite(plantAnimations.get(previewPath));
        return new Plant(previewSprite, animatedSprite, x, y, id);
    }

    public AnimatedSprite getAnimatedSprite(String cropsPath) {
        BufferedImage sheetImage = imageLoader.loadImage(cropsPath);
        SpriteSheet spriteSheet = new SpriteSheet(sheetImage);
        spriteSheet.loadSprites(TILE_SIZE, TILE_SIZE, 0);
        AnimatedSprite animatedSprite = new AnimatedSprite(spriteSheet, 0, false);
        animatedSprite.setAnimationRange(0, 4);
        return animatedSprite;
    }

    public Sprite getPreviewSprite(String previewPath) {
        BufferedImage sheetImage = imageLoader.loadImage(previewPath);
        return new Sprite(sheetImage);
    }

    public List<Sprite> getPreviews() {
        List<Sprite> previews = new ArrayList<>();
        for (int i = 0; i < plantMapping.size(); i++) {
            previews.add(getPreviewSprite(plantMapping.get(i)));
        }
        return previews;
    }
}
