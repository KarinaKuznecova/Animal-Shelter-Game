package base.gui;

import base.Game;
import base.graphicsservice.Position;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;

public class EscMenuButton extends GUIButton{

    int buttonColor;
    String text;

    public EscMenuButton(Sprite sprite, Rectangle rectangle, boolean fixed, int buttonColor, String text) {
        super(sprite, rectangle, fixed);
        rectangle.generateBackground(buttonColor);
        this.buttonColor = buttonColor;
        this.text = text;
    }

    @Override
    public void render(RenderHandler renderer, int zoom, Rectangle rectangle) {
        renderer.renderRectangle(this.rectangle, rectangle, 1, fixed);
        if (text != null) {
            renderer.renderText(text, new Position(this.rectangle.getX() + rectangle.getX() + (this.rectangle.getWidth() / 3) - 8,
                    this.rectangle.getY() + rectangle.getY() + (this.rectangle.getHeight() / 2) + 4));
        }
    }

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public void activate(Game game) {
        game.changeEscMenuColor(buttonColor);
    }
}
