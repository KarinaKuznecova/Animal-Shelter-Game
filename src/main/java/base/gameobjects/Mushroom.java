package base.gameobjects;

import base.Game;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.MapConstants.FOREST_MAP;

public class Mushroom extends Item implements GameObject {

    protected static final Logger logger = LoggerFactory.getLogger(Mushroom.class);

    public static final String ITEM_NAME = "mushroom";
    public static final int TILE_ID = 77;

    public Mushroom(int xPosition, int yPosition, Sprite sprite) {
        super(xPosition, yPosition, ITEM_NAME, sprite);
    }

    public Mushroom(int xPosition, int yPosition) {
        super(xPosition, yPosition, ITEM_NAME);
        setMapName(FOREST_MAP);
    }

    @Override
    public void update(Game game) {

    }

    @Override
    public int getLayer() {
        return 1;
    }

}
