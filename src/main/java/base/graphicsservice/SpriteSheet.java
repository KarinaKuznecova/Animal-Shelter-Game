package base.graphicsservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

public class SpriteSheet {

    private BufferedImage image;
    private final int totalWidth;
    private final int totalHeight;
    private int[] pixels;

    private Sprite[] loadedSprites = null;
    private int spriteSizeX;
    private boolean loaded = false;

    protected static final Logger logger = LoggerFactory.getLogger(SpriteSheet.class);

    public SpriteSheet(BufferedImage sheetImage) {
        image = sheetImage;
        totalWidth = sheetImage.getWidth();
        totalHeight = sheetImage.getHeight();

        pixels = new int[totalWidth * totalHeight];
        pixels = sheetImage.getRGB(0, 0, totalWidth, totalHeight, pixels, 0, totalWidth);
    }

    public void loadSprites(int spriteSizeX, int spriteSizeY, int paddingWidth) {
        this.spriteSizeX = spriteSizeX;
        loadedSprites = new Sprite[(totalWidth / spriteSizeX) * (totalHeight / spriteSizeY)];
        int spriteId = 0;

        for (int i = 0; i < totalHeight; i += spriteSizeY + paddingWidth) {
            for (int j = 0; j < totalWidth; j += spriteSizeX + paddingWidth) {
                loadedSprites[spriteId] = new Sprite(this, j, i, spriteSizeX, spriteSizeY);
                spriteId++;
            }
        }
        logger.info("Sprites loaded successfully");
        loaded = true;
    }

    public Sprite getSprite(int x, int y) {
        if (loaded) {
            int spriteId = x + y * (totalWidth / spriteSizeX);
            if (spriteId < loadedSprites.length) {
                return loadedSprites[spriteId];
            } else {
                logger.error("Wanted sprite id is out of bounds");
            }
        } else {
            logger.error("Sprites not loaded");
        }
        return null;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Sprite[] getLoadedSprites() {
        return loadedSprites;
    }
}
