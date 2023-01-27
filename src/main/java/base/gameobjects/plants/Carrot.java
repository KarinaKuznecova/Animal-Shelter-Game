package base.gameobjects.plants;

import base.gameobjects.Plant;

public class Carrot  extends Plant {

    public static final String NAME = "carrot";

    public Carrot(int x, int y, String plantType) {
        super(x, y, plantType);
        setGrowingTime(4300);
    }
}
