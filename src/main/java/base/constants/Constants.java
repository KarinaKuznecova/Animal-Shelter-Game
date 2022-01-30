package base.constants;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final int TILE_SIZE = 32;
    public static final int ZOOM = 2;

    public static final String CURRENT_GAME_VERSION = "1.2.2";

    public static final int BOWL_TILE_ID = 68;

    public static final int DEFAULT_PLANT_GROWING_TIME = 3000;

    public static int MAX_SCREEN_WIDTH = 21 * (TILE_SIZE * ZOOM);
    public static int MAX_SCREEN_HEIGHT = 21 * (TILE_SIZE * ZOOM);

    public static final String CAT_BLACK = "cat-black";
    public static final String CAT_WHITE = "cat-white";
    public static final String CAT_BROWN = "cat-brown";
    public static final String CAT_CARAMEL = "cat-caramel";
    public static final List<String> CAT_COLORS = Arrays.asList(CAT_BLACK, CAT_WHITE, CAT_BROWN, CAT_CARAMEL);

}
