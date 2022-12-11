package base.graphicsservice;

import base.map.MapTile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import static base.constants.ColorConstant.ALPHA;
import static base.constants.Constants.*;

public class Rectangle implements Serializable {

    private static final long serialVersionUID = 1L;

    private int x;
    private int y;
    private int width;
    private int height;
    private int[] pixels;

    protected static final Logger logger = LoggerFactory.getLogger(Rectangle.class);

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle() {
        this(0, 0, 0, 0);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int[] getPixels() {
        if (pixels != null) {
            return pixels;
        }
        logger.error("Attempt of getting pixels without generating graphics first");
        return new int[0];
    }

    public void generateBorder(int borderWidth, int borderColor) {
        generateBorder(borderWidth, borderColor, ALPHA);
    }

    public void generateBorder(int borderWidth, int borderColor, int backGroundColor) {
        pixels = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i < borderWidth || i >= height - borderWidth || j < borderWidth || j >= width - borderWidth) {
                    pixels[i * width + j] = borderColor;
                } else {
                    pixels[i * width + j] = backGroundColor;
                }
            }
        }
    }

    public void generateBackground(int color) {
        pixels = new int[width * height];
        Arrays.fill(pixels, color);
    }

    public boolean intersects(Rectangle otherRectangle) {
        if (otherRectangle == null) {
            return false;
        }
        return (intersectsByX(otherRectangle) && intersectsByY(otherRectangle));
    }

    public boolean intersects(int otherX, int otherY, int otherWidth, int otherHeight) {
        boolean intersectsByX = !(x > otherX + otherWidth || otherX > x + (width * ZOOM));
        boolean intersectsByY = !(y > otherY + otherHeight || otherY > y + (height * ZOOM));
        return intersectsByX && intersectsByY;
    }

    public boolean intersects(MapTile tile) {
        return (intersectsByX(tile) && intersectsByY(tile));
    }

    private boolean intersectsByY(Rectangle otherRectangle) {
        return !(y > otherRectangle.getY() + otherRectangle.getHeight() || otherRectangle.getY() > y + height);
    }

    private boolean intersectsByX(Rectangle otherRectangle) {
        return !(x > otherRectangle.getX() + otherRectangle.getWidth() || otherRectangle.getX() > x + width);
    }

    private boolean intersectsByY(MapTile tile) {
        return !(y > tile.getY() * CELL_SIZE + TILE_SIZE || tile.getY() * CELL_SIZE >= y + height);
    }

    private boolean intersectsByX(MapTile tile) {
        return !(x > tile.getX() * CELL_SIZE + CELL_SIZE || tile.getX() * CELL_SIZE >= x + width);
    }

    public boolean potentialIntersects(MapTile tile, int xPos, int yPos) {
        return (potentialIntersectsByX(tile, xPos) && potentialIntersectsByY(tile, yPos));
    }

    private boolean potentialIntersectsByY(MapTile tile, int yPos) {
        return !(yPos > tile.getY() * CELL_SIZE + TILE_SIZE || tile.getY() * CELL_SIZE > yPos + (CELL_SIZE - (TILE_SIZE / 2)));     //48 = 32 + 32/2
    }

    private boolean potentialIntersectsByX(MapTile tile, int xPos) {
        return !(xPos > tile.getX() * CELL_SIZE + CELL_SIZE || tile.getX() * CELL_SIZE > xPos + width);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rectangle rectangle = (Rectangle) o;
        return x == rectangle.x && y == rectangle.y && width == rectangle.width && height == rectangle.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }
}
