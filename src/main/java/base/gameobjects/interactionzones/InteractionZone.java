package base.gameobjects.interactionzones;

import base.Game;
import base.gameobjects.GameObject;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class InteractionZone implements GameObject {

    private static final Logger logger = LoggerFactory.getLogger(InteractionZone.class);

    protected transient Circle circle;
    private int centerPointX;
    private int centerPointY;
    private int radius;

    private boolean playerInRange;

    protected InteractionZone(int centerPointX, int centerPointY, int radius) {
        this.centerPointX = centerPointX;
        this.centerPointY = centerPointY;
        this.radius = radius;
        circle = new Circle(centerPointX, centerPointY, radius);
    }

    boolean isInRange(Rectangle rectangle) {
        return circle.intersects(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    public void action(Game game) {
        logger.info("Interaction");
    }

    public void changePosition(int newX, int newY) {
        circle.setCenterX(newX);
        circle.setCenterY(newY);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        renderer.renderCircle(circle);
        logger.debug(String.format("interaction circle location : %s - %s", circle.getCenterX(), circle.getCenterY()));
    }

    @Override
    public void update(Game game) {
        if (circle == null) {
            circle = new Circle(centerPointX, centerPointY, radius);
        }
        if (isInRange(game.getPlayer().getRectangle())) {
            logger.debug("player is in range of interaction zone");
            playerInRange = true;
        } else {
            playerInRange = false;
        }
    }

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        return false;
    }

    public boolean isPlayerInRange() {
        return playerInRange;
    }

    @Override
    public Rectangle getRectangle() {
        return null;
    }

    public int getCenterPointX() {
        return centerPointX;
    }

    public void setCenterPointX(int centerPointX) {
        this.centerPointX = centerPointX;
    }

    public int getCenterPointY() {
        return centerPointY;
    }

    public void setCenterPointY(int centerPointY) {
        this.centerPointY = centerPointY;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
