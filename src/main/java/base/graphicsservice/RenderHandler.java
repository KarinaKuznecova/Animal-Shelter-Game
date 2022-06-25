package base.graphicsservice;

import base.Game;
import base.gameobjects.Animal;
import base.gameobjects.GameObject;
import base.gameobjects.Plant;
import base.gameobjects.Player;
import base.gui.EditIcon;
import base.map.GameMap;
import base.map.MapTile;
import base.map.bigobjects.Bookcase;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.List;
import java.util.*;

import static base.constants.ColorConstant.ALPHA;
import static base.constants.Constants.TILE_SIZE;
import static base.constants.Constants.ZOOM;
import static base.constants.MultiOptionalObjects.bookcases;

public class RenderHandler {

    private final BufferedImage view;
    private final int[] pixels;
    private final Rectangle camera;
    private int maxScreenWidth;
    private int maxScreenHeight;

    private final List<String> textToDrawInCenter;
    private int textCountdown;
    private Map<Position, String> textToDraw;

    protected static final Logger logger = LoggerFactory.getLogger(RenderHandler.class);

    public RenderHandler(int width, int height) {

        setSizeBasedOnScreenSize();

        //Create a BufferedImage that will represent our view.
        view = new BufferedImage(maxScreenWidth, maxScreenHeight, BufferedImage.TYPE_INT_RGB);

        camera = new Rectangle(0, 0, width, height);

        //Create an array for pixels
        pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();

        textToDrawInCenter = new ArrayList<>();
        textToDraw = new HashMap<>();
    }

    private void setSizeBasedOnScreenSize() {
        GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (GraphicsDevice device : graphicsDevices) {
            if (maxScreenWidth < device.getDisplayMode().getWidth()) {
                maxScreenWidth = device.getDisplayMode().getWidth();
            }
            if (maxScreenHeight < device.getDisplayMode().getHeight()) {
                maxScreenHeight = device.getDisplayMode().getHeight();
            }
        }
    }

    public void render(Game game, Graphics graphics) {

        if (game.getSelectedTileId() != -1 && game.getMousePosition() != null) {
            try {
                renderTilePreview(game);
            } catch (NullPointerException e) {
                // Sometimes there is NPE when mouse is outside the game window. Will catch it and ignore.
                logger.error("Exception with preview", e);
            }
        }

        graphics.drawImage(view.getSubimage(0, 0, camera.getWidth(), camera.getHeight()), 0, 0, camera.getWidth(), camera.getHeight(), null);

        if (!textToDrawInCenter.isEmpty() && textCountdown != 1) {
            renderText(graphics);
            if (textCountdown > 1) {
                textCountdown--;
            }
        }
        if (!textToDrawInCenter.isEmpty() && textCountdown == 1) {
            textCountdown = 0;
            removeText();
        }

        for (Map.Entry<Position, String> entry : textToDraw.entrySet()) {
            Position linePosition = entry.getKey();
            renderText(graphics, entry.getValue(), linePosition.getXPosition(), linePosition.getYPosition());
        }
    }

    private void renderTilePreview(Game game) {
        if (game.getMousePosition() == null) {
            return;
        }
        int xScreenRelated = (int) game.getMousePosition().getX() - 10;
        int yScreenRelated = (int) game.getMousePosition().getY() - 32;
        int xPositionActual = xScreenRelated + getCamera().getX();
        int yPositionActual = yScreenRelated + getCamera().getY();
        int xPosition = xScreenRelated - (xPositionActual % (TILE_SIZE * ZOOM));
        int yPosition = yScreenRelated - (yPositionActual % (TILE_SIZE * ZOOM));
        MapTile potentialTile = new MapTile(5, game.getSelectedTileId(), 0, 0, game.isRegularTiles());

        if (bookcases.contains(game.getSelectedTileId()) && game.isRegularTiles()) {
            Bookcase bookcase = new Bookcase(xPosition, yPosition, bookcases.indexOf(game.getSelectedTileId()), 64);
            for (MapTile mapTile : bookcase.getObjectParts()) {
                int xForCurrentPart = mapTile.getX();
                int yForCurrentPart = mapTile.getY();

                drawPreview(game, xForCurrentPart, yForCurrentPart, mapTile);
            }
        } else {
            drawPreview(game, xPosition, yPosition, potentialTile);
        }

    }

