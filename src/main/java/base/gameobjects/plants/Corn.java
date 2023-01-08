package base.gameobjects.plants;

import base.gameobjects.Plant;

public class Corn extends Plant {

    public static final String NAME = "corn";

    public Corn(int x, int y, String plantType) {
        super(x, y, plantType);
        setGrowingTime(6500);
    }
}
