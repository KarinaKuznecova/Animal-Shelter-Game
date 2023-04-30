package base.graphicsservice;

import base.Game;
import base.gameobjects.*;
import base.gameobjects.npc.NpcSpawnSpot;
import base.gameobjects.npc.NpcSpot;
import base.gameobjects.player.Player;
import base.gameobjects.storage.StorageChest;
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

import static base.constants.ColorConstant.*;
import static base.constants.Constants.*;
import static base.constants.MultiOptionalObjects.bookcases;

public class RenderHandler {

    private final BufferedImage view;
    private final int[] pixels;
    private final Rectangle camera;
    private int maxScreenWidth;
    private int maxScreenHeight;

    private final List<String> textToDrawInCenter;
    private int textCountdown;
    private Map<Position, String> textToDrawFixed;
    private Map<Position, String> textToDrawNotFixed;

    protected static final Logger logger = LoggerFactory.getLogger(RenderHandler.class);

    public RenderHandler(int width, int height) {

        setSizeBasedOnScreenSize();

        //Create a BufferedImage that will represent our view.
        view = new BufferedImage(maxScreenWidth, maxScreenHeight, BufferedImage.TYPE_INT_RGB);

        camera = new Rectangle(0, 0, width, height);

        //Create an array for pixels
        pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();

        textToDrawInCenter = new ArrayList<>();
        textToDrawFixed = new HashMap<>();
        textToDrawNotFixed = new HashMap<>();
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

        drawPreview(game);

        graphics.drawImage(view.getSubimage(0, 0, camera.getWidth(), camera.getHeight()), 0, 0, camera.getWidth(), camera.getHeight(), null);

        drawTemporaryText(graphics);

        drawFixedTexts(graphics);
        drawNonFixedTexts(graphics);
    }

    private void drawNonFixedTexts(Graphics graphics) {
        for (Map.Entry<Position, String> entry : textToDrawNotFixed.entrySet()) {
            Position linePosition = entry.getKey();
            int xPosition = linePosition.getXPosition() - camera.getX();
            int yPosition = linePosition.getYPosition() - camera.getY();
            renderText(graphics, entry.getValue(), xPosition, yPosition, Color.BLACK, 20);
        }
    }

    private void drawFixedTexts(Graphics graphics) {
        for (Map.Entry<Position, String> entry : textToDrawFixed.entrySet()) {
            Position linePosition = entry.getKey();
            renderText(graphics, entry.getValue(), linePosition.getXPosition(), linePosition.getYPosition(), Color.BLACK, 20);
        }
    }

    private void drawTemporaryText(Graphics graphics) {
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
    }

