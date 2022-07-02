package base.map;

import base.graphicsservice.ImageLoader;
import base.graphicsservice.SpriteSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.FilePath.*;

public class TileService {

    private List<Tile> tileList;
    private List<Tile> terrainTiles;

    private static final Logger logger = LoggerFactory.getLogger(TileService.class);

    public TileService() {
        logger.info("Loading regular tiles");
        SpriteSheet spriteSheet = loadSpriteSheets(SPRITES_PATH);
        tileList = getTilesFromFile(TILE_LIST_PATH, spriteSheet);
        logger.info(String.format("Loaded %d tiles", tileList.size()));
        logger.info("Loading technical terrain tiles");
        SpriteSheet terrainSpriteSheet = loadSpriteSheets(TERRAIN_SPRITES_PATH);
        terrainTiles = getTilesFromFile(TERRAIN_TILE_LIST_PATH, terrainSpriteSheet);
    }

    private SpriteSheet loadSpriteSheets(String path) {
        logger.info("Sprite sheet loading started");

        BufferedImage bufferedImage = ImageLoader.loadImage(path);
        if (bufferedImage == null) {
            logger.error(String.format("Buffered image is null, sprite path: %s", path));
            throw new IllegalArgumentException();
        }
        SpriteSheet spriteSheet = new SpriteSheet(bufferedImage);
        spriteSheet.loadSprites(TILE_SIZE, TILE_SIZE, 0);

        logger.info("Sprite sheet loading done");
        return spriteSheet;
    }

    public List<Tile> getTilesFromFile(String tilesFile, SpriteSheet spriteSheet) {
        List<Tile> tiles = new ArrayList<>();
        File file = new File(tilesFile);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.startsWith("//")) {
                    String[] splitLine = line.split("-");
                    String tileName = splitLine[0];
                    int spriteXPosition = Integer.parseInt(splitLine[1]);
                    int spriteYPosition = Integer.parseInt(splitLine[2]);
                    int layer = Integer.parseInt(splitLine[3]);
                    Tile tile = new Tile(tileName, spriteSheet.getSprite(spriteXPosition, spriteYPosition), layer);
                    if (splitLine.length > 4) {
                        tile.setVisible(false);
                    }
                    tiles.add(tile);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tiles;
    }

    public List<Tile> getTiles() {
        return tileList;
    }

    public List<Tile> getTerrainTiles() {
        return terrainTiles;
    }

    public int getLayerById(int id, boolean regularTile) {
        if (id == -1) {
            return -1;
        }
        if (regularTile) {
            return tileList.get(id).getLayer();
        }
        return terrainTiles.get(id).getLayer();
    }
}
