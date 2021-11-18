package base.graphicsservice;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;

public class Sprite {

    private int width;
    private int height;
    private int[] pixels;

    public Sprite() {
    }

    public Sprite(SpriteSheet sheet, int startX, int startY, int width, int height) {
        this.width = width;
        this.height = height;

        pixels = new int[width * height];
        sheet.getImage().getRGB(startX, startY, width, height, pixels, 0, width);
    }

    public Sprite(BufferedImage image) {
        width = image.getWidth();
        height = image.getHeight();

        pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getPixels() {
        return pixels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sprite sprite = (Sprite) o;
        return width == sprite.width && height == sprite.height && Arrays.equals(pixels, sprite.pixels);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(width, height);
        result = 31 * result + Arrays.hashCode(pixels);
        return result;
    }
}
