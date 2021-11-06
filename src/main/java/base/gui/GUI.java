package base.gui;

import base.Game;
import base.gameobjects.GameObject;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

import java.util.List;

public class GUI implements GameObject {

    private Sprite backgroundSprite;
    private List<GUIButton> buttons;
    private Rectangle rectangle = new Rectangle();
    private boolean fixedOnScreen;

    public GUI(Sprite backgroundSprite, List<GUIButton> buttons, int xPosition, int yPosition, boolean fixedOnScreen) {
        this.backgroundSprite = backgroundSprite;
        this.buttons = buttons;
        this.fixedOnScreen = fixedOnScreen;

        rectangle.setX(xPosition);
        rectangle.setY(yPosition);
        if (backgroundSprite != null) {
            rectangle.setHeight(backgroundSprite.getHeight());
            rectangle.setWidth(backgroundSprite.getWidth());
        }
    }

    public GUI(List<GUIButton> buttons, int xPosition, int yPosition, boolean fixedOnScreen) {
        this(null, buttons, xPosition, yPosition, fixedOnScreen);
    }

    @Override
    public void render(RenderHandler renderer, int xZoom, int yZoom) {
        if (backgroundSprite != null) {
            renderer.renderSprite(backgroundSprite, rectangle.getX(), rectangle.getY(), xZoom, yZoom, fixedOnScreen);
        }
        for (GUIButton button : buttons) {
            button.render(renderer, xZoom, yZoom, rectangle);
        }

    }

    @Override
    public void update(Game game) {
        for (GUIButton button : buttons) {
            button.update(game);
        }
    }

    @Override
    public int getLayer() {
        return 5;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
        boolean stopChecking = false;

        if (!fixedOnScreen) {
            mouseRectangle = new Rectangle(mouseRectangle.getX() + camera.getX(), mouseRectangle.getY() + camera.getY(), 1, 1);
        } else {
            mouseRectangle = new Rectangle(mouseRectangle.getX(), mouseRectangle.getY(), 1, 1);
        }

        if (rectangle.getWidth() == 0 || rectangle.getHeight() == 0 || mouseRectangle.intersects(rectangle)) {
            mouseRectangle.setX(mouseRectangle.getX() - rectangle.getX());
            mouseRectangle.setY(mouseRectangle.getY() - rectangle.getY());
            for (GUIButton button : buttons) {
                boolean result = button.handleMouseClick(mouseRectangle, camera, xZoom, yZoom);
                if (!stopChecking) {
                    stopChecking = result;
                }
            }
        }
        return stopChecking;
    }

    public void addButton(GUIButton button) {
        buttons.add(button);
    }

    public int getButtonCount() {
        return buttons.size();
    }
}