package base.gameobjects;

import base.Game;
import base.gameobjects.animals.Wolf;
import base.gameobjects.interactionzones.InteractionZoneBushWithAnimal;
import base.gameobjects.services.AnimalService;
import base.graphicsservice.Position;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.Sprite;
import base.gui.ContextClue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static base.constants.ColorConstant.GREEN;
import static base.constants.Constants.*;
import static base.constants.FilePath.BUSH_IMG;
import static base.constants.MapConstants.FOREST_MAP;
import static base.graphicsservice.ImageLoader.getPreviewSprite;

public class Bush implements GameObject {

    private static final transient Logger logger = LoggerFactory.getLogger(Bush.class);

    private final int x;
    private final int y;
    private final transient Sprite sprite;
    private final Rectangle rectangle;
    private final String mapName;
    private final boolean canContainAnimal;
    private final transient Random random = new Random();

    private boolean isAnimalInside;
    private String animalType;

    private final transient InteractionZoneBushWithAnimal interactionZone;
    private final ContextClue contextClue;

    private final int maxInterval;
    private int currentInterval;

    private transient AnimalService animalService;

    public Bush(int x, int y, String mapName) {
        this.x = x;
        this.y = y;
        this.mapName = mapName;
        this.canContainAnimal = random.nextBoolean();
        sprite = getPreviewSprite(BUSH_IMG);
        rectangle = new Rectangle(x, y, 96, 82);
        rectangle.generateBorder(1, GREEN);
        interactionZone = new InteractionZoneBushWithAnimal(rectangle.getX() + 96, rectangle.getY() + 82, 150);

        contextClue = new ContextClue();

        isAnimalInside = false;
        maxInterval = 7000 + random.nextInt(7000);
        currentInterval = maxInterval;
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (sprite != null) {
            renderer.renderSprite(sprite, x, y, zoom, false);
        }
        if (DEBUG_MODE) {
            renderer.renderRectangle(rectangle, ZOOM, false);
            interactionZone.render(renderer, zoom);
        }
        if (canContainAnimal && interactionZone.isPlayerInRange() && isAnimalInside) {
            contextClue.render(renderer, 1);
        }
    }

    @Override
    public void update(Game game) {
        if (!canContainAnimal) {
            return;
        }
        if (currentInterval < 1) {
            createAnimalInside(game);
            currentInterval = maxInterval;
        }
        if (!isAnimalInside) {
            currentInterval--;
        }
        interactionZone.update(game);
        int xPosition = getX() - game.getRenderer().getCamera().getX() + 85;
        int yPosition = getY() - game.getRenderer().getCamera().getY() - 30;
        Position contextCluPosition = new Position(xPosition, yPosition);
        contextClue.changePosition(contextCluPosition);
    }

    @Override
    public int getLayer() {
        return 3;
    }

    @Override
    public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int zoom, Game game) {
        if (!canContainAnimal) {
            return false;
        }
        if (mouseRectangle.intersects(rectangle) && isAnimalInside && interactionZone.isPlayerInRange()) {
            isAnimalInside = false;
            Animal animal = animalService.createAnimal(rectangle.getX(), rectangle.getY(), animalType, mapName);
            animal.setCurrentMap(game.getGameMap().getMapName());
            animal.setHungerInPercent(random.nextInt(100));
            animal.setThirstInPercent(random.nextInt(100));
            animal.setEnergyInPercent(random.nextInt(100));
            animal.setCurrentAge(random.nextInt(Animal.GROWING_UP_TIME));
            game.getAnimalsOnMaps().get(game.getGameMap().getMapName()).add(animal);
            game.addAnimalToPanel(animal);
            game.saveMaps();

            return true;
        }
        return false;
    }

    private void createAnimalInside(Game game) {
        if (game.getAnimalCount() >= ANIMAL_LIMIT) {
            logger.debug("Too many animals");
            return;
        }
        animalService = game.getAnimalService();
        animalType = getPotentialAnimal();
        isAnimalInside = true;
        logger.info(String.format("New animal inside the bush - %s", animalType));
    }

    private String getPotentialAnimal() {
        String potentialAnimal = animalService.getRandomAnimalType();
        if (Wolf.TYPE.equalsIgnoreCase(potentialAnimal) && !FOREST_MAP.equalsIgnoreCase(mapName)) {
            logger.debug("Wolf cannot appear on non-forest map, will recalculate");
            return getPotentialAnimal();
        }
        return potentialAnimal;
    }

    public int getX() {
        return rectangle.getX();
    }

    public int getY() {
        return rectangle.getY();
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }

    public String getMapName() {
        return mapName;
    }

    public boolean isCanContainAnimal() {
        return canContainAnimal;
    }

    public boolean isAnimalInside() {
        return isAnimalInside;
    }

    public String getAnimalType() {
        return animalType;
    }

    public ContextClue getContextClue() {
        return contextClue;
    }

    public int getMaxInterval() {
        return maxInterval;
    }

    public int getCurrentInterval() {
        return currentInterval;
    }
}
