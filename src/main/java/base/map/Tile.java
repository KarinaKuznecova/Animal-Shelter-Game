package base.map;

import base.graphicsservice.Sprite;

public class Tile {

    public String tileName;
    private Sprite sprite;
    private int layer;
    private boolean visible;

    public Tile(String tileName, Sprite sprite, int layer) {
        this.tileName = tileName;
        this.sprite = sprite;
        this.layer = layer;
        visible = true;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public int getLayer() {
        return layer;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
