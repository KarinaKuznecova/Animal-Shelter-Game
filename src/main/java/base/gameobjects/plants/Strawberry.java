package base.gameobjects.plants;

import base.gameobjects.Plant;

public class Strawberry  extends Plant {

    public static final String NAME = "strawberry";

    public Strawberry(int x, int y, String plantType) {
        super(x, y, plantType);
        setGrowingTime(3500);
        setRefreshable(true);
    }
}