    private void drawPreview(Game game, int xPosition, int yPosition, MapTile potentialTile) {
        BufferedImage subimage = view.getSubimage(0, 0, camera.getWidth(), camera.getHeight());
        Sprite sprite;
        if (potentialTile.isRegularTile()) {
            sprite = game.getGameMap().getTileService().getTiles().get(potentialTile.getId()).getSprite();
        } else {
            sprite = game.getGameMap().getTileService().getTerrainTiles().get(potentialTile.getId()).getSprite();
        }

        BufferedImage tmpImage = new BufferedImage(TILE_SIZE * ZOOM, TILE_SIZE * ZOOM, BufferedImage.TYPE_INT_ARGB);

        Graphics tGraphics = subimage.createGraphics();
        int[] result = fillTransparentArray(sprite.getPixels(), sprite.getWidth(), sprite.getHeight(), ZOOM, ZOOM);
        tmpImage.setRGB(0, 0, tmpImage.getWidth(), tmpImage.getHeight(), result, 0, tmpImage.getWidth());
        tGraphics.drawImage(tmpImage, xPosition, yPosition, null);
        tGraphics.dispose();
    }

    public int[] fillTransparentArray(int[] spritePixels, int renderWidth, int renderHeight, int xZoom, int yZoom) {
        int pixel = 0;
        int[] result = new int[(renderWidth * xZoom) * (renderHeight * yZoom)];
        for (int y = 0; y < renderHeight; y++) {            //every row
            for (int x = 0; x < renderWidth; x++) {         // every pixel of row
                for (int yZ = 0; yZ < yZoom; yZ++) {            // repeat for y zoom
                    for (int xZ = 0; xZ < xZoom; xZ++) {           // repeat for x zoom

                        int xPos = (x * xZoom + xZ);
                        int yPos = (y * yZoom + yZ);

                        int pixelIndex = xPos + yPos * renderWidth * xZoom;

                        int transparency = 70;
                        if (isAlphaColor(spritePixels[pixel])) {
                            transparency = 0;
                        }
                        Color c = new Color(spritePixels[pixel]);
                        int r = c.getRed();
                        int g = c.getGreen();
                        int b = c.getBlue();
                        c = new Color(r, g, b, transparency);
                        result[pixelIndex] = c.getRGB();
                    }
                }
                pixel++;
            }
        }
        return result;
    }

    public void renderMap(Game game, GameMap gameMap) {
        renderBackground(gameMap);
        for (int i = 0; i <= 5; i++) {
            List<MapTile> tiles = gameMap.getLayeredTiles().get(i);
            if (tiles != null) {
                // with for-each loop there is ConcurrentModificationException often, but with this loop everything works fine
                for (int j = 0; j < tiles.size(); j++) {
                    MapTile mappedTile = tiles.get(j);
                    if (mappedTile.getLayer() == i && isInRangeOfCamera(mappedTile)) {
                        renderTile(gameMap, mappedTile);
                    }
                }
            }
            renderGameObjects(game, gameMap, i);
        }
    }

    private boolean isInRangeOfCamera(MapTile mappedTile) {
        return mappedTile.getX() * (TILE_SIZE * ZOOM) > camera.getX() - (TILE_SIZE * ZOOM)
                && mappedTile.getX() * (TILE_SIZE * ZOOM) < camera.getX() + camera.getWidth() + (TILE_SIZE * ZOOM)
                && mappedTile.getY() * (TILE_SIZE * ZOOM) > camera.getY() - (TILE_SIZE * ZOOM)
                && mappedTile.getY() * (TILE_SIZE * ZOOM) < camera.getY() + camera.getHeight() + (TILE_SIZE * ZOOM);
    }

    private void renderBackground(GameMap gameMap) {
        int backGroundTileId = gameMap.getBackGroundTileId();
        if (backGroundTileId >= 0) {
            for (int i = (camera.getX() / (TILE_SIZE * ZOOM)) * (TILE_SIZE * ZOOM) - 1;
                 i < (camera.getX() / (TILE_SIZE * ZOOM)) * (TILE_SIZE * ZOOM) + camera.getWidth() + (TILE_SIZE * ZOOM);
                 i += TILE_SIZE * ZOOM) {
                for (int j = (camera.getY() / (TILE_SIZE * ZOOM)) * (TILE_SIZE * ZOOM) - 1;
                     j < (camera.getY() / (TILE_SIZE * ZOOM)) * (TILE_SIZE * ZOOM) + camera.getHeight() + (TILE_SIZE * ZOOM);
                     j += TILE_SIZE * ZOOM) {
                    renderSprite(gameMap.getTileService().getTerrainTiles().get(backGroundTileId).getSprite(), i, j, ZOOM, false);
                }
            }
        }
    }

