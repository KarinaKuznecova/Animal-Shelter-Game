package base.gui;

import base.Game;
import base.gameobjects.GameObject;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

import java.io.Serializable;

public abstract class GUIButton implements GameObject, Serializable {

    private static final long serialVersionUID = 1L;

    protected GuiService guiService = new GuiService();

    protected Sprite sprite;
    protected Rectangle region;
    protected boolean fixed;
    protected int objectCount;
    protected boolean multipleOptions;

    protected GUIButton(Sprite sprite, Rectangle region, boolean fixed) {
        this.sprite = sprite;
        this.region = region;
        this.fixed = fixed;
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
    }

    public void render(RenderHandler renderer, int zoom, Rectangle interfaceRect) {
        if (objectCount > 1) {
            renderer.renderSprite(sprite, region.getX() + interfaceRect.getX(), region.getY() + interfaceRect.getY(), zoom, fixed, objectCount);
        } else {
            renderer.renderSprite(sprite, region.getX() + interfaceRect.getX(), region.getY() + interfaceRect.getY(), zoom, fixed, 0);
        }
    }

    @Override
    public void update(Game game) {

    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (mouseRectangle.intersects(region)) {
            activate();
            return true;
        }
        return false;
    }

    public abstract void activate();

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public int getObjectCount() {
        return objectCount;
    }

    public void setObjectCount(int objectCount) {
        this.objectCount = objectCount;
    }
}