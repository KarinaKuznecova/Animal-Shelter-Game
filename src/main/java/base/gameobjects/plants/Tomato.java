package base.gameobjects.plants;

import base.gameobjects.Plant;

public class Tomato  extends Plant {

    public static final String NAME = "tomato";

    public Tomato(int x, int y, String plantType) {
        super(x, y, plantType);
        setGrowingTime(3900);
        setRefreshable(true);
    }
}
