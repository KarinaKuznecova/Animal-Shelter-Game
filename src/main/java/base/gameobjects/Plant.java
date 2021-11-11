package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.Game.TILE_SIZE;

public class Plant implements GameObject {

    protected static final Logger logger = LoggerFactory.getLogger(Plant.class);
    public static final int GROWING_TIME = 400;

    Sprite previewSprite;
    AnimatedSprite animatedSprite;

    Rectangle rectangle;

    int growingTicks;
    int growingStage;

    int plantId;

    public Plant(Sprite previewSprite, AnimatedSprite animatedSprite, int x, int y, int plantId) {
        this.previewSprite = previewSprite;
        this.animatedSprite = animatedSprite;

        rectangle = new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
        rectangle.generateGraphics(1, 123);
        this.plantId = plantId;
    }


    @Override
    public void render(RenderHandler renderer, int xZoom, int yZoom) {
        if (animatedSprite != null) {
            renderer.renderSprite(animatedSprite, rectangle.getX(), rectangle.getY(), xZoom, yZoom, false);
        }
    }

    @Override
    public void update(Game game) {
        if (growingStage < 3) {
            growingTicks++;
            if (growingTicks > GROWING_TIME) {
                animatedSprite.incrementSprite();
                growingStage++;
                growingTicks = 0;
            }
        }
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
        return false;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public int getPlantId() {
        return plantId;
    }

    public int getGrowingStage() {
        return growingStage;
    }

    public void setGrowingStage(int growingStage) {
        for (int i = 0; i < growingStage; i++) {
            animatedSprite.incrementSprite();
        }
        this.growingStage = growingStage;
    }
}
