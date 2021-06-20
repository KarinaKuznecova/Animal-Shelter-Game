package base.map;

import base.graphicsService.RenderHandler;
import base.graphicsService.SpriteSheet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TileService {

    private List<Tile> tileList = new ArrayList<>();

    public TileService(File tilesFile, SpriteSheet spriteSheet) {
        try {
            Scanner scanner = new Scanner(tilesFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.startsWith("//")) {
                    String[] splitLine = line.split("-");
                    String tileName = splitLine[0];
                    int spriteXPosition = Integer.parseInt(splitLine[1]);
                    int spriteYPosition = Integer.parseInt(splitLine[2]);
                    int layer = Integer.parseInt(splitLine[3]);
                    Tile tile = new Tile(tileName, spriteSheet.getSprite(spriteXPosition, spriteYPosition), layer);
                    tileList.add(tile);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void renderTile(int tileIndex, RenderHandler renderer, int xPosition, int yPosition, int xZoom, int yZoom) {
        if (tileIndex >= 0 && tileList.size() > tileIndex) {
            renderer.renderSprite(tileList.get(tileIndex).getSprite(), xPosition, yPosition, xZoom, yZoom, false);
        } else {
            System.out.println("tileIndex: " + tileIndex + " is out of bounds");
        }
    }

    public List<Tile> getTiles() {
        return tileList;
    }

    public int getLayerById(int id) {
        return tileList.get(id).getLayer();
    }
}
