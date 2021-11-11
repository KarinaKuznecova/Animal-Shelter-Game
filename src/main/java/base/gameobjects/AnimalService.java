package base.gameobjects;

import base.gameobjects.animals.*;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Sprite;
import base.graphicsservice.SpriteSheet;
import base.map.GameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
    Map<Integer, String> animalIdMapping;
    List<Animal> allAnimals;

    ImageLoader imageLoader = new ImageLoader();

    protected static final Logger logger = LoggerFactory.getLogger(AnimalService.class);

    public AnimalService() {
        initializeAnimationMapping();
        allAnimals = new ArrayList<>();
    }

    void initializeAnimationMapping() {
        animalAnimations = new HashMap<>();
        animalIdMapping = new HashMap<>();
        animalAnimations.put(RAT, RAT_SHEET_PATH);
        animalIdMapping.put(0, RAT);
        animalAnimations.put(MOUSE, MOUSE_SHEET_PATH);
        animalIdMapping.put(1, MOUSE);
        animalAnimations.put(CHICKEN, CHICKEN_SHEET_PATH);
        animalIdMapping.put(2, CHICKEN);
        animalAnimations.put(BUTTERFLY, BUTTERFLY_SHEET_PATH);
        animalIdMapping.put(3, BUTTERFLY);
        animalAnimations.put(CAT, CAT_SHEET_PATH);
        animalIdMapping.put(4, CAT);
        animalAnimations.put(CAT2, CAT_SHEET_PATH2);
        animalIdMapping.put(5, CAT2);
    }

    public List<Animal> getListOfAnimals() {
        return allAnimals;
    }

    public List<Animal> getPossibleAnimals() {
        List<Animal> animalList = new ArrayList<>();
        for (int i = 0; i < animalIdMapping.size(); i++) {
            String animalName = animalIdMapping.get(i);
            animalList.add(createAnimal(animalName, getAnimatedSprite(animalName), 1, 1, ""));
        }
        return animalList;
    }

    public String getAnimalSheetPath(String animalName) {
        return animalAnimations.get(animalName);
    }

    public AnimatedSprite getAnimatedSprite(String animalName) {
        BufferedImage sheetImage = imageLoader.loadImage(getAnimalSheetPath(animalName));
        SpriteSheet animalSheet = new SpriteSheet(sheetImage);
        animalSheet.loadSprites(TILE_SIZE, TILE_SIZE, 0);
        return new AnimatedSprite(animalSheet, 9, false);
    }

    public Animal createAnimal(String animalName, int startX, int startY, String mapName) {
        AnimatedSprite sprite = getAnimatedSprite(animalName);
        return createAnimal(animalName, sprite, startX, startY, mapName);
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

    public String getAnimalNameById(int id) {
        return animalIdMapping.get(id);
    }

    public void fixStuckAnimals(GameMap gameMap, List<Animal> animals) {
        for (Animal animal : animals) {
            if (animal.isAnimalStuck(gameMap)) {
                animal.tryToMove(gameMap);
            }
        }
    }

    private String getFilePath(Animal animal, int id) {
        String path = "animals/" + animal.getHomeMap() + "-" + getAnimalType(animal) + "-" + id;

        File animalFile = new File(path);
        if (animalFile.exists()) {
            path = getFilePath(animal, ++id);
        }
        return path;
    }

    public void saveAnimalToFile(Animal animal) {
        logger.info("Saving animal to file");

        String path = getFilePath(animal, 0);

        File animalFile = new File(path);
        try {
            if (!animalFile.createNewFile()) {
                logger.error(String.format("Unable to create file: %s", animalFile));
                throw new IllegalArgumentException();
            }

            PrintWriter printWriter = new PrintWriter(animalFile);

            printWriter.println("Type:" + getAnimalType(animal));
            printWriter.println("HomeMap:" + animal.getHomeMap());
            printWriter.println("Speed:" + animal.getSpeed());
            printWriter.println("CanTravel:"); //should be filled in scope of #issue24
            printWriter.println("X:" + animal.getCurrentX());
            printWriter.println("Y:" + animal.getCurrentY());

            printWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Animal> loadAnimalsFromFile(String mapName) {
        logger.info("Loading animals from files");
        List<Animal> animalsOnMap = new ArrayList<>();
        File directory = new File("animals/");
        if (directory.listFiles() == null || directory.listFiles().length == 0) {
            logger.info("No animals on this map");
            return animalsOnMap;
        }
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith(mapName)) {
                String animalType = null;
                int speed;
                int x = 0;
                int y = 0;
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.startsWith("Type:")) {
                            String[] splitLine = line.split(":");
                            animalType = splitLine[1];
                            continue;
                        }
                        if (line.startsWith("Speed:")) {
                            String[] splitLine = line.split(":");
                            speed = Integer.parseInt(splitLine[1]);
                            continue;
                        }
                        if (line.startsWith("X:")) {
                            String[] splitLine = line.split(":");
                            x = Integer.parseInt(splitLine[1]);
                            continue;
                        }
                        if (line.startsWith("Y:")) {
                            String[] splitLine = line.split(":");
                            y = Integer.parseInt(splitLine[1]);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (animalType != null) {
                    Animal animal = createAnimal(animalType, x, y, mapName);
                    animalsOnMap.add(animal);
                }
            }
        }
        return animalsOnMap;
    }

}
