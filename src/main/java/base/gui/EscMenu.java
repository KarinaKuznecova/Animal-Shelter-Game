package base.gui;

import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;

import java.util.List;

public class EscMenu extends GUI {

    private Rectangle backGroundRectangle;

    public EscMenu(List<GUIButton> buttons, int x, int y, int width, int height, int color) {
        super(buttons, x, y, true);
        backGroundRectangle = new Rectangle(x, y, width, height);
        backGroundRectangle.generateBackground(color);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        super.render(renderer, zoom);
        renderer.renderRectangle(backGroundRectangle, zoom, true);

        for (GUIButton button : buttons) {
            button.render(renderer, zoom, rectangle);
        }
    }

    public void changeColor(int newColor) {
        backGroundRectangle.generateBackground(newColor);
    }
}
