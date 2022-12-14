package base.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static base.constants.VisibleText.*;

public class MapConstants {

    public static final String MAIN_MAP = "MainMap";
    public static final String SECOND_MAP = "SecondMap";
    public static final String WATER_MAP = "WaterMap";
    public static final String TOP_LEFT_MAP = "TopLeftMap";
    public static final String TOP_CENTER_MAP = "TopCenterMap";
    public static final String TOP_RIGHT_MAP = "TopRightMap";
    public static final String BOTTOM_LEFT_MAP = "BottomLeftMap";
    public static final String BOTTOM_CENTER_MAP = "BottomCenterMap";
    public static final String BOTTOM_RIGHT_MAP = "BottomRightMap";
    public static final String HOME_MAP = "Home";
    public static final String FOREST_MAP = "Forest";
    public static final String CITY_MAP = "City";

    public static final String TEST_MAP = "TestMap";

    public static final List<String> HOME_MAPS = Arrays.asList(TOP_CENTER_MAP, TOP_RIGHT_MAP, HOME_MAP);
    public static final List<String> BOTTOM_MAPS = Arrays.asList(BOTTOM_LEFT_MAP, BOTTOM_CENTER_MAP, BOTTOM_RIGHT_MAP);
    public static final List<String> OUTSIDE_MAPS = Arrays.asList(TOP_LEFT_MAP, MAIN_MAP, FOREST_MAP, CITY_MAP);

    public static final Map<String, String> PRETTIER_MAP_NAMES = new HashMap<>();

    static {
        PRETTIER_MAP_NAMES.put(MAIN_MAP, startingMap);
        PRETTIER_MAP_NAMES.put(SECOND_MAP, secondMap);
        PRETTIER_MAP_NAMES.put(WATER_MAP, island);

        PRETTIER_MAP_NAMES.put(TOP_LEFT_MAP, backyard);
        PRETTIER_MAP_NAMES.put(TOP_CENTER_MAP, home);
        PRETTIER_MAP_NAMES.put(TOP_RIGHT_MAP, home2);

        PRETTIER_MAP_NAMES.put(BOTTOM_LEFT_MAP, bottomLeftMap);
        PRETTIER_MAP_NAMES.put(BOTTOM_CENTER_MAP, bottomCenterMap);
        PRETTIER_MAP_NAMES.put(BOTTOM_RIGHT_MAP, bottomRightMap);

        PRETTIER_MAP_NAMES.put(HOME_MAP, home);
        PRETTIER_MAP_NAMES.put(FOREST_MAP, forest);
        PRETTIER_MAP_NAMES.put(CITY_MAP, city);
    }
}
