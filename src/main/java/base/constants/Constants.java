package base.constants;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final int TILE_SIZE = 32;
    public static final int ZOOM = 2;
    public static final int CELL_SIZE = TILE_SIZE * ZOOM;
    public static final int PLAYER_SPRITE_SIZE = 37;

    public static final String DEBUG_MODE_PROPERTY = "debug-mode";
    public static boolean DEBUG_MODE;

    public static final String CHEATS_MODE_PROPERTY = "cheats";
    public static boolean CHEATS_MODE;

    public static final String TEST_MAP_PROPERTY = "test-map";
    public static boolean TEST_MAP_MODE;

    public static final String CURRENT_GAME_VERSION = "1.4.2";

    public static final int INVENTORY_LIMIT = 99;
    public static final int ANIMAL_LIMIT = 15;
    public static final int BUSH_INTERVAL_BOUND = 7000;
    public static final int EVENTS_INTERVAL = 1000;
    public static final int MAX_FOOD_FRESHNESS = 25_000;

    private static final String ENGLISH = "eng";
    private static final String LATVIAN = "lv";
    private static final String RUSSIAN = "ru";
    private static final String SWEDEN = "se";
    private static final String INDIAN = "hin";
    private static final String GERMAN = "ger";
    private static final String ITALIAN = "it";
    private static final String SPANISH = "spn";

    public static final String LANGUAGE_PROPERTY = "language";
    public static String LANGUAGE;

    public static final int BOWL_TILE_ID = 68;
    public static final int WATER_BOWL_TILE_ID = 73;
    public static final int PILLOW_TILE_ID = 74;
    public static final int CHEST_TILE_ID = 36;

    public static final int DEFAULT_PLANT_GROWING_TIME = 3000;

    public static int MAX_SCREEN_WIDTH = 21 * (CELL_SIZE);
    public static int MAX_SCREEN_HEIGHT = 21 * (CELL_SIZE);

    public static final int GROWING_UP_TIME = 200_000;
    public static final int MAX_HUNGER = 30_000;
    public static final int MAX_THIRST = 25_000;
    public static final int MAX_ENERGY = 40_000;
    public static final int MIN_ENERGY = 1;

    public static final String CAT_BLACK = "cat-black";
    public static final String CAT_WHITE = "cat-white";
    public static final String CAT_BROWN = "cat-brown";
    public static final String CAT_CARAMEL = "cat-caramel";
    public static final List<String> CAT_COLORS = Arrays.asList(CAT_WHITE, CAT_BROWN, CAT_CARAMEL, CAT_BLACK);

    public static final String RAT_BLACK = "rat-black";
    public static final String RAT_BLACK_WHITE = "rat-blackwhite";
    public static final String RAT_RED = "rat-red";
    public static final String RAT_WHITE = "rat-white";
    public static final String RAT_SILVER = "rat-silver";
    public static final List<String> RAT_COLORS = Arrays.asList(RAT_BLACK_WHITE, RAT_RED, RAT_WHITE, RAT_SILVER,
            RAT_BLACK);

}
