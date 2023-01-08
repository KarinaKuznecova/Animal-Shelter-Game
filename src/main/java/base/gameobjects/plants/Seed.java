package base.gameobjects.plants;

import base.gameobjects.Item;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Seed extends Item {

    protected static final transient Logger logger = LoggerFactory.getLogger(Seed.class);

    private String plantType;

    public Seed(String plantType, Sprite sprite, int x, int y) {
        super(x, y, "seed" + plantType, sprite);
        this.plantType = plantType;
    }

    public String getPlantType() {
        return plantType;
    }
}
