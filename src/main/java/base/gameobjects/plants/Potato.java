package base.gameobjects.plants;

import base.gameobjects.Plant;

public class Potato extends Plant {

    public static final String NAME = "potato";

    public Potato(int x, int y, String plantType) {
        super(x, y, plantType);
        setGrowingTime(4900);
        setRefreshable(false);
    }
}
