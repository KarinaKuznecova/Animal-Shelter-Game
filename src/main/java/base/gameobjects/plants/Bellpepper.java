package base.gameobjects.plants;

import base.gameobjects.Plant;

public class Bellpepper extends Plant {

    public static final String NAME = "bellpepper";

    public Bellpepper(int x, int y, String plantType) {
        super(x, y, plantType);
        setGrowingTime(5500);
        setRefreshable(true);
    }
}
