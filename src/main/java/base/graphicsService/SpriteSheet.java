package base.graphicsService;

import java.awt.image.BufferedImage;

public class SpriteSheet {

    private BufferedImage image;
    private final int TOTAL_WIDTH;
    private final int TOTAL_HEIGHT;
    private int[] pixels;

    private Sprite[] loadedSprites = null;
    private int spriteSizeX;
    private boolean loaded = false;

    public SpriteSheet(BufferedImage sheetImage) {
        image = sheetImage;
        TOTAL_WIDTH = sheetImage.getWidth();
        TOTAL_HEIGHT = sheetImage.getHeight();

        pixels = new int[TOTAL_WIDTH * TOTAL_HEIGHT];
        pixels = sheetImage.getRGB(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT, pixels, 0, TOTAL_WIDTH);
    }

    public void loadSprites(int spriteSizeX, int spriteSizeY, int paddingWidth) {
        this.spriteSizeX = spriteSizeX;
        loadedSprites = new Sprite[(TOTAL_WIDTH / spriteSizeX) * (TOTAL_HEIGHT / spriteSizeY)];
        int spriteId = 0;

        for (int i = 0; i < TOTAL_HEIGHT; i += spriteSizeY + paddingWidth) {
            for (int j = 0; j < TOTAL_WIDTH; j += spriteSizeX + paddingWidth) {
                loadedSprites[spriteId] = new Sprite(this, j, i, spriteSizeX, spriteSizeY);
                spriteId++;
            }
        }
        System.out.println("Sprites loaded succesfully");
        loaded = true;
    }

    public Sprite getSprite(int x, int y) {
        if (loaded) {
            int spriteId = x + y * (TOTAL_WIDTH / spriteSizeX);
            if (spriteId < loadedSprites.length) {
                return loadedSprites[spriteId];
            } else {
                System.out.println("wanted sprite id is out of bounds");
            }
        } else {
            System.out.println("Sprites not loaded");
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
