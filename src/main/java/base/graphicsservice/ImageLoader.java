package base.graphicsservice;

import base.Game;
import base.gameobjects.AnimatedSprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ImageLoader {

    private static final Logger logger = LoggerFactory.getLogger(ImageLoader.class);

    private ImageLoader() {
    }

    public static BufferedImage loadImage(String path) {
        try {
            logger.debug(String.format("Will try to load - %s", path));
            BufferedImage image = ImageIO.read(Objects.requireNonNull(Game.class.getResource(path)));
            BufferedImage formattedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            formattedImage.getGraphics().drawImage(image, 0, 0, null);
            return formattedImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AnimatedSprite getAnimatedSprite(String path, int tileSize) {
        if (path == null) {
            return null;
        }
        BufferedImage sheetImage = loadImage(path);
        SpriteSheet animalSheet = new SpriteSheet(sheetImage);
        animalSheet.loadSprites(tileSize, tileSize, 0);
        return new AnimatedSprite(animalSheet, 9, false);
    }

    public static Sprite getPreviewSprite(String previewPath) {
        if (previewPath == null) {
            return null;
        }
        BufferedImage sheetImage = loadImage(previewPath);
        return new Sprite(sheetImage);
    }
}
