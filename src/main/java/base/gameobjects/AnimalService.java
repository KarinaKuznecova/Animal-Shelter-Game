package base.gameobjects;

import base.Game;
import base.gameobjects.animals.*;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Sprite;
import base.graphicsservice.SpriteSheet;
import base.map.GameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.*;

import static base.Game.TILE_SIZE;

public class AnimalService {

    public static final String RAT = "rat";
    public static final String MOUSE = "mouse";
    public static final String CHICKEN = "chicken";
    public static final String BUTTERFLY = "butterfly";
    public static final String CAT = "cat";
    public static final String CAT2 = "cat2";

    public static final String RAT_SHEET_PATH = "img/rat.png";
    public static final String MOUSE_SHEET_PATH = "img/mouse.png";
    public static final String CHICKEN_SHEET_PATH = "img/chicken.png";
    public static final String BUTTERFLY_SHEET_PATH = "img/butterfly.png";
    public static final String CAT_SHEET_PATH = "img/cat1.png";
    public static final String CAT_SHEET_PATH2 = "img/cat2.png";

    Map<String, String> animalAnimations;
    List<Animal> allAnimals;

    ImageLoader imageLoader = new ImageLoader();

    protected static final Logger logger = LoggerFactory.getLogger(AnimalService.class);

    public AnimalService() {
        initializeAnimationMapping();
        allAnimals = new ArrayList<>();
    }

    void initializeAnimationMapping() {
        animalAnimations = new HashMap<>();
        animalAnimations.put(RAT, RAT_SHEET_PATH);
        animalAnimations.put(MOUSE, MOUSE_SHEET_PATH);
        animalAnimations.put(CHICKEN, CHICKEN_SHEET_PATH);
        animalAnimations.put(BUTTERFLY, BUTTERFLY_SHEET_PATH);
        animalAnimations.put(CAT, CAT_SHEET_PATH);
        animalAnimations.put(CAT2, CAT_SHEET_PATH2);
    }

    public List<Animal> getListOfAnimals() {
        return allAnimals;
    }

    public List<String> listOfAnimalsToLoad() {
        return Arrays.asList(RAT, CHICKEN, MOUSE, RAT, RAT, BUTTERFLY, MOUSE, CAT, CAT2);
    }

    public String getAnimalSheetPath(String animalName) {
        return animalAnimations.get(animalName);
    }

    public void loadAnimatedImages(List<String> animalsToLoad, int startX, int startY, String mapName) {

        for (String animal : animalsToLoad) {
            animal = animal.toLowerCase();
            BufferedImage sheetImage = imageLoader.loadImage(getAnimalSheetPath(animal));
            SpriteSheet animalSheet = new SpriteSheet(sheetImage);
            animalSheet.loadSprites(TILE_SIZE, TILE_SIZE, 0);
            AnimatedSprite sprite = new AnimatedSprite(animalSheet, 9, false);
            allAnimals.add(createAnimal(animal, sprite, startX, startY, mapName));
        }
    }

    public Animal createAnimal(String animalName, Sprite sprite, int startX, int startY, String mapName) {
        Animal animal;
        switch (animalName.toLowerCase()) {
            case RAT:
                animal = new Rat(sprite, startX, startY, 3);
                break;
            case MOUSE:
                animal = new Mouse(sprite, startX, startY, 2);
                break;
            case CHICKEN:
                animal = new Chicken(sprite, startX, startY, 2);
                break;
            case BUTTERFLY:
                animal = new Butterfly(sprite, startX, startY, 1);
                break;
            case CAT:
            case CAT2:
                animal = new Cat(sprite, startX, startY, 2);
                break;
            default:
                logger.error(String.format("Unknown animal requested or animal not defined : %s", animalName));
                throw new IllegalArgumentException();
        }
        animal.setHomeMap(mapName);
        return animal;
    }

    public String getAnimalType(Animal animal) {
        if (animal instanceof Rat) {
            return RAT;
        }
        if (animal instanceof Mouse) {
            return MOUSE;
        }
        if (animal instanceof Chicken) {
            return CHICKEN;
        }
        if (animal instanceof Butterfly) {
            return BUTTERFLY;
        }
        if (animal instanceof Cat) {
            return CAT;
        }
        return null;
    }

    public void fixStuckAnimals(GameMap gameMap, List<Animal> animals) {
        for (Animal animal : animals) {
            if (animal.isAnimalStuck(gameMap)) {
                animal.tryToMove(gameMap);
            }
        }
    }

}
