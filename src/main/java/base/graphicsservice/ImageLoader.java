package base.graphicsservice;

import base.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ImageLoader {

    protected static final Logger logger = LoggerFactory.getLogger(ImageLoader.class);

    public BufferedImage loadImage(String path) {
        try {
            logger.info(String.format("Will try to load - %s", path));
            BufferedImage image = ImageIO.read(Objects.requireNonNull(Game.class.getResource(path)));
            BufferedImage formattedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            formattedImage.getGraphics().drawImage(image, 0, 0, null);
            return formattedImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
