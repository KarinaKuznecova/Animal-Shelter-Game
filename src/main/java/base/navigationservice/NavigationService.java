package base.navigationservice;

import base.constants.MapConstants;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.Constants.ZOOM;
import static base.constants.MapConstants.*;

public class NavigationService {

    public static String getNextPortalToGetToCenter(String mapName) {
        if (MapConstants.MAPS_NEAR_MAIN_MAP.contains(mapName) || mapName == null) {
            return MapConstants.MAIN_MAP;
        }
        if (mapName.startsWith("Bottom")) {
            return BOTTOM_CENTER_MAP;
        }
        if (mapName.startsWith("TOP")) {
            return MapConstants.TOP_CENTER_MAP;
        }
        return mapName;
    }

    public static String getNextPortalToGetToHome(String mapName) {
        if (mapName == null) {
            return null;
        }
        if (MapConstants.BOTTOM_LEFT_MAP.equalsIgnoreCase(mapName) || MapConstants.BOTTOM_RIGHT_MAP.equalsIgnoreCase(mapName)) {
            return BOTTOM_CENTER_MAP;
        }
        if (BOTTOM_CENTER_MAP.equalsIgnoreCase(mapName) || MapConstants.SECOND_MAP.equalsIgnoreCase(mapName) || MapConstants.WATER_MAP.equalsIgnoreCase(mapName)) {
            return MapConstants.MAIN_MAP;
        }
        return MapConstants.TOP_CENTER_MAP;
    }

    public static String getNextPortalTo(String currentMap, String destination) {
        if (currentMap.equals(destination)) {
            return null;
        }

        if (MapConstants.BOTTOM_LEFT_MAP.equalsIgnoreCase(currentMap) || MapConstants.BOTTOM_RIGHT_MAP.equalsIgnoreCase(currentMap)) {
            return BOTTOM_CENTER_MAP;
        }
        if (BOTTOM_CENTER_MAP.equals(currentMap)) {
            if (BOTTOM_LEFT_MAP.equals(destination) || BOTTOM_RIGHT_MAP.equals(destination)) {
                return destination;
            } else {
                return MAIN_MAP;
            }
        }

        if (MapConstants.TOP_LEFT_MAP.equalsIgnoreCase(currentMap) || MapConstants.TOP_RIGHT_MAP.equalsIgnoreCase(currentMap)) {
            return MapConstants.TOP_CENTER_MAP;
        }
        if (TOP_CENTER_MAP.equals(currentMap)) {
            if (TOP_LEFT_MAP.equals(destination) || TOP_RIGHT_MAP.equals(destination)) {
                return destination;
            } else {
                return MAIN_MAP;
            }
        }

        if (MapConstants.SECOND_MAP.equalsIgnoreCase(currentMap) || MapConstants.WATER_MAP.equalsIgnoreCase(currentMap)) {
            return MapConstants.MAIN_MAP;
        }
        if (MAIN_MAP.equals(currentMap)) {
            if (MAPS_NEAR_MAIN_MAP.contains(destination)) {
                return destination;
            }
            if (destination.startsWith("TOP")) {
                return TOP_CENTER_MAP;
            }
            if (destination.startsWith("BOTTOM")) {
                return BOTTOM_CENTER_MAP;
            }
        }
        return destination;
    }

    public static String getNextPortalToOutside(String mapName) {
        if (MapConstants.TOP_CENTER_MAP.equalsIgnoreCase(mapName)) {
            return MapConstants.TOP_LEFT_MAP;
        }
        if (MapConstants.TOP_RIGHT_MAP.equalsIgnoreCase(mapName)) {
            return MapConstants.TOP_CENTER_MAP;
        }
        return mapName;
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
