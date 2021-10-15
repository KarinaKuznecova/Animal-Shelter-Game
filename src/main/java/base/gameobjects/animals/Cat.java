package base.gameobjects.animals;

import base.gameobjects.Animal;
import base.graphicsservice.Sprite;

public class Cat extends Animal {

    public Cat(Sprite playerSprite, int startX, int startY, int speed) {
        super(playerSprite, startX, startY, speed);
    }

    @Override
    public String getHomeMap() {
        return "TopCenterMap";
    }
}
