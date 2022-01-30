package base.navigationservice;

import base.constants.MapConstants;

public class NavigationService {

    public static String getNextPortalToGetToCenter(String mapName) {
        if (MapConstants.MAPS_NEAR_MAIN_MAP.contains(mapName) || mapName == null) {
            return MapConstants.MAIN_MAP;
        }
        if (mapName.startsWith("Bottom")) {
            return MapConstants.BOTTOM_CENTER_MAP;
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
            return MapConstants.BOTTOM_CENTER_MAP;
        }
        if (MapConstants.BOTTOM_CENTER_MAP.equalsIgnoreCase(mapName) || MapConstants.SECOND_MAP.equalsIgnoreCase(mapName) || MapConstants.WATER_MAP.equalsIgnoreCase(mapName)) {
            return MapConstants.MAIN_MAP;
        }
        return MapConstants.TOP_CENTER_MAP;
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
}
