package base.gameobjects;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static base.constants.Constants.*;

public class Plant implements GameObject {

    protected static final Logger logger = LoggerFactory.getLogger(Plant.class);

    private transient Sprite previewSprite;
    private transient AnimatedSprite animatedSprite;

    private final Rectangle rectangle;

    private int growingTicks;
    private int growingStage;
    private int growingTime;

    private final String plantType;

    private boolean isWild;
    private boolean isRefreshable;

    public Plant(int x, int y, String plantType) {
        this.plantType = plantType;

        rectangle = new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (animatedSprite != null) {
            renderer.renderSprite(animatedSprite, rectangle.getX(), rectangle.getY(), zoom, false);
        }
    }

    @Override
    public void update(Game game) {
        if (animatedSprite != null && growingStage < animatedSprite.getSpritesSize() - 1) {
            growingTicks++;
            if (growingTicks > getGrowingTime()) {
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
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (mouseRectangle.intersects(rectangle)) {
            logger.info("Plant is clicked");
            if (growingStage == animatedSprite.getSpritesSize() - 1) {
                game.pickUpPlant(this);
            }
            return true;
        }
        return false;
    }

    private void adjustAnimatedSprite() {
        animatedSprite.reset();
        for (int i = 0; i < growingStage; i++) {
            animatedSprite.incrementSprite();
        }
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public int getGrowingStage() {
        return growingStage;
    }

    public void setGrowingStage(int growingStage) {
        this.growingStage = growingStage;
        if (animatedSprite != null) {
            adjustAnimatedSprite();
        }
    }

    public Sprite getPreviewSprite() {
        return previewSprite;
    }

    public int getGrowingTime() {
        return growingTime > 0 ? growingTime : DEFAULT_PLANT_GROWING_TIME;
    }

    public void setGrowingTime(int growingTime) {
        this.growingTime = growingTime;
    }

    public String getPlantType() {
        return plantType;
    }

    public int getGrowingTicks() {
        return growingTicks;
    }

    public void setGrowingTicks(int growingTicks) {
        this.growingTicks = growingTicks;
    }

    public boolean isWild() {
        return isWild;
    }

    public void setWild(boolean wild) {
        isWild = wild;
    }

    public boolean isRefreshable() {
        return isRefreshable;
    }

    public void setRefreshable(boolean refreshable) {
        isRefreshable = refreshable;
    }

    public void setPreviewSprite(Sprite previewSprite) {
        this.previewSprite = previewSprite;
    }

    public void setAnimatedSprite(AnimatedSprite animatedSprite) {
        this.animatedSprite = animatedSprite;
        adjustAnimatedSprite();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plant plant = (Plant) o;
        return growingTicks == plant.growingTicks && growingStage == plant.growingStage && growingTime == plant.growingTime
                && previewSprite.equals(plant.previewSprite) && animatedSprite.equals(plant.animatedSprite)
                && rectangle.equals(plant.rectangle) && plantType.equals(plant.plantType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previewSprite, animatedSprite, rectangle, growingTicks, growingStage, growingTime, plantType);
    }
}
