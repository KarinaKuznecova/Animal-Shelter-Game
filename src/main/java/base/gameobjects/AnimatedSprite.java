package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import base.graphicsservice.SpriteSheet;

public class AnimatedSprite extends Sprite implements GameObject {

    private final Sprite[] sprites;
    private int currentSprite = 0;
    private int speed;
    private int counter;
    boolean vertical;

    private int startSprite = 0;
    private int endSprite;

    //higher number = slower speed
    public AnimatedSprite(SpriteSheet sheet, int speed, boolean vertical) {
        sprites = sheet.getLoadedSprites();
        this.speed = speed;
        this.endSprite = sprites.length - 1;
        this.vertical = vertical;
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
        if (vertical) {
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
}
