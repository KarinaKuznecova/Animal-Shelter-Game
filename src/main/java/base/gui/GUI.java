package base.gui;

import base.Game;
import base.gameobjects.GameObject;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

import java.io.Serializable;
import java.util.List;

import static base.constants.Constants.INVENTORY_LIMIT;

public class GUI implements GameObject, Serializable {

    private static final long serialVersionUID = 1L;

    private final Sprite backgroundSprite;
    protected final List<GUIButton> buttons;
    protected Rectangle rectangle;
    private final boolean fixedOnScreen;

    public GUI(Sprite backgroundSprite, List<GUIButton> buttons, int xPosition, int yPosition, boolean fixedOnScreen) {
        this.backgroundSprite = backgroundSprite;
        this.buttons = buttons;
        this.fixedOnScreen = fixedOnScreen;

        rectangle = new Rectangle();
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
    public void render(RenderHandler renderer, int zoom) {
        if (backgroundSprite != null) {
            renderer.renderSprite(backgroundSprite, rectangle.getX(), rectangle.getY(), zoom, fixedOnScreen);
        }
        for (GUIButton button : buttons) {
            button.render(renderer, zoom, rectangle);
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
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
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
                boolean result = button.handleMouseClick(mouseRectangle, camera, zoom, game);
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

    public GUIButton getButtonBySprite(Sprite sprite) {
        for (GUIButton button : buttons) {
            if (sprite.equals(button.getSprite()) && button.getObjectCount() < INVENTORY_LIMIT) {
                return button;
            }
        }
        return getEmptyButton();
    }

    public GUIButton findButtonByDefaultId(String id) {
        for (GUIButton button : buttons) {
            if (button instanceof BackpackButton && ((BackpackButton) button).getDefaultId().equalsIgnoreCase(id)) {
                return button;
            }
        }
        return null;
    }

    public GUIButton getButtonByItemName(String itemName) {
        for (GUIButton button : buttons) {
            if (button instanceof BackpackButton && itemName.equals(((BackpackButton) button).getItemName())) {
                return button;
            }
        }
        return getEmptyButton();
    }

    public GUIButton getEmptyButton() {
        for (GUIButton button : buttons) {
            if (button.sprite == null) {
                return button;
            }
        }
        return null;
    }

    public List<GUIButton> getButtons() {
        return buttons;
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }
}