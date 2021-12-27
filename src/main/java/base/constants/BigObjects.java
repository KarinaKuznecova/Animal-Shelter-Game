package base.constants;

import java.util.Arrays;
import java.util.List;

public class BigObjects {

    public static final List<Integer> flowerIds = Arrays.asList(0, 1, 2, 3, 4, 5, 6);
    public static final List<Integer> chairIds = Arrays.asList(18, 19, 20, 21);
    public static final List<Integer> armChairIds = Arrays.asList(22, 23);
    public static final List<Integer> rugs = Arrays.asList(27, 28, 29);
    public static final List<Integer> bucket = Arrays.asList(33, 34);
    public static final List<Integer> rocks = Arrays.asList(38, 39, 40, 41, 42, 43);
    public static final List<Integer> pumpkins = Arrays.asList(65, 66, 67);
    public static final List<Integer> bookcases = Arrays.asList(72, 69, 70, 71);

    public static final List<List<Integer>> MASTER_TILE_LIST = Arrays.asList(flowerIds, chairIds, armChairIds, rugs, bucket, rocks, pumpkins, bookcases);
}
