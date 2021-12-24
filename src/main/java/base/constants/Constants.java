package base.constants;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final int TILE_SIZE = 32;
    public static final int ZOOM = 2;

    public static final int BOWL_TILE_ID = 68;

    public static final int DEFAULT_PLANT_GROWING_TIME = 3000;

    public static int MAX_SCREEN_WIDTH = 21 * (TILE_SIZE * ZOOM);
    public static int MAX_SCREEN_HEIGHT = 21 * (TILE_SIZE * ZOOM);

    public static final String CAT_BLACK = "cat-black";
    public static final String CAT_WHITE = "cat-white";
    public static final String CAT_BROWN = "cat-brown";
    public static final String CAT_CARAMEL = "cat-caramel";

    public static final String MAIN_MAP = "MainMap";
    public static final String SECOND_MAP = "SecondMap";
    public static final String WATER_MAP = "WaterMap";
    public static final String TOP_LEFT_MAP = "TopLeftMap";
    public static final String TOP_CENTER_MAP = "TopCenterMap";
    public static final String TOP_RIGHT_MAP = "TopRightMap";
    public static final String BOTTOM_LEFT_MAP = "BottomLeftMap";
    public static final String BOTTOM_CENTER_MAP = "BottomCenterMap";
    public static final String BOTTOM_RIGHT_MAP = "BottomRightMap";

    public static final List<String> MAPS_NEAR_MAIN_MAP = Arrays.asList(TOP_CENTER_MAP, SECOND_MAP, WATER_MAP, BOTTOM_CENTER_MAP);
    public static final List<String> HOME_MAPS = Arrays.asList(TOP_CENTER_MAP, TOP_RIGHT_MAP);
}
