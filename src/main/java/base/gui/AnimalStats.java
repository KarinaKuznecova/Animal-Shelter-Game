package base.gui;

import base.Game;
import base.gameobjects.AgeStage;
import base.gameobjects.Animal;
import base.gameobjects.animaltraits.Trait;
import base.graphicsservice.Position;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;

import java.util.List;

import static base.constants.ColorConstant.BROWN;
import static base.constants.ColorConstant.LIGHT_BLUE;
import static base.constants.Constants.DEBUG_MODE;
import static base.constants.Constants.GROWING_UP_TIME;
import static base.constants.MapConstants.PRETTIER_MAP_NAMES;
import static base.constants.VisibleText.*;

public class AnimalStats {

    private final Animal animal;
    private final Rectangle region;
    private boolean isVisible;
    private final EditIcon editIcon;
    private final HeartIcon heartIcon;
    private Game game;

    public AnimalStats(Game game, Animal animal, Rectangle region) {
        this.game = game;
        this.animal = animal;
        this.region = region;
        editIcon = new EditIcon();
        heartIcon = new HeartIcon();
    }

    void renderStats(RenderHandler renderer, Rectangle rectangle) {
        Rectangle statsRectangle;
        if (DEBUG_MODE && animal.getAge() != AgeStage.ADULT) {
            statsRectangle = new Rectangle(region.getX() - 280, region.getY(), 270, 200);
        } else {
            statsRectangle = new Rectangle(region.getX() - 280, region.getY(), 270, 180);
        }
        if (animal.getPersonality().size() > 4) {
            statsRectangle.setHeight(statsRectangle.getHeight() + (30 * (animal.getPersonality().size() / 4)));
        }
        statsRectangle.generateBorder(2, BROWN, LIGHT_BLUE);
        renderer.renderRectangle(statsRectangle, rectangle, 1, true);
        renderer.renderCustomizableText(name + ": " + animal.getName(), new Position(statsRectangle.getX() + 30, statsRectangle.getY() + 30), editIcon);
        renderer.renderText(hunger + ": " + animal.getCurrentHungerInPercent() + "%", new Position(statsRectangle.getX() + 30, statsRectangle.getY() + 50));
        renderer.renderText(thirst + ": " + animal.getCurrentThirstInPercent() + "%", new Position(statsRectangle.getX() + 30, statsRectangle.getY() + 70));
        renderer.renderText(energy + ": " + animal.getCurrentEnergyInPercent() + "%", new Position(statsRectangle.getX() + 30, statsRectangle.getY() + 90));
        renderer.renderText(location + ": " + PRETTIER_MAP_NAMES.get(animal.getCurrentMap()), new Position(statsRectangle.getX() + 30, statsRectangle.getY() + 110));
        renderer.renderText(age + ": " + animal.getAge().toString().toLowerCase(), new Position(statsRectangle.getX() + 30, statsRectangle.getY() + 130));
        if (DEBUG_MODE && animal.getAge() != AgeStage.ADULT) {
            renderer.renderText(age + ": " + animal.getCurrentAge() + " / " + GROWING_UP_TIME, new Position(statsRectangle.getX() + 30, statsRectangle.getY() + 170));
        }

        editIcon.render(renderer, 1);
        if (animal.isFavorite()) {
            heartIcon.changePosition(new Position(statsRectangle.getX() + 230, statsRectangle.getY() + 16));
            heartIcon.render(renderer, 1);
        }

        List<Trait> personality = animal.getPersonality();
        for (int i = 1; i <= personality.size(); i++) {
            Trait trait = personality.get(i-1);
            int traitX = statsRectangle.getX() + (i * 40);
            int traitY = statsRectangle.getY() + 140 + (i / 4 * 30);
            renderer.renderSprite(game.getSpriteService().getTraitIcon(trait), traitX, traitY, 2, true);
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
