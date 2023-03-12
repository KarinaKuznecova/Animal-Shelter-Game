package base.map;

import base.graphicsservice.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Tile {

    private String tileName;
    private transient Sprite sprite;
    private int layer;
    private boolean visibleInMenu;
    private int spriteXPosition;
    private int spriteYPosition;

    private List<TileType> attributes;

    public Tile(String tileName, Sprite sprite, int layer) {
        this.tileName = tileName;
        this.sprite = sprite;
        this.layer = layer;
        visibleInMenu = true;
    }

    public Tile(String tileName, Sprite sprite, int layer, int spriteXPosition, int spriteYPosition) {
        this.tileName = tileName;
        this.sprite = sprite;
        this.layer = layer;
        visibleInMenu = true;
        this.spriteXPosition = spriteXPosition;
        this.spriteYPosition = spriteYPosition;
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

    public String getTileName() {
        return tileName;
    }

    public int getSpriteXPosition() {
        return spriteXPosition;
    }

    public int getSpriteYPosition() {
        return spriteYPosition;
    }

    public List<TileType> getAttributes() {
        return attributes;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void setSpriteXPosition(int spriteXPosition) {
        this.spriteXPosition = spriteXPosition;
    }

    public void setSpriteYPosition(int spriteYPosition) {
        this.spriteYPosition = spriteYPosition;
    }

    public void setAttributes(List<TileType> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(TileType attribute) {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        if (!attributes.contains(attribute)) {
            attributes.add(attribute);
        }
    }

    public boolean isGrass() {
        return attributes.contains(TileType.GRASS);
    }
}
