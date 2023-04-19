package base.gameobjects;

import base.Game;
import base.gameobjects.interactionzones.InteractionZone;
import base.gameobjects.interactionzones.InteractionZoneKitchen;
import base.graphicsservice.*;
import base.gui.ContextClue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static base.constants.Constants.*;
import static base.constants.FilePath.QUESTION_ICON_PATH;

public class CookingStove implements GameObject {

    protected static final Logger logger = LoggerFactory.getLogger(CookingStove.class);

    public static final List<Integer> TILE_IDS = Arrays.asList(152, 153);
    private int tileId;

    private transient InteractionZone interactionZone;
    private transient ContextClue contextClue;

    private transient Sprite sprite;
    private final int xPosition;
    private final int yPosition;
    private final Rectangle rectangle;

    public CookingStove(int xPosition, int yPosition, Sprite sprite, int tileId) {
        this(xPosition, yPosition, tileId);
        this.sprite = sprite;
    }

    public CookingStove(int xPosition, int yPosition, int tileId) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.tileId = tileId;
        this.rectangle = new Rectangle(xPosition, yPosition, TILE_SIZE, TILE_SIZE);
        interactionZone = new InteractionZoneKitchen(xPosition + 32, yPosition + 32, 290);
        setContextClue(new ContextClue(new Sprite(ImageLoader.loadImage(QUESTION_ICON_PATH))));
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (sprite != null) {
            renderer.renderSprite(sprite, xPosition, yPosition, ZOOM, false);
        }
        if (DEBUG_MODE) {
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

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void setInteractionZone(InteractionZone interactionZone) {
        this.interactionZone = interactionZone;
    }

    public void setContextClue(ContextClue contextClue) {
        this.contextClue = contextClue;
        int yPos = yPosition - CELL_SIZE;
        if (tileId == 152) {
            yPos = yPosition;
        }
        Position contextCluPosition = new Position(xPosition + (TILE_SIZE / 2), yPos);
        contextClue.changePosition(contextCluPosition);
    }

    public ContextClue getContextClue() {
        return contextClue;
    }

    public InteractionZone getInteractionZone() {
        return interactionZone;
    }

    public int getTileId() {
        return tileId;
    }

    public void setTileId(int tileId) {
        if (TILE_IDS.contains(tileId)) {
            this.tileId = tileId;
        }
    }
}
