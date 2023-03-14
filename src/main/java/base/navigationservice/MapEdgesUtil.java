package base.navigationservice;

import static base.constants.Constants.CELL_SIZE;

public class MapEdgesUtil {

    private MapEdgesUtil() {
    }

    public static int getNorthEdgeStrict() {
      return 0;
    }

    public static int getNorthEdgePlusTile() {
        return getNorthEdgeStrict() - CELL_SIZE;
    }

    public static int getWestEdgeStrict() {
        return 0;
    }

    public static int getWestEdgePlusTile() {
        return getWestEdgeStrict() - CELL_SIZE;
    }

    public static int getEastEdgeStrict(int mapWidth) {
        return mapWidth * CELL_SIZE;
    }

    public static int getEastEdgeMinus(int mapWidth, int subtractAmount) {
        return getEastEdgeStrict(mapWidth) + CELL_SIZE - subtractAmount;
    }

    public static int getEastEdgePlusTile(int mapWidth) {
        return getEastEdgeStrict(mapWidth) + CELL_SIZE;
    }

    public static int getSouthEdgeStrict(int mapHeight) {
        return mapHeight * CELL_SIZE;
    }

    public static int getSouthEdgeMinus(int mapHeight, int subtractAmount) {
        return getSouthEdgeStrict(mapHeight) + CELL_SIZE - subtractAmount;
    }

    public static int getSouthEdgePlusTile(int mapHeight) {
        return getSouthEdgeStrict(mapHeight) + CELL_SIZE;
    }
}