    private void drawPreview(Game game) {
        if (game.getSelectedTileId() != -1 && game.getMousePosition() != null) {
            try {
                renderTilePreview(game);
            } catch (Exception e) {
                // Sometimes there is NPE or other exception when mouse is outside the game window. Will catch it and ignore so game continues.
                logger.error("Exception with preview", e);
            }
        } else {
            String itemNameByButtonId = game.getItemNameByButtonId();
            if (itemNameByButtonId != null && !itemNameByButtonId.isEmpty() && !itemNameByButtonId.startsWith("null") && game.getMousePosition() != null) {
                try {
                    renderItemPreview(game);
                } catch (Exception e) {
                    // Sometimes there is NPE or other exception when mouse is outside the game window. Will catch it and ignore so game continues.
                    logger.error("Exception with preview", e);
                }
            }
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
        int xPosition = xScreenRelated - (xPositionActual % CELL_SIZE);
        int yPosition = yScreenRelated - (yPositionActual % CELL_SIZE);
        MapTile potentialTile = new MapTile(5, game.getSelectedTileId(), 0, 0, game.isRegularTiles());

        if (bookcases.contains(game.getSelectedTileId()) && game.isRegularTiles()) {
            Bookcase bookcase = new Bookcase(xPosition, yPosition, bookcases.indexOf(game.getSelectedTileId()), 64);
            for (MapTile mapTile : bookcase.getObjectParts()) {
                int xForCurrentPart = mapTile.getX();
                int yForCurrentPart = mapTile.getY();

                drawPreview(xForCurrentPart, yForCurrentPart, getTileSpriteForPreview(game, mapTile));
            }
        } else {
            drawPreview(xPosition, yPosition, getTileSpriteForPreview(game, potentialTile));
        }
    }

    private void renderItemPreview(Game game) {
        if (game.getMousePosition() == null || game.getSelectedItem().isEmpty()) {
            return;
        }
        int xScreenRelated = (int) game.getMousePosition().getX() - 10;
        int yScreenRelated = (int) game.getMousePosition().getY() - 32;
        int xPositionActual = xScreenRelated + getCamera().getX();
        int yPositionActual = yScreenRelated + getCamera().getY();
        int xPosition = xScreenRelated - (xPositionActual % CELL_SIZE);
        int yPosition = yScreenRelated - (yPositionActual % CELL_SIZE);
        Item itemToDraw = new Item(0, 0, game.getSelectedItem(), game.getSpriteService().getItemSprite(game.getItemNameByButtonId(), game.getTileService()));

        if (itemToDraw.getSprite() != null) {
            drawPreview(xPosition, yPosition, itemToDraw.getSprite());
        }

    }

    private Sprite getTileSpriteForPreview(Game game, MapTile potentialTile) {
        Sprite sprite;
        if (potentialTile.isRegularTile()) {
            sprite = game.getTileService().getTiles().get(potentialTile.getId()).getSprite();
        } else {
            sprite = game.getTileService().getTerrainTiles().get(potentialTile.getId()).getSprite();
        }
        return sprite;
    }

    private void drawPreview(int xPosition, int yPosition, Sprite sprite) {
        BufferedImage subimage = view.getSubimage(0, 0, camera.getWidth(), camera.getHeight());

        BufferedImage tmpImage = new BufferedImage(sprite.getWidth() * ZOOM, sprite.getHeight() * ZOOM, BufferedImage.TYPE_INT_ARGB);

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
        renderBackground(game, gameMap);
        for (int i = 0; i <= 5; i++) {
            List<MapTile> tiles = gameMap.getLayeredTiles().get(i);
            if (tiles != null) {
                // with for-each loop there is ConcurrentModificationException often, but with this loop everything works fine
                for (int j = 0; j < tiles.size(); j++) {
                    MapTile mappedTile = tiles.get(j);
                    if (mappedTile.getLayer() == i && isInRangeOfCamera(mappedTile)) {
                        renderTile(game, gameMap, mappedTile);
                    }
                }
            }
            renderGameObjects(game, gameMap, i);
        }
    }

    private boolean isInRangeOfCamera(MapTile mappedTile) {
        return mappedTile.getX() * CELL_SIZE > camera.getX() - CELL_SIZE
                && mappedTile.getX() * CELL_SIZE < camera.getX() + camera.getWidth() + CELL_SIZE
                && mappedTile.getY() * CELL_SIZE > camera.getY() - CELL_SIZE
                && mappedTile.getY() * CELL_SIZE < camera.getY() + camera.getHeight() + CELL_SIZE;
    }

    private void renderBackground(Game game, GameMap gameMap) {
        int backGroundTileId = gameMap.getBackGroundTileId();
        if (backGroundTileId >= 0) {
            for (int i = (camera.getX() / CELL_SIZE) * CELL_SIZE - 64;
                 i < (camera.getX() / CELL_SIZE) * CELL_SIZE + camera.getWidth() + CELL_SIZE;
                 i += CELL_SIZE) {
                for (int j = (camera.getY() / CELL_SIZE) * CELL_SIZE - 64;
                     j < (camera.getY() / CELL_SIZE) * CELL_SIZE + camera.getHeight() + CELL_SIZE;
                     j += CELL_SIZE) {
                    renderSprite(game.getTileService().getTerrainTiles().get(backGroundTileId).getSprite(), i, j, ZOOM, false);
                }
            }
        }
    }

    private void renderTile(Game game, GameMap gameMap, MapTile mappedTile) {
        int xPosition = mappedTile.getX() * CELL_SIZE;
        int yPosition = mappedTile.getY() * CELL_SIZE;
        if (xPosition <= gameMap.getMapWidth() * CELL_SIZE && yPosition <= gameMap.getMapHeight() * CELL_SIZE) {
            if (mappedTile.isRegularTile()) {
                renderSprite(game.getTileService().getTiles().get(mappedTile.getId()).getSprite(), xPosition, yPosition, ZOOM, false);
            } else {
                renderSprite(game.getTileService().getTerrainTiles().get(mappedTile.getId()).getSprite(), xPosition, yPosition, ZOOM, false);
            }
        }
    }

    private void renderGameObjects(Game game, GameMap gameMap, int layer) {
        for (GameObject gameObject : gameMap.getGameMapObjects()) {
            if (gameObject != null && gameObject.getLayer() == layer) {
                gameObject.render(this, ZOOM);
            }
        }
        List<Animal> animalsOnCurrentMap = game.getAnimalsOnMaps().get(gameMap.getMapName());
        if (animalsOnCurrentMap != null) {
            for (Animal animal : animalsOnCurrentMap) {
                if (animal.getLayer() == layer) {
                    animal.render(this, ZOOM);
                }
            }
        }
        if (game.getPlayer().getLayer() == layer) {
            game.getPlayer().render(this, ZOOM);
        }
        if (game.getGameMap().getNpcs() != null && !game.getGameMap().getNpcs().isEmpty() && game.getGameMap().getNpcs().get(0).getLayer() == layer) {
            game.getGameMap().getNpcs().get(0).render(this, ZOOM);
        }
    }

    private void renderText(Graphics graphics) {
        int xOffset = maxScreenWidth / 3;
        int yOffset = 20;
        for (int i = 0; i < textToDrawInCenter.size(); i++) {
            Color color = new Color(0xB3FFFFFF, true);
            renderText(graphics, textToDrawInCenter.get(i), xOffset, maxScreenHeight / 3 + (yOffset * i), color, 24);
        }
    }

    private void renderText(Graphics graphics, String line, int x, int y, Color color, int fontSize) {
        graphics.setColor(color);
        if (fontSize > 20) {
            graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        } else {
            graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));
        }
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
            textToDrawFixed.put(position, line);
        }
    }

    public void renderSprite(Sprite sprite, int xPosition, int yPosition, int zoom, boolean fixed, Integer count) {
        renderPixelsArrays(sprite.getPixels(), sprite.getWidth(), sprite.getHeight(), xPosition, yPosition, zoom, fixed);
        Position numberPosition = new Position(xPosition + (sprite.getWidth() * zoom - 25), yPosition + (sprite.getHeight() * zoom - 5));
        if (fixed) {
            if (count != null && count != 0) {
                renderNumber(count, numberPosition);
            } else {
                textToDrawFixed.remove(numberPosition);
            }
        } else {
            if (count != null && count != 0) {
                textToDrawNotFixed.put(numberPosition, String.valueOf(count));
            } else {
                textToDrawNotFixed.remove(numberPosition);
            }
        }
    }

    public void renderStorageSprite(Sprite sprite, int xPosition, int yPosition, int zoom, boolean fixed, Integer count) {
        renderPixelsArrays(sprite.getPixels(), sprite.getWidth(), sprite.getHeight(), xPosition, yPosition, zoom, fixed);
        Position numberPosition = new Position(xPosition + (TILE_SIZE * zoom) - 25, yPosition + (TILE_SIZE * zoom) - 5);
        if (fixed) {
            if (count != null && count != 0) {
                renderNumber(count, numberPosition);
            } else {
                textToDrawFixed.remove(numberPosition);
            }
        } else {
            if (count != null && count != 0) {
                textToDrawNotFixed.put(numberPosition, String.valueOf(count));
            } else {
                textToDrawNotFixed.remove(numberPosition);
            }
        }
    }

    public void renderNumber(int number, Position numberPosition) {
        textToDrawFixed.put(numberPosition, String.valueOf(number));
    }

    public void renderText(String text, Position textPosition) {
        textToDrawFixed.put(textPosition, text);
    }

    public void renderCustomizableText(String text, Position textPosition, EditIcon editIcon) {
        textToDrawFixed.put(textPosition, text);

        Position editIconPosition = new Position(textPosition.getXPosition() - 20, textPosition.getYPosition() - 15);
        editIcon.changePosition(editIconPosition);
    }

    public void clearRenderedText() {
        textToDrawFixed.clear();
        textToDrawNotFixed.clear();
    }

    public void removeTextFromPosition(Position position) {
        textToDrawNotFixed.remove(position);
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
        Rectangle playerRect = player.getRectangle();

        logger.info("Adjusting X");
        int mapEnd = game.getGameMap().getMapWidth() * CELL_SIZE;
        int diffToEnd = mapEnd - playerRect.getX();
        if (diffToEnd < 96) {
            logger.info("Adjustment will be on the right side");
            camera.setX(mapEnd + CELL_SIZE - game.getWidth());
        }

        if (mapEnd < camera.getX() + camera.getWidth()) {
            logger.info("Adjustment will be on the right side");
            camera.setX(mapEnd + CELL_SIZE - game.getWidth());
        }

        if (playerRect.getX() < CELL_SIZE + TILE_SIZE) {
            logger.info("Adjustment will be on the left side");
            camera.setX(-CELL_SIZE);
        }

        logger.info("Adjusting Y");
        mapEnd = game.getGameMap().getMapHeight() * CELL_SIZE;
        diffToEnd = mapEnd - playerRect.getY();

        if (diffToEnd < CELL_SIZE + TILE_SIZE) {
            logger.info("Adjustment will be on the bottom side");
            camera.setY(mapEnd + CELL_SIZE + TILE_SIZE - game.getHeight());
        }

        if (playerRect.getY() < 96) {
            logger.info("Adjustment will be on the top side");
            camera.setY(-CELL_SIZE);
        }
    }

    public void setTextToDrawInCenter(List<String> textToDrawInCenter) {
        logger.debug(String.format("adding %d lines", textToDrawInCenter.size()));
        removeText();
        this.textToDrawInCenter.addAll(textToDrawInCenter);
    }

    public void setTextToDraw(String line, int timer) {
        textCountdown = timer;
        setTextToDrawFixed(line);
    }

    private void setTextToDrawFixed(String line) {
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
