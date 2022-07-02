package base.navigationservice;

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
