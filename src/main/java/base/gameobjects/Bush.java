package base.gameobjects;

import base.Game;
import base.gameobjects.animals.Wolf;
import base.gameobjects.interactionzones.InteractionZoneBushWithAnimal;
import base.gameobjects.services.AnimalService;
import base.graphicsservice.*;
import base.gui.ContextClue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static base.constants.ColorConstant.GREEN;
import static base.constants.Constants.*;
import static base.constants.FilePath.HEART_ICON_PATH;
import static base.constants.MapConstants.FOREST_MAP;

public class Bush implements GameObject {

    private static final transient Logger logger = LoggerFactory.getLogger(Bush.class);

    private final int x;
    private final int y;
    private transient Sprite sprite;
    private final Rectangle rectangle;
    private final String mapName;
    private transient boolean canContainAnimal;
    private transient boolean oneTimeSpawn;
    private transient Random random = new Random();

    private transient boolean isAnimalInside;
    private transient String animalType;

    private transient InteractionZoneBushWithAnimal interactionZone;
    private transient ContextClue contextClue;

    private transient int maxInterval;
    private transient int currentInterval;

    private transient AnimalService animalService;

    public Bush(int x, int y, String mapName) {
        this.x = x;
        this.y = y;
        this.mapName = mapName;
        this.canContainAnimal = random.nextBoolean();
        rectangle = new Rectangle(x + 48, y + 75, 96, 82);
        rectangle.generateBorder(1, GREEN);
        interactionZone = new InteractionZoneBushWithAnimal(x + 96, y + 82, 150);

        contextClue = new ContextClue(new Sprite(ImageLoader.loadImage(HEART_ICON_PATH)));

        isAnimalInside = false;
        maxInterval = BUSH_INTERVAL_BOUND + random.nextInt(BUSH_INTERVAL_BOUND);
        currentInterval = maxInterval;
    }

    public void startBush() {
        rectangle.generateBorder(1, GREEN);
        random = new Random();
        this.canContainAnimal = random.nextBoolean();
        interactionZone = new InteractionZoneBushWithAnimal(x + 96, y + 82, 150);
        contextClue = new ContextClue(new Sprite(ImageLoader.loadImage(HEART_ICON_PATH)));

        isAnimalInside = false;
        maxInterval = BUSH_INTERVAL_BOUND + random.nextInt(BUSH_INTERVAL_BOUND);
        currentInterval = maxInterval;
    }

    public void startOneTimeBush() {
        rectangle.generateBorder(1, GREEN);
        random = new Random();
        interactionZone = new InteractionZoneBushWithAnimal(x + 96, y + 82, 150);
        contextClue = new ContextClue(new Sprite(ImageLoader.loadImage(HEART_ICON_PATH)));

        int randomNum = random.nextInt(10);
        if (randomNum != 5) {
            oneTimeSpawn = true;
            canContainAnimal = true;
            maxInterval = 100;
            currentInterval = 0;
        }
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        if (sprite != null) {
            renderer.renderSprite(sprite, x, y, zoom, false);
        }
        if (DEBUG_MODE) {
            renderer.renderRectangle(rectangle, 1, false);
            interactionZone.render(renderer, zoom);
        }
        contextClue.render(renderer, 1, true);
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
        if (!isAnimalInside && !oneTimeSpawn) {
            currentInterval--;
        }
        contextClue.setVisible(canContainAnimal && interactionZone.isPlayerInRange() && isAnimalInside);

        interactionZone.update(game);
        int xPosition = getX() - game.getRenderer().getCamera().getX() + 35;
        int yPosition = getY() - game.getRenderer().getCamera().getY() - 105;
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
            if (oneTimeSpawn) {
                animal.setCurrentMap(mapName);
            } else {
                animal.setCurrentMap(game.getGameMap().getMapName());
            }
            animal.setHungerInPercent(random.nextInt(100));
            animal.setThirstInPercent(random.nextInt(100));
            animal.setEnergyInPercent(random.nextInt(100));
            animal.setCurrentAge(random.nextInt(GROWING_UP_TIME));
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

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void setInteractionZone(InteractionZoneBushWithAnimal interactionZone) {
        this.interactionZone = interactionZone;
    }
}
