package base.mapgenerator;

import base.gameobjects.Bush;
import base.gameobjects.GameObject;
import base.gameobjects.Portal;
import base.gameobjects.tree.Oak;
import base.gameobjects.tree.Spruce;
import base.gameobjects.tree.Tree;
import base.gameobjects.tree.TreeType;
import base.graphicsservice.Rectangle;
import base.map.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static base.constants.Constants.CELL_SIZE;
import static base.constants.FilePath.JSON_MAPS_DIRECTORY;
import static base.constants.MapConstants.FOREST_MAP;

public class ForestMapGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ForestMapGenerator.class);

    private final Random random = new Random();
    private final GameMapConverter gameMapConverter = new GameMapConverter();
    private final NoiseGenerator noiseGenerator;
    private final List<Tile> listOfTiles = new ArrayList<>();
    private final TileService tileService = new TileService();
    float maxNumber = -1;
    float minNumber = 1;

    public ForestMapGenerator() {
        int seed = random.nextInt();
        float octaves = random.nextFloat();
        float persistence = random.nextFloat();

        noiseGenerator = new NoiseGenerator(seed, 3);
    }

    public GameMap generateMap(int width, int height, String mapName) {
        logger.info("Generating forest map");

        float[][] noiseResult = noiseGenerator.getNoiseMap(height, height);

        defineMaxAndMin(width, height, noiseResult);
        fillListOfTiles();

        GameMap gameMap = new GameMap(mapName);
        gameMap.setMapWidth(width);
        gameMap.setMapHeight(height);
        gameMap.setBackGroundTileId(175);

        fillMapWithBackgroundTiles(noiseResult, gameMap);

        TreeType treeType = getTreeType(mapName);
        fillTrees(gameMap, treeType);

        fillMapWithBushes(gameMap, 3);

        sortGameObjects(gameMap);

        logger.info("Generating forest map done");
        return gameMap;
    }

    private TreeType getTreeType(String mapName) {
        TreeType treeType;
        if (mapName.contains("oak")) {
            treeType = TreeType.OAK;
        } else if (mapName.contains("spruce")) {
            treeType = TreeType.SPRUCE;
        } else {
            treeType = TreeType.MIX;
        }
        return treeType;
    }

    private void fillTrees(GameMap gameMap, TreeType treeType) {
        fillMapWithTopRowOfTrees(gameMap, treeType);
        fillMapWithBottomRowOfTrees(gameMap, treeType);
        fillMapWithLeftRowOfTrees(gameMap, treeType);
        fillMapWithRightRowOfTrees(gameMap, treeType);
        fillMapWithTrees(gameMap, calculateNumberOfTrees(gameMap), treeType);
    }

    private int calculateNumberOfTrees(GameMap gameMap) {
        int numberOfTrees = gameMap.getMapHeight() + gameMap.getMapWidth() + Math.max(gameMap.getMapHeight(), gameMap.getMapWidth());
        logger.info(String.format("There will be %s number of trees", numberOfTrees));
        return numberOfTrees;
    }

    private void defineMaxAndMin(int width, int height, float[][] noiseResult) {
        logger.info("Defining min and max for noise result");
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                float number = noiseResult[y][x];
                if (number > maxNumber) {
                    maxNumber = number;
                }
                if (number < minNumber) {
                    minNumber = number;
                }
            }
        }
    }

    public void fillListOfTiles() {
        logger.info("Filling list of tiles");
        Tile one3 = tileService.getTerrainTiles().get(162);
        one3.tileId = 162;
        listOfTiles.add(one3);
        Tile one2 = tileService.getTerrainTiles().get(173);
        one2.tileId = 173;
        listOfTiles.add(one2);
        listOfTiles.add(one2);
        Tile two2 = tileService.getTerrainTiles().get(174);
        two2.tileId = 174;
        listOfTiles.add(two2);
        listOfTiles.add(two2);
        Tile three2 = tileService.getTerrainTiles().get(175);
        three2.tileId = 175;
        listOfTiles.add(three2);
        listOfTiles.add(three2);
    }

    private void fillMapWithBackgroundTiles(float[][] noiseResult, GameMap gameMap) {
        int width = gameMap.getMapWidth();
        int height = gameMap.getMapHeight();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                float number = noiseResult[y][x];

                int tileId = getTileId(minNumber, maxNumber, number);

                Tile tile = listOfTiles.get(tileId);
                gameMap.setTile(x, y, tile.tileId, tile.getLayer(), false);
            }
        }
    }

    private int getTileId(float minNumber, float maxNumber, float number) {
        float step = (maxNumber - minNumber) / listOfTiles.size();

        float temp = number;
        int count = 0;
        while (temp >= minNumber) {
            temp -= step;
            count++;
        }
        if (count >= listOfTiles.size()) {
            count = listOfTiles.size() - 1;
        }
        return count;
    }

    private void fillMapWithTrees(GameMap gameMap, int numberOfTrees, TreeType treeType) {
        for (int i = 0; i < numberOfTrees; i++) {
            int xRandom = random.nextInt(gameMap.getMapWidth() - 2) + 3;
            int yRandom = random.nextInt(gameMap.getMapHeight() - 3);

            if (!gameMap.getGameObjectsNearPosition(xRandom * CELL_SIZE, yRandom * CELL_SIZE).isEmpty()) {
                logger.debug("Skipping tree");
                i--;
                continue;
            }

            putTree(gameMap, yRandom * CELL_SIZE, xRandom * CELL_SIZE, treeType);
        }
    }

    private void fillMapWithTopRowOfTrees(GameMap gameMap, TreeType treeType) {
        logger.info("filling top row of trees");
        for (int i = 0; i <= gameMap.getMapWidth() - 1; i += 2) {
            int xPosition = i * CELL_SIZE;
            int yPosition = (random.nextInt(2) - 2) * CELL_SIZE;

            if (treeType == TreeType.SPRUCE && (i < (gameMap.getMapWidth() / 2) + 3 && i > (gameMap.getMapWidth() / 2))) {
                logger.info("Adding portal on the top");
                int xPortal = xPosition + 32;
                int yPortal = -64;
                Rectangle portalRectangle = new Rectangle(xPortal, yPortal, CELL_SIZE, CELL_SIZE);
                gameMap.addObject(new Portal(portalRectangle, FOREST_MAP));
                addPathOnTop(gameMap, xPortal, yPortal);
                continue;
            }

            putTree(gameMap, yPosition, xPosition, treeType);
        }
    }

    private void addPathOnTop(GameMap gameMap, int xPosition, int yPosition) {
        logger.info("Adding path on top");
        int smallerX = xPosition / CELL_SIZE;
        int smallerY = yPosition / CELL_SIZE;
        gameMap.setTile(smallerX, smallerY, 28, 1, false);
        gameMap.setTile(smallerX, smallerY + 1, 28, 1, false);
        gameMap.setTile(smallerX, smallerY + 2, 28, 1, false);
        gameMap.setTile(smallerX, smallerY + 3, 31, 1, false);

        gameMap.setTile(smallerX + 1, smallerY, 30, 1, false);
        gameMap.setTile(smallerX + 1, smallerY + 1, 30, 1, false);
        gameMap.setTile(smallerX + 1, smallerY + 2, 30, 1, false);
        gameMap.setTile(smallerX + 1, smallerY + 3, 33, 1, false);
    }

    private void fillMapWithBottomRowOfTrees(GameMap gameMap, TreeType treeType) {
        logger.info("filling bottom row of trees");
        for (int i = 0; i <= gameMap.getMapWidth() - 1; i += 2) {
            int xPosition = i * CELL_SIZE;
            int yPosition = (gameMap.getMapHeight() * CELL_SIZE) - ((random.nextInt(2)) * CELL_SIZE) - CELL_SIZE;

            if (treeType == TreeType.OAK && (i < (gameMap.getMapWidth() / 2) + 3 && i > (gameMap.getMapWidth() / 2))) {
                logger.info("Adding portal on the bottom");
                int xPortal = xPosition + 32;
                int yPortal = gameMap.getMapHeight() * CELL_SIZE;
                Rectangle portalRectangle = new Rectangle(xPortal, yPortal, CELL_SIZE, CELL_SIZE);
                gameMap.addObject(new Portal(portalRectangle, FOREST_MAP));
                addPathOnBottom(gameMap, xPortal, yPortal);
                continue;
            }

            putTree(gameMap, yPosition, xPosition, treeType);
        }
    }

    private void addPathOnBottom(GameMap gameMap, int xPosition, int yPosition) {
        logger.info("Adding path on the bottom");
        int smallerX = xPosition / CELL_SIZE;
        int smallerY = yPosition / CELL_SIZE;
        gameMap.setTile(smallerX, smallerY, 28, 1, false);
        gameMap.setTile(smallerX, smallerY - 1, 28, 1, false);
        gameMap.setTile(smallerX, smallerY - 2, 28, 1, false);
        gameMap.setTile(smallerX, smallerY - 3, 25, 1, false);

        gameMap.setTile(smallerX + 1, smallerY, 30, 1, false);
        gameMap.setTile(smallerX + 1, smallerY - 1, 30, 1, false);
        gameMap.setTile(smallerX + 1, smallerY - 2, 30, 1, false);
        gameMap.setTile(smallerX + 1, smallerY - 3, 27, 1, false);
    }

    private void fillMapWithLeftRowOfTrees(GameMap gameMap, TreeType treeType) {
        logger.info("filling left row of trees");
        for (int i = 0; i <= gameMap.getMapHeight() - 1; i += 2) {
            int yPosition = i * CELL_SIZE;
            int xPosition = (random.nextInt(2) - 2) * CELL_SIZE;

            if (treeType == TreeType.MIX && (i < (gameMap.getMapHeight() / 2) + 2 && i > (gameMap.getMapHeight() / 2) - 1)) {
                logger.info("Adding portal on the left");
                int xPortal = -64;
                int yPortal = i * CELL_SIZE + 56;
                Rectangle portalRectangle = new Rectangle(xPortal, yPortal, CELL_SIZE, CELL_SIZE);
                gameMap.addObject(new Portal(portalRectangle, FOREST_MAP));
                addPathOnLeft(gameMap, xPortal, yPortal);
                continue;
            }

            putTree(gameMap, yPosition, xPosition, treeType);
        }
    }

    private void addPathOnLeft(GameMap gameMap, int xPosition, int yPosition) {
        logger.info("Adding path on the left");
        int smallerX = xPosition / CELL_SIZE;
        int smallerY = yPosition / CELL_SIZE;
        gameMap.setTile(smallerX, smallerY, 26, 1, false);
        gameMap.setTile(smallerX + 1, smallerY, 26, 1, false);
        gameMap.setTile(smallerX + 2, smallerY, 26, 1, false);
        gameMap.setTile(smallerX + 3, smallerY, 27, 1, false);

        gameMap.setTile(smallerX, smallerY + 1, 32, 1, false);
        gameMap.setTile(smallerX + 1, smallerY + 1, 32, 1, false);
        gameMap.setTile(smallerX + 2, smallerY + 1, 32, 1, false);
        gameMap.setTile(smallerX + 3, smallerY + 1, 33, 1, false);
    }

    private void fillMapWithRightRowOfTrees(GameMap gameMap, TreeType treeType) {
        logger.info("filling right row of trees");
        for (int i = 0; i <= gameMap.getMapHeight() - 1; i += 2) {
            int yPosition = i * CELL_SIZE;
            int xPosition = (gameMap.getMapWidth() * CELL_SIZE) - ((random.nextInt(2) - 2) * CELL_SIZE) - (CELL_SIZE * 2);

            putTree(gameMap, yPosition, xPosition, treeType);
        }
    }

    private void putTree(GameMap gameMap, int yPosition, int xPosition, TreeType treeType) {
        if (treeType == TreeType.MIX) {
            putTreeRandom(gameMap, yPosition, xPosition);
            return;
        }
        Tree tree;
        if (TreeType.OAK == treeType) {
            tree = new Oak(xPosition, yPosition);
        } else {
            tree = new Spruce(xPosition, yPosition);
        }
        gameMap.addObject((GameObject) tree);
    }

    private void putTreeRandom(GameMap gameMap, int yPosition, int xPosition) {
        boolean type = random.nextBoolean();
        Tree tree;
        if (type) {
            tree = new Oak(xPosition, yPosition);
        } else {
            tree = new Spruce(xPosition, yPosition);
        }
        gameMap.addObject((GameObject) tree);
    }

    private void fillMapWithBushes(GameMap gameMap, int numberOfBushes) {
        for (int i = 0; i < numberOfBushes; i++) {
            int xRandom = random.nextInt(gameMap.getMapWidth()) + 1;
            int yRandom = random.nextInt(gameMap.getMapHeight());

            if (!gameMap.getGameObjectsNearPosition(xRandom * CELL_SIZE, yRandom * CELL_SIZE).isEmpty()) {
                logger.debug("Skipping bush");
                i--;
                continue;
            }
            putBush(gameMap, yRandom * CELL_SIZE, xRandom * CELL_SIZE);
        }
    }


    private void putBush(GameMap gameMap, int yPosition, int xPosition) {
        Bush bush = new Bush(xPosition, yPosition, gameMap.getMapName());
        gameMap.addObject(bush);
    }

    private void sortGameObjects(GameMap gameMap) {
        logger.info("sorting game objects");
        gameMap.sortGameObjects();
    }

    public void saveMapToJson(GameMap gameMap) {
        Gson gson = new Gson();
        try {
            String newPath = JSON_MAPS_DIRECTORY;
            File directory = new File(newPath);
            if (!directory.exists() && !directory.mkdirs()) {
                return;
            }
            FileWriter writer = new FileWriter(newPath + gameMap.getMapName());
            GameMapDTO gameMapDTO = gameMapConverter.getGameMapDTO(gameMap);
            gson.toJson(gameMapDTO, writer);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
