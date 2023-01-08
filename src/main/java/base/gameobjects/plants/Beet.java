package base.gameobjects.plants;

import base.gameobjects.Plant;

public class Beet  extends Plant {

    public static final String NAME = "beet";

    public Beet(int x, int y, String plantType) {
        super(x, y, plantType);
        setGrowingTime(5500);
    }
}
