package base.gui;

import base.Game;
import base.gameobjects.GameObject;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

public abstract class GUIButton implements GameObject {

    protected Sprite sprite;
    protected Rectangle region;
    protected boolean fixed;

    protected GUIButton(Sprite sprite, Rectangle region, boolean fixed) {
        this.sprite = sprite;
        this.region = region;
        this.fixed = fixed;
    }

    @Override
    public void render(RenderHandler renderer, int xZoom, int yZoom) {}

    public void render(RenderHandler renderer, int xZoom, int yZoom, Rectangle interfaceRect) {
        renderer.renderSprite(sprite, region.getX() + interfaceRect.getX(), region.getY() + interfaceRect.getY(), xZoom, yZoom, fixed);
    }

    @Override
    public void update(Game game) {

    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
        if (mouseRectangle.intersects(region)) {
            activate();
            return true;
        }
        return false;
    }

    public abstract void activate();
}