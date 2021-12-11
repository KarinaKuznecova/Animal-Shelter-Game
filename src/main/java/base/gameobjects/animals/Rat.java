package base.gameobjects.animals;

import base.gameobjects.Animal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rat extends Animal {

    public static final String NAME = "rat";

    private static final Logger logger = LoggerFactory.getLogger(Rat.class);

    public Rat(int startX, int startY, int speed, int hungerLevel) {
        super(NAME, startX, startY, speed, 32, hungerLevel);
    }
}
