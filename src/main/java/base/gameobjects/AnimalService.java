package base.gameobjects;

import base.gameobjects.animals.*;
import base.graphicsservice.Sprite;
import base.map.GameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class AnimalService {

    public static final String RAT = "rat";
    public static final String MOUSE = "mouse";
    public static final String CHICKEN = "chicken";
    public static final String BUTTERFLY = "butterfly";
    public static final String CAT = "cat";
    public static final String PIG = "pig";

    Map<Integer, String> animalIdMapping;

    protected static final Logger logger = LoggerFactory.getLogger(AnimalService.class);

    public AnimalService() {
        initializeAnimationMapping();
    }

    void initializeAnimationMapping() {
        animalIdMapping = new HashMap<>();
        animalIdMapping.put(0, Rat.NAME);
        animalIdMapping.put(1, Mouse.NAME);
        animalIdMapping.put(2, Chicken.NAME);
        animalIdMapping.put(3, Butterfly.NAME);
        animalIdMapping.put(4, Cat.NAME);
        animalIdMapping.put(5, Pig.NAME);
    }

    public List<Sprite> getAnimalPreviewSprites() {
        List<Sprite> previews = new ArrayList<>();
        for (int i = 0; i < animalIdMapping.size(); i++) {
            String animalName = animalIdMapping.get(i);
            previews.add(createAnimal(animalName, 1, 1, "").getPreviewSprite());
        }
        return previews;
    }

    public Animal createAnimal(String animalName, int startX, int startY, String mapName) {
        Animal animal;
        switch (animalName.toLowerCase()) {
            case RAT:
                animal = new Rat(startX, startY, 3);
                break;
            case MOUSE:
                animal = new Mouse(startX, startY, 2);
                break;
            case CHICKEN:
                animal = new Chicken(startX, startY, 2);
                break;
            case BUTTERFLY:
                animal = new Butterfly(startX, startY, 1);
                break;
            case CAT:
                animal = new Cat(startX, startY, 2);
                break;
            case PIG:
                animal = new Pig(startX, startY, 3);
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
        if (animal instanceof Pig) {
            return PIG;
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

    private String getFilePath(Animal animal, int id) {
        String path = "animals/" + animal.getHomeMap() + "-" + getAnimalType(animal) + "-" + id;

        File animalFile = new File(path);
        if (animalFile.exists()) {
            path = getFilePath(animal, ++id);
        }
        return path;
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
