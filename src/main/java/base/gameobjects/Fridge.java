package base.gameobjects;

import base.Game;
import base.gameobjects.interactionzones.InteractionZone;
import base.gameobjects.interactionzones.InteractionZoneKitchen;
import base.graphicsservice.*;
import base.gui.ContextClue;
import base.map.bigobjects.Bookcase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.ColorConstant.GREEN;
import static base.constants.Constants.*;
import static base.constants.FilePath.QUESTION_ICON_PATH;

public class Fridge extends Bookcase implements GameObject {

    protected static final Logger logger = LoggerFactory.getLogger(Fridge.class);

    public static final int TILE_ID = 72;

    private transient InteractionZone interactionZone;
    private transient ContextClue contextClue;
    private final Rectangle rectangle;
    private final int xPosition;
    private final int yPosition;

    public Fridge(int xPosition, int yPosition) {
        super(xPosition, yPosition, 0,1);
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.rectangle = new Rectangle(xPosition, yPosition - CELL_SIZE, TILE_SIZE, CELL_SIZE + TILE_SIZE);
        rectangle.generateBorder(1, GREEN);
        interactionZone = new InteractionZoneKitchen(xPosition + 32, yPosition + 32, 290);
        setContextClue(new ContextClue(new Sprite(ImageLoader.loadImage(QUESTION_ICON_PATH))));
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (DEBUG_MODE) {
            renderer.renderRectangle(rectangle, zoom, false);
            interactionZone.render(renderer, zoom);
        }
        if (interactionZone.isPlayerInRange()) {
            contextClue.render(renderer, 1, false);
        }
    }

    @Override
    public void update(Game game) {
        interactionZone.update(game);
        if (!game.isInRangeOfAnyKitchen() && game.isCookingMenuOpen()) {
            game.hideCookingMenu();
            game.closeBackpack();
        }
        if (!interactionZone.isPlayerInRange()) {
            contextClue.setVisible(false);
        }
        if (interactionZone.isPlayerInRange() && !game.isAnyKitchenContextClueVisible()) {
            contextClue.setVisible(true);
        }
    }

    @Override
    public int getLayer() {
        return 3;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (mouseRectangle.intersects(rectangle)) {
            logger.info("Fridge is clicked");
            if (interactionZone.isPlayerInRange()) {
                game.showCookingMenu();
            }
            return true;
        }
        return false;
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setContextClue(ContextClue contextClue) {
        this.contextClue = contextClue;
        Position contextCluPosition = new Position(xPosition + (TILE_SIZE / 2), yPosition - CELL_SIZE);
        contextClue.changePosition(contextCluPosition);
    }

    public ContextClue getContextClue() {
        return contextClue;
    }

    public void setInteractionZone(InteractionZone interactionZone) {
        this.interactionZone = interactionZone;
    }

    public InteractionZone getInteractionZone() {
        return interactionZone;
    }
}
