package base.map;

import base.graphicsService.Sprite;

public class Tile {

    public String tileName;
    public Sprite sprite;
    public int layer;

    public Tile(String tileName, Sprite sprite, int layer) {
        this.tileName = tileName;
        this.sprite = sprite;
        this.layer = layer;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public int getLayer() {
        return layer;
    }
}
