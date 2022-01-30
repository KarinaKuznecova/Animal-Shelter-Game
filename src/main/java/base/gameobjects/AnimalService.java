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

import static base.constants.Constants.*;
import static base.constants.FilePath.ANIMALS_DIR_PATH;
import static base.gameobjects.Animal.MAX_HUNGER;
import static base.gameobjects.Animal.MAX_THIRST;

public class AnimalService {

    public List<String> animalNames = Arrays.asList(Rat.NAME, Mouse.NAME, Chicken.NAME, Butterfly.NAME, Cat.NAME, Pig.NAME, Bunny.NAME);

    protected static final Logger logger = LoggerFactory.getLogger(AnimalService.class);

    public Map<String, Sprite> getAnimalPreviewSprites() {
        Map<String, Sprite> previews = new HashMap<>();
        for (String animalName : animalNames) {
            previews.put(animalName, createAnimal(animalName, 1, 1, "", null, MAX_HUNGER, MAX_THIRST).getPreviewSprite());
        }
        return previews;
    }

    public Animal createAnimal(int x, int y, String animalType, String mapName) {
        if (animalType.contains("-")) {
            String[] split = animalType.split("-");
            String name = split[0];
            String color = split[1];
            return createAnimal(name, x, y, mapName, color, MAX_HUNGER, MAX_THIRST);
        }
        return createAnimal(animalType, x, y, mapName, null, MAX_HUNGER, MAX_THIRST);
    }

    public Animal createAnimal(String animalName, int startX, int startY, String mapName, String color, int hunger, int thirst) {
        Animal animal;
        switch (animalName.toLowerCase()) {
            case Rat.NAME:
                animal = new Rat(startX, startY, 3, color, hunger, thirst);
                break;
            case Mouse.NAME:
                animal = new Mouse(startX, startY, 3, hunger, thirst);
                break;
            case Chicken.NAME:
                animal = new Chicken(startX, startY, 3, hunger, thirst);
                break;
            case Butterfly.NAME:
                animal = new Butterfly(startX, startY, 1, hunger, thirst);
                break;
            case Cat.NAME:
                animal = new Cat(startX, startY, 3, color, hunger, thirst);
                break;
            case Pig.NAME:
                animal = new Pig(startX, startY, 3, hunger, thirst);
                break;
            case Bunny.NAME:
                animal = new Bunny(startX, startY, 3, hunger, thirst);
                break;
            default:
                logger.error(String.format("Unknown animal requested or animal not defined : %s", animalName));
                throw new IllegalArgumentException();
        }
        animal.setCurrentMap(mapName);
        return animal;
    }

    public String getAnimalType(Animal animal) {
        if (animal.getAnimalName().contains("-")) {
            return animal.getAnimalName().substring(0, animal.getAnimalName().indexOf("-"));
        }
        return animal.getAnimalName();
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
        String path = getFilePath(animal, 0);

        File animalFile = new File(path);
        try {
            if (!animalFile.createNewFile()) {
                logger.error(String.format("Unable to create file: %s", animalFile));
                throw new IllegalArgumentException();
            }

            PrintWriter printWriter = new PrintWriter(animalFile);

            printWriter.println("Game version: " + CURRENT_GAME_VERSION);

            printWriter.println("Type:" + getAnimalType(animal));
            printWriter.println("CurrentMap:" + animal.getCurrentMap());
            printWriter.println("Speed:" + animal.getSpeed());
            if (animal.getColor() != null) {
                printWriter.println("Color:" + animal.getColor());
            }
            printWriter.println("Hunger:" + animal.getCurrentHunger());
            printWriter.println("Thirst:" + animal.getCurrentThirst());
            printWriter.println("X:" + animal.getCurrentX());
            printWriter.println("Y:" + animal.getCurrentY());

            printWriter.close();

            animal.setFileName(animalFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFilePath(Animal animal, int id) {
        String path = ANIMALS_DIR_PATH + getAnimalType(animal) + "-" + id;

        File animalFile = new File(path);
        if (animalFile.exists()) {
            path = getFilePath(animal, ++id);
        }
        return path;
    }

    public List<Animal> loadAllAnimals() {
        logger.info("Loading animals from files");
        List<Animal> animalsOnMap = new ArrayList<>();
        File directory = new File(ANIMALS_DIR_PATH);
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
            int thirst = MAX_THIRST;
            int x = 0;
            int y = 0;
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("HomeMap:") || line.startsWith("CurrentMap")) {
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
                    if (line.startsWith("Thirst:")) {
                        String[] splitLine = line.split(":");
                        thirst = Integer.parseInt(splitLine[1]);
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
                Animal animal = createAnimal(animalType, x, y, mapName, color, hunger, thirst);
                animal.setFileName(file.getName());
                animalsOnMap.add(animal);
            }
        }
        return animalsOnMap;
    }

    public void deleteAnimalFiles(Animal animal) {
        logger.info("Deleting animal file");
        List<Animal> animalList = new ArrayList<>();
        animalList.add(animal);
        deleteAnimalFiles(animalList);
    }

    public void deleteAnimalFiles(List<Animal> animals) {
        for (Animal animal : animals) {
            File fileToDelete = new File(ANIMALS_DIR_PATH + animal.getFileName());
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
        }
    }

    public String getNextColor(String animalType) {
        if (animalType.startsWith(Cat.NAME)) {
            if (CAT_COLORS.size() == CAT_COLORS.indexOf(animalType) + 1) {
                return CAT_COLORS.get(0);
            }
            return CAT_COLORS.get(CAT_COLORS.indexOf(animalType) + 1);
        }
        if (animalType.startsWith(Rat.NAME)) {
            if (RAT_COLORS.size() == RAT_COLORS.indexOf(animalType) + 1) {
                return RAT_COLORS.get(0);
            }
            return RAT_COLORS.get(RAT_COLORS.indexOf(animalType) + 1);
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
        if (animalType.startsWith(Rat.NAME)) {
            return new Rat(0, 0, 0, color).getPreviewSprite();
        }
        return null;
    }
}
