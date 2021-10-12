package base.gameobjects.animals;

import base.gameobjects.Animal;
import base.graphicsservice.Sprite;

public class Butterfly extends Animal {

    public Butterfly(Sprite playerSprite, int startX, int startY, int speed) {
        super(playerSprite, startX, startY, speed);
    }

    @Override
    public int getLayer() {
        return 2;
    }
}
