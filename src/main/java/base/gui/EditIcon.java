package base.gui;

import base.Game;
import base.gameobjects.Animal;
import base.graphicsservice.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.FilePath.EDIT_ICON_PATH;

public class EditIcon {

    private final Sprite sprite;
    private Rectangle rectangle;

    protected static final Logger logger = LoggerFactory.getLogger(EditIcon.class);

    public EditIcon() {
        sprite = new Sprite(ImageLoader.loadImage(EDIT_ICON_PATH));
        rectangle = new Rectangle();
    }

    public void changePosition(Position position) {
        rectangle = new Rectangle(position.getXPosition() - 3, position.getYPosition() - 3, 20, 20);
    }

    public void render(RenderHandler renderer, int zoom) {
        renderer.renderSprite(sprite, rectangle.getX() + 2, rectangle.getY() + 2, zoom, true);
    }

    public boolean handleMouseClick(Rectangle mouseRectangle, Game game, Animal animal) {
        if (mouseRectangle.intersects(rectangle)) {
            logger.info("edit icon clicked");
            game.editAnimalName(animal);
            return true;
        }
        return false;
    }
}
