package base.gameObjects;

import base.Game;
import base.graphicsService.Rectangle;
import base.graphicsService.RenderHandler;
import base.graphicsService.Sprite;
import base.graphicsService.SpriteSheet;

import java.awt.image.BufferedImage;

public class AnimatedSprite extends Sprite implements GameObject {

    private Sprite[] sprites;
    private int currentSprite = 0;
    private int speed;
    private int counter;
    boolean vertical;

    private int startSprite = 0;
    private int endSprite;

    //@param speed represents how many frames pass until sprite changes
    public AnimatedSprite(BufferedImage[] images, int speed) {
        sprites = new Sprite[images.length];
        this.speed = speed;
        this.startSprite = images.length - 1;

        for (int i = 0; i < sprites.length; i++) {
            sprites[i] = new Sprite(images[i]);
        }
    }

    //higher number = slower speed
    public AnimatedSprite(SpriteSheet sheet, int speed, boolean vertical) {
        sprites = sheet.getLoadedSprites();
        this.speed = speed;
        this.endSprite = sprites.length - 1;
        this.vertical = vertical;
    }

    public AnimatedSprite(SpriteSheet sheet, Rectangle[] positions, int speed) {
        sprites = new Sprite[positions.length];
        this.speed = speed;
        this.endSprite = positions.length - 1;

        for (int i = 0; i < positions.length; i++)
            sprites[i] = new Sprite(sheet, positions[i].getX(), positions[i].getY(), positions[i].getWidth(), positions[i].getHeight());
    }

    // as often as possible
    // render is dealt specifically with the Layer class
    @Override
    public void render(RenderHandler renderer, int xZoom, int yZoom) {
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

    public int getWidth() {
        return sprites[currentSprite].getWidth();
    }

    public int getHeight() {
        return sprites[currentSprite].getHeight();
    }

    public int[] getPixels() {
        return sprites[currentSprite].getPixels();
    }

    public void incrementSprite() {
        if (vertical) {
            currentSprite += 4; //do currentSprite++ if horizontal
        }
        else {
            currentSprite++;
        }
        if (currentSprite >= endSprite) {
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
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
        return false;
    }
}
