package base.navigationservice;

import java.util.Arrays;
import java.util.List;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.Constants.ZOOM;
import static base.constants.MapConstants.*;

public class NavigationService {

    public static String getNextPortalToGetToCenter(String mapName) {
        if (TOP_LEFT_MAP.equalsIgnoreCase(mapName)) {
            return HOME_MAP;
        }
        return MAIN_MAP;
    }

    public static List<String> getNearestMaps(String currentMap) {
        if (TOP_LEFT_MAP.equalsIgnoreCase(currentMap)) {
            return Arrays.asList(HOME_MAP, MAIN_MAP);
        }
        else if (MAIN_MAP.equalsIgnoreCase(currentMap)) {
            return Arrays.asList(HOME_MAP, TOP_LEFT_MAP);
        }
        return Arrays.asList(MAIN_MAP, TOP_LEFT_MAP);
    }

    public static int getPixelsToAdjustPosition(Direction direction, int x, int y) {
        switch (direction) {
            case DOWN:
                if (y % (TILE_SIZE * ZOOM) == 0) {
                    return 64;
                }
                return (TILE_SIZE * ZOOM) - (y % (TILE_SIZE * ZOOM));
            case UP:
                if (y % (TILE_SIZE * ZOOM) == 0) {
                    return 64;
                }
                return y % (TILE_SIZE * ZOOM);
            case RIGHT:
                if (x % (TILE_SIZE * ZOOM) == 0) {
                    return 64;
                }
                return (TILE_SIZE * ZOOM) - (x % (TILE_SIZE * ZOOM));
            case LEFT:
                if (x % (TILE_SIZE * ZOOM) == 0) {
                    return 64;
                }
                return x % (TILE_SIZE * ZOOM);
            default:
                return (TILE_SIZE * ZOOM);
        }
    }
}
