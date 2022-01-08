package base.graphicsservice;

import base.map.MapTile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;

import static base.constants.ColorConstant.ALPHA;
import static base.constants.Constants.TILE_SIZE;
import static base.constants.Constants.ZOOM;

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

    public void generateGraphics(int borderWidth, int color) {
        pixels = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i < borderWidth || i >= height - borderWidth || j < borderWidth || j >= width - borderWidth) {
                    pixels[i * width + j] = color;
                } else {
                    pixels[i * width + j] = ALPHA;
                }
            }
        }
    }

    public boolean intersects(Rectangle otherRectangle) {
        return (intersectsByX(otherRectangle) && intersectsByY(otherRectangle));
    }

    public boolean intersects(int otherX, int otherY, int otherWidth, int otherHeight) {
        boolean intersectsByX = !(x > otherX + otherWidth || otherX > x + (width * ZOOM));
        boolean intersectsByY = !(y > otherY + otherHeight || otherY > y + (height * ZOOM));
        return intersectsByX && intersectsByY;
    }

    private boolean intersectsByY(Rectangle otherRectangle) {
        return !(y > otherRectangle.getY() + otherRectangle.getHeight() || otherRectangle.getY() > y + height);
    }

    private boolean intersectsByX(Rectangle otherRectangle) {
        return !(x > otherRectangle.getX() + otherRectangle.getWidth() || otherRectangle.getX() > x + width);
    }

    public boolean intersects(MapTile tile) {
        return (intersectsByX(tile) && intersectsByY(tile));
    }

    private boolean intersectsByY(MapTile tile) {
        return !(y > tile.getY() * (TILE_SIZE * ZOOM) + TILE_SIZE || tile.getY() * (TILE_SIZE * ZOOM) >= y + height);
    }

    private boolean intersectsByX(MapTile tile) {
        return !(x > tile.getX() * (TILE_SIZE * ZOOM) + TILE_SIZE || tile.getX() * (TILE_SIZE * ZOOM) >= x + width);
    }

    public boolean potentialIntersects(MapTile tile, int xPos, int yPos) {
        return (intersectsByX(tile, xPos) && intersectsByY(tile, yPos));
    }

    private boolean intersectsByY(MapTile tile, int yPos) {
        return !(yPos > tile.getY() * (TILE_SIZE * ZOOM) + TILE_SIZE || tile.getY() * (TILE_SIZE * ZOOM) > yPos + ((TILE_SIZE * ZOOM) - (TILE_SIZE / 2)));     //48 = 32 + 32/2
    }

    private boolean intersectsByX(MapTile tile, int xPos) {
        return !(xPos > tile.getX() * (TILE_SIZE * ZOOM) + TILE_SIZE || tile.getX() * (TILE_SIZE * ZOOM) > xPos + width);
    }

    public boolean potentialIntersects(Rectangle rectangle, int xPos, int yPos) {
        return (intersectsByX(rectangle, xPos) && intersectsByY(rectangle, yPos));
    }

    private boolean intersectsByY(Rectangle rectangle, int yPos) {
        return !(yPos > rectangle.getY() * (TILE_SIZE * ZOOM) + TILE_SIZE || rectangle.getY() * (TILE_SIZE * ZOOM) > yPos + ((TILE_SIZE * ZOOM) - (TILE_SIZE / 2)));     //48 = 32 + 32/2
    }

    private boolean intersectsByX(Rectangle rectangle, int xPos) {
        return !(xPos > rectangle.getX() * (TILE_SIZE * ZOOM) + TILE_SIZE || rectangle.getX() * (TILE_SIZE * ZOOM) > xPos + width);
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
