package base.map;

import base.graphicsservice.ImageLoader;
import base.graphicsservice.SpriteSheet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.FilePath.*;

public class TileService {

    private List<Tile> tileList;
    private List<Tile> terrainTiles;

    private static final Logger logger = LoggerFactory.getLogger(TileService.class);

    public TileService() {
        loadRegularTiles();
        loadTerrainTiles();
    }

    private void loadRegularTiles() {
        logger.info("Loading regular tiles");
        SpriteSheet spriteSheet = loadSpriteSheets(SPRITES_PATH);
        tileList = loadTilesAsJson(TILE_LIST_PATH);
        addSpritesToTiles(spriteSheet);

        logger.info(String.format("Loaded %d tiles", tileList.size()));
    }

    private void loadTerrainTiles() {
        logger.info("Loading technical terrain tiles");
        SpriteSheet spriteSheet = loadSpriteSheets(TERRAIN_SPRITES_PATH);
        terrainTiles = loadTilesAsJson(TERRAIN_TILE_LIST_PATH);
        addSpritesToTerrainTiles(spriteSheet);
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

    public List<Tile> loadTilesAsJson(String fileName) {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(CONFIG_DIRECTORY + fileName)) {
            Type collectionType = new TypeToken<List<Tile>>() {}.getType();
            return gson.fromJson(reader, collectionType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void saveTilesAsJson() {
        Gson gson = new Gson();
        try {
            File directory = new File(CONFIG_DIRECTORY);
            if (!directory.exists() && !directory.mkdirs()) {
                logger.error("Error while saving storage to json file - cannot create directory");
            }
            FileWriter writer = new FileWriter(CONFIG_DIRECTORY + TILE_LIST_PATH);
            gson.toJson(tileList, writer);
            writer.flush();
            writer.close();

            FileWriter writer2 = new FileWriter(CONFIG_DIRECTORY + TERRAIN_TILE_LIST_PATH);
            gson.toJson(terrainTiles, writer2);
            writer2.flush();
            writer2.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void addSpritesToTiles(SpriteSheet spriteSheet) {
        for (Tile tile : tileList) {
            setTileSprite(spriteSheet, tile);
            setTileAttributes(tile);
        }
    }

    private void addSpritesToTerrainTiles(SpriteSheet spriteSheet) {
        for (Tile tile : terrainTiles) {
            setTileSprite(spriteSheet, tile);
            setTileAttributes(tile);
        }
    }

    private void setTileSprite(SpriteSheet spriteSheet, Tile tile) {
        tile.setSprite(spriteSheet.getSprite(tile.getSpriteXPosition(), tile.getSpriteYPosition()));
    }

    private void setTileAttributes(Tile tile) {
        tile.setAttributes(new ArrayList<>());
        if (tile.getTileName().contains("Grass")) {
            tile.addAttribute(TileType.GRASS);
        }
        if (tile.getTileName().contains("Wall")) {
            tile.addAttribute(TileType.WALL);
        }
    }
}
