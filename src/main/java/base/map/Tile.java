package base.map;

import base.graphicsservice.Sprite;

public class Tile {

    public String tileName;
    private Sprite sprite;
    private int layer;
    private boolean visibleInMenu;

    public Tile(String tileName, Sprite sprite, int layer) {
        this.tileName = tileName;
        this.sprite = sprite;
        this.layer = layer;
        visibleInMenu = true;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public int getLayer() {
        return layer;
    }

    public boolean isVisibleInMenu() {
        return visibleInMenu;
    }

    public void setVisibleInMenu(boolean visibleInMenu) {
        this.visibleInMenu = visibleInMenu;
    }
}
