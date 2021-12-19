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

import static base.gameobjects.Animal.MAX_HUNGER;

public class AnimalService {

    public static final String RAT = "rat";
    public static final String MOUSE = "mouse";
    public static final String CHICKEN = "chicken";
    public static final String BUTTERFLY = "butterfly";
    public static final String CAT = "cat";
    public static final String PIG = "pig";
    public static final String BUNNY = "bunny";

    public static final String IMAGES_PATH = "img/";

    List<String> animalNames = Arrays.asList(Rat.NAME, Mouse.NAME, Chicken.NAME, Butterfly.NAME, Cat.NAME, Pig.NAME, Bunny.NAME);

    protected static final Logger logger = LoggerFactory.getLogger(AnimalService.class);

    public Map<String, Sprite> getAnimalPreviewSprites() {
        Map<String, Sprite> previews = new HashMap<>();
        for (String animalName : animalNames) {
            previews.put(animalName, createAnimal(animalName, 1, 1, "", null, MAX_HUNGER).getPreviewSprite());
        }
        return previews;
    }

    public Animal createAnimal(int x, int y, String animalType, String mapName) {
        if (animalType.contains("-")) {
            String[] split = animalType.split("-");
            String name = split[0];
            String color = split[1];
            return createAnimal(name, x, y, mapName, color, MAX_HUNGER);
        }
        return createAnimal(animalType, x, y, mapName, null, MAX_HUNGER);
    }

    public Animal createAnimal(String animalName, int startX, int startY, String mapName, String color, int hunger) {
        Animal animal;
        switch (animalName.toLowerCase()) {
            case RAT:
                animal = new Rat(startX, startY, 3, hunger);
                break;
            case MOUSE:
                animal = new Mouse(startX, startY, 3, hunger);
                break;
            case CHICKEN:
                animal = new Chicken(startX, startY, 3, hunger);
                break;
            case BUTTERFLY:
                animal = new Butterfly(startX, startY, 1, hunger);
                break;
            case CAT:
                animal = new Cat(startX, startY, 3, color, hunger);
                break;
            case PIG:
                animal = new Pig(startX, startY, 3, hunger);
                break;
            case BUNNY:
                animal = new Bunny(startX, startY, 3, hunger);
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
        if (animal instanceof Bunny) {
            return BUNNY;
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

    public void saveAllAnimals(List<Animal> animals) {
        for (Animal animal : animals) {
            saveAnimalToFile(animal);
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

            printWriter.println("CurrentMap:" + animal.getHomeMap());
            printWriter.println("Type:" + getAnimalType(animal));
            printWriter.println("HomeMap:" + animal.getHomeMap());
            printWriter.println("Speed:" + animal.getSpeed());
            if (animal.getColor() != null) {
                printWriter.println("Color:" + animal.getColor());
            }
            printWriter.println("Hunger:" + animal.getCurrentHunger());
            printWriter.println("X:" + animal.getCurrentX());
            printWriter.println("Y:" + animal.getCurrentY());

            printWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFilePath(Animal animal, int id) {
        String path = "animals/" + getAnimalType(animal) + "-" + id;

        File animalFile = new File(path);
        if (animalFile.exists()) {
            path = getFilePath(animal, ++id);
        }
        return path;
    }

    public List<Animal> loadAllAnimals() {
        logger.info("Loading animals from files");
        List<Animal> animalsOnMap = new ArrayList<>();
        File directory = new File("animals/");
        if (directory.listFiles() == null || directory.listFiles().length == 0) {
            logger.info("No animals on this map");
            return animalsOnMap;
        }
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String mapName = "";
            String animalType = null;
            String color = null;
            int speed;
            int hunger = MAX_HUNGER;
            int x = 0;
            int y = 0;
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("CurrentMap:")) {
                        String[] splitLine = line.split(":");
                        mapName = splitLine[1];
                        continue;
                    }
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
                    if (line.startsWith("Color:")) {
                        String[] splitLine = line.split(":");
                        color = splitLine[1];
                        continue;
                    }
                    if (line.startsWith("Hunger:")) {
                        String[] splitLine = line.split(":");
                        hunger = Integer.parseInt(splitLine[1]);
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
                Animal animal = createAnimal(animalType, x, y, mapName, color, hunger);
                animal.setFileName(file.getName());
                animalsOnMap.add(animal);
            }
        }
        return animalsOnMap;
    }

    public void deleteAnimalFiles(List<Animal> animals) {
        logger.info("Deleting all animal files");
        for (Animal animal : animals) {
            File fileToDelete = new File("animals/" + animal.getFileName());
            if (fileToDelete.exists()) {
                boolean success = fileToDelete.delete();
                System.out.println(success);
            }
        }
    }

    public String getNextColor(String animalType) {
        if (animalType.startsWith(Cat.NAME)) {
            if (Cat.colors.size() == Cat.colors.indexOf(animalType) + 1) {
                return Cat.colors.get(0);
            }
            return Cat.colors.get(Cat.colors.indexOf(animalType) + 1);
        }
        return animalType;
    }

    public Sprite getNewColorSprite(String animalType) {
        String color = null;
        if (animalType.contains("-")) {
            String[] split = animalType.split("-");
            color = split[1];
        }
        if (animalType.startsWith(Cat.NAME)) {
            return new Cat(0, 0, 0, color).getPreviewSprite();
        }
        return null;
    }
}