    private void renderTile(GameMap gameMap, MapTile mappedTile) {
        int xPosition = mappedTile.getX() * TILE_SIZE * ZOOM;
        int yPosition = mappedTile.getY() * TILE_SIZE * ZOOM;
        if (xPosition <= gameMap.getMapWidth() * TILE_SIZE * ZOOM && yPosition <= gameMap.getMapHeight() * TILE_SIZE * ZOOM) {
            if (mappedTile.isRegularTile()) {
                renderSprite(gameMap.getTileService().getTiles().get(mappedTile.getId()).getSprite(), xPosition, yPosition, ZOOM, false);
            } else {
                renderSprite(gameMap.getTileService().getTerrainTiles().get(mappedTile.getId()).getSprite(), xPosition, yPosition, ZOOM, false);
            }
        }
    }

    private void renderGameObjects(Game game, GameMap gameMap, int layer) {
        if (game.getPlayer().getLayer() == layer) {
            game.getPlayer().render(this, ZOOM);
        }
        List<Animal> animalsOnCurrentMap = game.getAnimalsOnMaps().get(gameMap.getMapName());
        for (Animal animal : animalsOnCurrentMap) {
            if (animal.getLayer() == layer) {
                animal.render(this, ZOOM);
            }
        }
        for (GameObject gameObject : gameMap.getItems()) {
            if (gameObject.getLayer() == layer) {
                gameObject.render(this, ZOOM);
            }
        }
        for (GameObject gameObject : gameMap.getInteractiveObjects()) {
            if (gameObject.getLayer() == layer) {
                gameObject.render(this, ZOOM);
            }
        }
        for (Plant plant : gameMap.getPlants()) {
            if (plant.getLayer() == layer) {
                plant.render(this, ZOOM);
            }
        }
    }

    private void renderText(Graphics graphics) {
        int xOffset = maxScreenWidth / 4;
        int yOffset = 20;
        for (int i = 0; i < textToDrawInCenter.size(); i++) {
            renderText(graphics, textToDrawInCenter.get(i), xOffset, 200 + (yOffset * i));
        }
    }

    private void renderText(Graphics graphics, String line, int x, int y) {
        graphics.setColor(Color.black);
        graphics.setFont(new Font("SansSerif", Font.PLAIN, 20));
        graphics.drawString(line, x, y);
    }

    public void renderRectangle(Rectangle rectangle, int xZoom, boolean fixed) {
        int[] rectanglePixels = rectangle.getPixels();
        if (rectanglePixels != null) {
            renderPixelsArrays(rectanglePixels, rectangle.getWidth(), rectangle.getHeight(), rectangle.getX(), rectangle.getY(), xZoom, fixed);
        }
    }

    public void renderRectangle(Rectangle rectangle, Rectangle rectangleOffset, int zoom, boolean fixed) {
        int[] rectanglePixels = rectangle.getPixels();
        if (rectanglePixels != null) {
            renderPixelsArrays(rectanglePixels, rectangle.getWidth(), rectangle.getHeight(), rectangle.getX() + rectangleOffset.getX(), rectangle.getY() + rectangleOffset.getY(), zoom, fixed);
        }
    }

    public void renderCircle(Circle circle) {
        int x = (int) (circle.getCenterX() - circle.getRadius() - getCamera().getX());
        int y = (int) (circle.getCenterY() - circle.getRadius() - getCamera().getY());
        int radius = (int) circle.getRadius();
        Graphics graphics = view.getGraphics();
        graphics.drawOval(x, y, radius * 2, radius * 2);
    }

    public void renderSprite(Sprite sprite, int xPosition, int yPosition, int zoom, boolean fixed) {
        renderPixelsArrays(sprite.getPixels(), sprite.getWidth(), sprite.getHeight(), xPosition, yPosition, zoom, fixed);
    }

    public void renderSprite(Sprite sprite, int xPosition, int yPosition, int zoom, boolean fixed, String line) {
        renderPixelsArrays(sprite.getPixels(), sprite.getWidth(), sprite.getHeight(), xPosition, yPosition, zoom, fixed);
        Position position = new Position(xPosition + (sprite.getWidth() * zoom - 62), yPosition + (sprite.getHeight() * zoom - 6));
        if (line != null) {
            textToDraw.put(position, line);
        }
    }

    public void renderSprite(Sprite sprite, int xPosition, int yPosition, int zoom, boolean fixed, Integer count) {
        renderPixelsArrays(sprite.getPixels(), sprite.getWidth(), sprite.getHeight(), xPosition, yPosition, zoom, fixed);
        Position numberPosition = new Position(xPosition + (sprite.getWidth() * zoom - 25), yPosition + (sprite.getHeight() * zoom - 5));
        if (count != null && count != 0) {
            renderNumber(count, numberPosition);
        } else {
            textToDraw.remove(numberPosition);
        }
    }

    public void renderNumber(int number, Position numberPosition) {
        textToDraw.put(numberPosition, String.valueOf(number));
    }

