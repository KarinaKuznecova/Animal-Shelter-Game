package base.constants;

import java.util.HashMap;
import java.util.Map;

import static base.constants.VisibleText.*;

public class MapConstants {

    public static final String MAIN_MAP = "MainMap";
    public static final String TOP_LEFT_MAP = "TopLeftMap";
    public static final String TOP_CENTER_MAP = "TopCenterMap";
    public static final String HOME_MAP = "Home";
    public static final String FOREST_MAP = "Forest";
    public static final String CITY_MAP = "City";

    public static final String TEST_MAP = "TestMap";

    public static final Map<String, String> PRETTIER_MAP_NAMES = new HashMap<>();

    static {
        PRETTIER_MAP_NAMES.put(MAIN_MAP, startingMap);

        PRETTIER_MAP_NAMES.put(HOME_MAP, home);
        PRETTIER_MAP_NAMES.put(TOP_LEFT_MAP, backyard);

        PRETTIER_MAP_NAMES.put(FOREST_MAP, forest);
        PRETTIER_MAP_NAMES.put(CITY_MAP, city);
    }
}
