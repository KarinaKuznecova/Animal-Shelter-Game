package base.gui;

import base.Game;
import base.graphicsservice.Position;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;

import java.util.List;

import static base.constants.ColorConstant.*;
import static base.constants.Constants.ZOOM;

public class DialogBox extends GUI {

    private String dialogText;

    public DialogBox(List<GUIButton> buttons, Rectangle rectangle) {
        super(buttons, rectangle.getX(), rectangle.getY(), true);
        this.rectangle = rectangle;
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        rectangle.generateBorder(3, BROWN, BLUE);
        renderer.renderRectangle(rectangle, ZOOM, true);

        for (GUIButton button : buttons) {
            button.render(renderer, zoom, rectangle);
        }

        renderer.renderText(dialogText, new Position(rectangle.getX() + 15, rectangle.getY() + 30));
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        boolean stopChecking = false;
        mouseRectangle = new Rectangle(mouseRectangle.getX(), mouseRectangle.getY(), 1, 1);

        for (GUIButton button : buttons) {
            boolean result = button.handleMouseClick(mouseRectangle, camera, zoom, game);
            if (!stopChecking) {
                stopChecking = result;
            }
        }
        return stopChecking;
    }

    public void setDialogText(String dialogText) {
        this.dialogText = dialogText;
    }
}
