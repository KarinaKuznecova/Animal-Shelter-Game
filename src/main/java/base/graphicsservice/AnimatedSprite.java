package base.graphicsservice;

import base.Game;
import base.gameobjects.GameObject;

public class AnimatedSprite extends Sprite implements GameObject {

    private final Sprite[] sprites;
    private int currentSprite = 0;
    private int speed;
    private int counter;
    private boolean isVertical;

    private int startSprite = 0;
    private int endSprite;

    //higher number = slower speed
    public AnimatedSprite(SpriteSheet sheet, int speed, boolean isVertical) {
        sprites = sheet.getLoadedSprites();
        this.speed = speed;
        this.endSprite = sprites.length - 1;
        this.isVertical = isVertical;
    }

    public AnimatedSprite(Sprite[] sprites, int speed, boolean isVertical, int endSprite) {
        this.sprites = sprites;
        this.speed = speed;
        this.isVertical = isVertical;
        this.endSprite = endSprite;
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        // as often as possible
        // render is dealt specifically with the Layer class
    }

    // should be 60 fps
    @Override
    public void update(Game game) {
        counter++;
        if (counter >= speed) {
            counter = 0;
            incrementSprite();
        }
    }

    @Override
    public int getWidth() {
        return sprites[currentSprite].getWidth();
    }

    @Override
    public int getHeight() {
        return sprites[currentSprite].getHeight();
    }

    @Override
    public int[] getPixels() {
        return sprites[currentSprite].getPixels();
    }

    public void incrementSprite() {
        if (isVertical) {
            currentSprite += 4;
        } else {
            currentSprite++;
        }
        if (currentSprite > endSprite) {
            currentSprite = startSprite;
        }
    }

    public void setAnimationRange(int startSprite, int endSprite) {
        this.startSprite = startSprite;
        this.endSprite = endSprite;
        reset();
    }

    public void reset() {
        counter = 0;
        currentSprite = startSprite;
    }

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        return false;
    }

    public Sprite getStartSprite() {
        return sprites[startSprite];
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpritesSize() {
        return sprites.length;
    }

    public int getCurrentSprite() {
        return currentSprite;
    }

    public int getEndSprite() {
        return endSprite;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public Rectangle getRectangle() {
        return null;
    }

    public Sprite[] getSprites() {
        return sprites;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }
}
