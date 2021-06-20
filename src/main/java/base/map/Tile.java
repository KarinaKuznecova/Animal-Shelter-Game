package base.map;

import base.graphicsService.Sprite;

public class Tile {

    public String tileName;
    public Sprite sprite;

    public Tile(String tileName, Sprite sprite) {
        this.tileName = tileName;
        this.sprite = sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
