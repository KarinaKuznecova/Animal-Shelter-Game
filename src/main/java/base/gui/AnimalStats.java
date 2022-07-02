package base.gui;

import base.Game;
import base.gameobjects.Animal;
import base.graphicsservice.Position;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;

import static base.constants.ColorConstant.*;
import static base.constants.MapConstants.PRETTIER_MAP_NAMES;
import static base.constants.VisibleText.*;

public class AnimalStats {

    private final Animal animal;
    private final Rectangle region;
    private boolean isVisible;
    private final EditIcon editIcon;
    private final HeartIcon heartIcon;

    public AnimalStats(Animal animal, Rectangle region) {
        this.animal = animal;
        this.region = region;
        editIcon = new EditIcon();
        heartIcon = new HeartIcon();
    }

    void renderStats(RenderHandler renderer, Rectangle rectangle) {
        Rectangle statsRectangle = new Rectangle(region.getX() - 280, region.getY(), 270, 140);
        statsRectangle.generateBorder(2, BROWN, LIGHT_BLUE);
        renderer.renderRectangle(statsRectangle, rectangle, 1, true);
        renderer.renderCustomizableText(name + ": ", animal.getName(), new Position(statsRectangle.getX() + 30, statsRectangle.getY() + 30), editIcon);
        renderer.renderText(hunger + ": " + animal.getCurrentHungerInPercent() + "%", new Position(statsRectangle.getX() + 30, statsRectangle.getY() + 50));
        renderer.renderText(thirst + ": " + animal.getCurrentThirstInPercent() + "%", new Position(statsRectangle.getX() + 30, statsRectangle.getY() + 70));
        renderer.renderText(energy + ": " + animal.getCurrentEnergyInPercent() + "%", new Position(statsRectangle.getX() + 30, statsRectangle.getY() + 90));
        renderer.renderText(location + ": " + PRETTIER_MAP_NAMES.get(animal.getCurrentMap()), new Position(statsRectangle.getX() + 30, statsRectangle.getY() + 110));
        renderer.renderText(age + ": " + animal.getAge().toString().toLowerCase(), new Position(statsRectangle.getX() + 30, statsRectangle.getY() + 130));

        editIcon.render(renderer, 1);
        if (animal.isFavorite()) {
            heartIcon.changePosition(new Position(statsRectangle.getX() + 230, statsRectangle.getY() + 16));
            heartIcon.render(renderer, 1);
        }
    }

    public void update(Game game) {
        if (animal == game.getYourSelectedAnimal()) {
            if (!isVisible) {
                isVisible = true;
            }
        } else {
            if (isVisible) {
                isVisible = false;
            }
        }
    }

    public boolean handleMouseClick(Rectangle mouseRectangle, Game game) {
        if (isVisible) {
            return editIcon.handleMouseClick(mouseRectangle, game, animal);
        }
        return false;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
