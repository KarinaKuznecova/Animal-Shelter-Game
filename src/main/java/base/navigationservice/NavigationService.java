package base.navigationservice;

import java.util.Arrays;
import java.util.List;

import static base.constants.Constants.*;

public class NavigationService {

    public static final List<String> MAPS_NEAR_MAIN_MAP = Arrays.asList(TOP_CENTER_MAP, SECOND_MAP, WATER_MAP, BOTTOM_CENTER_MAP);

    public static String getNextMapToGetToCenter(String mapName) {
        if (MAPS_NEAR_MAIN_MAP.contains(mapName) || mapName == null) {
            return MAIN_MAP;
        }
        if (mapName.startsWith("Bottom")) {
            return BOTTOM_CENTER_MAP;
        }
        if (mapName.startsWith("TOP")) {
            return TOP_CENTER_MAP;
        }
        return mapName;
    }

    public static String getNextMapToGetToHome(String mapName) {
        if (mapName == null) {
            return null;
        }
        if (BOTTOM_LEFT_MAP.equalsIgnoreCase(mapName) || BOTTOM_RIGHT_MAP.equalsIgnoreCase(mapName)) {
            return BOTTOM_CENTER_MAP;
        }
        if (BOTTOM_CENTER_MAP.equalsIgnoreCase(mapName) || SECOND_MAP.equalsIgnoreCase(mapName) || WATER_MAP.equalsIgnoreCase(mapName)) {
            return MAIN_MAP;
        }
        return TOP_CENTER_MAP;
    }
}