    public void renderText(String text, Position textPosition) {
        textToDraw.put(textPosition, text);
    }

    public void renderCustomizableText(String text, String customizable, Position textPosition, EditIcon editIcon) {
        textToDraw.put(textPosition, text);

        Position customTextPosition = new Position(textPosition.getXPosition() + text.length() * 10, textPosition.getYPosition());
        textToDraw.put(customTextPosition, customizable);

        Position editIconPosition = new Position(textPosition.getXPosition() - 20, textPosition.getYPosition() - 15);
        editIcon.changePosition(editIconPosition);
    }

    public void clearRenderedText() {
        textToDraw.clear();
    }

    public void renderPixelsArrays(int[] renderPixels, int renderWidth, int renderHeight, int xPosition, int yPosition, int zoom, boolean fixed) {
        for (int y = 0; y < renderHeight; y++) {
            for (int x = 0; x < renderWidth; x++) {
                for (int yZ = 0; yZ < zoom; yZ++) {
                    for (int xZ = 0; xZ < zoom; xZ++) {
                        int pixel = renderPixels[renderWidth * y + x];
                        int xPos = (x * zoom + xZ + xPosition);
                        int yPos = (y * zoom + yZ + yPosition);
                        setPixel(pixel, xPos, yPos, fixed);
                    }
                }
            }
        }
    }

    public void setPixel(int pixel, int x, int y, boolean fixed) {
        int pixelIndex = 0;
        if (!fixed && isInRangeOfCamera(x, y)) {
            pixelIndex = (x - camera.getX()) + (y - camera.getY()) * view.getWidth();
        }
        if (fixed && x >= 0 && y >= 0 && x <= camera.getWidth() && y <= camera.getHeight()) {
            pixelIndex = x + y * view.getWidth();
        }
        if (isInGlobalRange(pixelIndex) && !isAlphaColor(pixel)) {
            pixels[pixelIndex] = pixel;
        }
    }

    private boolean isAlphaColor(int pixel) {
        return pixel == ALPHA;
    }

    private boolean isInGlobalRange(int pixelIndex) {
        return pixelIndex < pixels.length;
    }

    private boolean isInRangeOfCamera(int x, int y) {
        return x >= camera.getX() &&
                y >= camera.getY() &&
                x <= camera.getX() + camera.getWidth() &&
                y <= camera.getY() + camera.getHeight();
    }

    public void clear() {
        Arrays.fill(pixels, 0);
    }

    public int getMaxWidth() {
        return maxScreenWidth;
    }

    public int getMaxHeight() {
        return maxScreenHeight;
    }

    public void adjustCamera(Game game, Player player) {
        logger.info("Adjusting camera");
        Rectangle playerRect = player.getPlayerRectangle();

        logger.info("Adjusting X");
        int mapEnd = game.getGameMap().getMapWidth() * (TILE_SIZE * ZOOM);
        int diffToEnd = mapEnd - playerRect.getX();
        if (diffToEnd < 96) {
            logger.info("Adjustment will be on the right side");
            camera.setX(mapEnd + (TILE_SIZE * ZOOM) - game.getWidth());
        }

        if (playerRect.getX() < (TILE_SIZE * ZOOM) + TILE_SIZE) {
            logger.info("Adjustment will be on the left side");
            camera.setX(-(TILE_SIZE * ZOOM));
        }

        logger.info("Adjusting Y");
        mapEnd = game.getGameMap().getMapHeight() * (TILE_SIZE * ZOOM);
        diffToEnd = mapEnd - playerRect.getY();

        if (diffToEnd < (TILE_SIZE * ZOOM) + TILE_SIZE) {
            logger.info("Adjustment will be on the bottom side");
            camera.setY(mapEnd + (TILE_SIZE * ZOOM) + TILE_SIZE - game.getHeight());
        }

        if (playerRect.getY() < 96) {
            logger.info("Adjustment will be on the top side");
            camera.setY(-(TILE_SIZE * ZOOM));
        }
    }

    public void setTextToDrawInCenter(List<String> textToDrawInCenter) {
        logger.debug(String.format("adding %d lines", textToDrawInCenter.size()));
        removeText();
        this.textToDrawInCenter.addAll(textToDrawInCenter);
    }

    public void setTextToDraw(String line, int timer) {
        textCountdown = timer;
        setTextToDraw(line);
    }

    private void setTextToDraw(String line) {
        logger.debug(String.format("adding <%s> line", line));
        removeText();
        textToDrawInCenter.add(line);
    }

    public void removeText() {
        textToDrawInCenter.clear();
    }

    public List<String> getTextToDrawInCenter() {
        return textToDrawInCenter;
    }

    public Rectangle getCamera() {
        return camera;
    }
}
