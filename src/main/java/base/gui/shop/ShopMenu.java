package base.gui.shop;

import base.graphicsservice.RenderHandler;
import base.gui.GUI;
import base.gui.GUIButton;

import java.util.List;

public class ShopMenu extends GUI {

    boolean isVisible;

    public ShopMenu(List<GUIButton> buttons, int xPosition, int yPosition) {
        super(buttons, xPosition, yPosition, false);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void switchVisible() {
        isVisible = !isVisible;
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (isVisible) {
            for (GUIButton button : buttons) {
                button.render(renderer, zoom, rectangle);
            }
        }
    }
}
