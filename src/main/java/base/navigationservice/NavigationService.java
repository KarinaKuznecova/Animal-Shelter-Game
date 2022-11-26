package base.navigationservice;

import java.util.Arrays;
import java.util.List;

import static base.constants.Constants.*;
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
                if (y % CELL_SIZE == 0) {
                    return 64;
                }
                return CELL_SIZE - (y % CELL_SIZE);
            case UP:
                if (y % CELL_SIZE == 0) {
                    return 64;
                }
                return y % CELL_SIZE;
            case RIGHT:
                if (x % CELL_SIZE == 0) {
                    return 64;
                }
                return CELL_SIZE - (x % CELL_SIZE);
            case LEFT:
                if (x % CELL_SIZE == 0) {
                    return 64;
                }
                return x % CELL_SIZE;
            default:
                return CELL_SIZE;
        }
    }
}
