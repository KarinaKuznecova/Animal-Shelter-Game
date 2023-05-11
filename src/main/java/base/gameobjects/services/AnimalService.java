package base.gameobjects.services;

import base.Game;
import base.gameobjects.AgeStage;
import base.gameobjects.Animal;
import base.gameobjects.animals.*;
import base.gameobjects.animaltraits.Trait;
import base.graphicsservice.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static base.constants.Constants.*;
import static base.constants.FilePath.ANIMALS_DIR_PATH;
import static base.constants.MapConstants.*;
import static base.gameobjects.AgeStage.ADULT;
import static base.gameobjects.AgeStage.BABY;
import static base.gameobjects.animaltraits.Trait.WILD;

public class AnimalService {

    public static final List<String> ANIMAL_TYPES = Arrays.asList(Rat.TYPE, Mouse.TYPE, Chicken.TYPE, Butterfly.TYPE, Cat.TYPE, Pig.TYPE, Bunny.TYPE, Dog.TYPE, Wolf.TYPE);
    private final Random random = new Random();

    protected static final Logger logger = LoggerFactory.getLogger(AnimalService.class);
    private final AnimalNamingService animalNamingService = new AnimalNamingService();

    public Map<String, Sprite> getAnimalPreviewSprites() {
        Map<String, Sprite> previews = new HashMap<>();
        for (String animalName : ANIMAL_TYPES) {
            previews.put(animalName, createAnimal(animalName, 1, 1, "", null, MAX_HUNGER, MAX_THIRST, MAX_ENERGY, ADULT, "").getPreviewSprite());
        }
        return previews;
    }

    public Animal createNewAnimal(int x, int y, String animalType, String mapName) {
        Animal newAnimal;
        if (animalType.contains("-")) {
            String[] split = animalType.split("-");
            String name = split[0];
            String color = split[1];
            newAnimal = createAnimal(name, x, y, mapName, color, MAX_HUNGER, MAX_THIRST, MAX_ENERGY, BABY, animalNamingService.getRandomName(random.nextBoolean()));
        } else {
            newAnimal = createAnimal(animalType, x, y, mapName, null, MAX_HUNGER, MAX_THIRST, MAX_ENERGY, BABY, animalNamingService.getRandomName(random.nextBoolean()));
        }
        newAnimal.getPersonality().add(Trait.values()[random.nextInt(Trait.values().length)]);
        return newAnimal;
    }

    private Animal createAnimal(String animalType, int startX, int startY, String mapName, String color, int hunger, int thirst, int energy, AgeStage age, String name) {
        Animal animal;
        switch (animalType.toLowerCase()) {
            case Rat.TYPE:
                animal = new Rat(startX, startY, 2, color, hunger, thirst, energy, age, name);
                break;
            case Mouse.TYPE:
                animal = new Mouse(startX, startY, 2, hunger, thirst, energy, age, name);
                break;
            case Chicken.TYPE:
                animal = new Chicken(startX, startY, 2, hunger, thirst, energy, age, name);
                break;
            case Butterfly.TYPE:
                animal = new Butterfly(startX, startY);
                break;
            case Cat.TYPE:
                animal = new Cat(startX, startY, 2, color, hunger, thirst, energy, age, name);
                break;
            case Pig.TYPE:
                animal = new Pig(startX, startY, 2, hunger, thirst, energy, age, name);
                break;
            case Bunny.TYPE:
                animal = new Bunny(startX, startY, 2, hunger, thirst, energy, age, name);
                break;
            case Dog.TYPE:
                animal = new Dog(startX, startY, 2, hunger, thirst, energy, age, name);
                break;
            case Wolf.TYPE:
                animal = new Wolf(startX, startY, 2, hunger, thirst, energy, age, name);
                break;
            default:
                logger.error(String.format("Unknown animal requested or animal not defined : %s", animalType));
                throw new IllegalArgumentException();
        }
        setCurrentMapName(mapName, animal);
        return animal;
    }

    private void setCurrentMapName(String mapName, Animal animal) {
        if (MAIN_MAP.equalsIgnoreCase(mapName) || HOME_MAP.equalsIgnoreCase(mapName) || TOP_LEFT_MAP.equalsIgnoreCase(mapName) || FOREST_MAP.equalsIgnoreCase(mapName) || CITY_MAP.equalsIgnoreCase(mapName)) {
            animal.setCurrentMap(mapName);
        } else {
            animal.setCurrentMap(MAIN_MAP);
        }
        if (TEST_MAP_MODE && TEST_MAP.equals(mapName)) {
            animal.setCurrentMap(TEST_MAP);
        }
    }

    public String getAnimalType(Animal animal) {
        if (animal.getAnimalType().contains("-")) {
            return animal.getAnimalType().substring(0, animal.getAnimalType().indexOf("-"));
        }
        return animal.getAnimalType();
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
            File directory = new File(ANIMALS_DIR_PATH);
            if (!directory.exists() && !directory.mkdir()) {
                logger.error(String.format("Unable to create file: %s", animalFile));
                throw new IllegalArgumentException();
            }
            if (!animalFile.createNewFile()) {
                logger.error(String.format("Unable to create file: %s", animalFile));
                throw new IllegalArgumentException();
            }

            PrintWriter printWriter = new PrintWriter(animalFile);

            printWriter.println("Game version: " + CURRENT_GAME_VERSION);

            printWriter.println("Type:" + getAnimalType(animal));
            printWriter.println("Name:" + animal.getName());
            printWriter.println("CurrentMap:" + animal.getCurrentMap());
            printWriter.println("Speed:" + animal.getSpeed());
            if (animal.getColor() != null) {
                printWriter.println("Color:" + animal.getColor());
            }
            printWriter.println("Hunger:" + animal.getCurrentHunger());
            printWriter.println("Thirst:" + animal.getCurrentThirst());
            printWriter.println("Energy:" + animal.getCurrentEnergy());
            printWriter.println("Age:" + animal.getAge());
            printWriter.println("CurrentAge:" + animal.getCurrentAge());
            printWriter.println("Favorite:" + animal.isFavorite());
            printWriter.println("X:" + animal.getCurrentX());
            printWriter.println("Y:" + animal.getCurrentY());
            printWriter.println("Personality:" + animal.getPersonality());

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
            String name = "";
            int hunger = MAX_HUNGER;
            int thirst = MAX_THIRST;
            int energy = MAX_ENERGY;
            AgeStage age = ADULT;
            int currentAge = GROWING_UP_TIME;
            boolean favorite = false;
            int x = 0;
            int y = 0;
            List<Trait> traits = new ArrayList<>();
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
                    if (line.startsWith("Energy:")) {
                        String[] splitLine = line.split(":");
                        energy = Integer.parseInt(splitLine[1]);
                        continue;
                    }
                    if (line.startsWith("Age:")) {
                        String[] splitLine = line.split(":");
                        String ageString = splitLine[1];
                        age = AgeStage.valueOf(ageString);
                        continue;
                    }
                    if (line.startsWith("CurrentAge:")) {
                        String[] splitLine = line.split(":");
                        currentAge = Integer.parseInt(splitLine[1]);
                        continue;
                    }
                    if (line.startsWith("Name:")) {
                        String[] splitLine = line.split(":");
                        if (splitLine.length > 1) {
                            name = splitLine[1];
                        }
                        continue;
                    }
                    if (line.startsWith("Favorite:")) {
                        String[] splitLine = line.split(":");
                        favorite = Boolean.parseBoolean(splitLine[1]);
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
                        continue;
                    }
                    if (line.startsWith("Personality:")) {
                        String[] splitLine = line.split(":");
                        String traitsLine = splitLine[1];
                        traitsLine = traitsLine.replace('[', ' ');
                        traitsLine = traitsLine.replace(']', ' ');
                        traitsLine = traitsLine.strip();
                        String[] splitTraits = traitsLine.split(",");
                        for (String splitTrait : splitTraits) {
                            String trait = splitTrait;
                            trait = trait.strip();
                            if (!trait.isEmpty()) {
                                traits.add(Trait.valueOf(trait));
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (animalType != null) {
                Animal animal = createAnimal(animalType, x, y, mapName, color, hunger, thirst, energy, age, name);
                animal.setPersonality(traits);
                animal.setCurrentAge(currentAge);
                animal.setFavorite(favorite);
                animal.setFileName(file.getName());
                if (TEST_MAP_MODE) {
                    if (TEST_MAP.equals(animal.getCurrentMap())) {
                        animalsOnMap.add(animal);
                    }
                } else {
                    if (!TEST_MAP.equals(animal.getCurrentMap())) {
                        animalsOnMap.add(animal);
                    }
                }
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
        if (animalType.startsWith(Cat.TYPE)) {
            if (CAT_COLORS.size() == CAT_COLORS.indexOf(animalType) + 1) {
                return CAT_COLORS.get(0);
            }
            return CAT_COLORS.get(CAT_COLORS.indexOf(animalType) + 1);
        }
        if (animalType.startsWith(Rat.TYPE)) {
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
        if (animalType.startsWith(Cat.TYPE)) {
            return new Cat(0, 0, 0, color).getPreviewSprite();
        }
        if (animalType.startsWith(Rat.TYPE)) {
            return new Rat(0, 0, 0, color).getPreviewSprite();
        }
        return null;
    }

    public String getRandomAnimalType() {
        int animalId = random.nextInt(ANIMAL_TYPES.size());
        String animalType = ANIMAL_TYPES.get(animalId);
        if (Butterfly.TYPE.equalsIgnoreCase(animalType)) {
            logger.debug("Skipping butterfly");
            return getRandomAnimalType();
        }
        if (animalId == ANIMAL_TYPES.indexOf(Cat.TYPE)) {
            int catType = random.nextInt(CAT_COLORS.size());
            animalType = CAT_COLORS.get(catType);
        }
        if (animalId == ANIMAL_TYPES.indexOf(Rat.TYPE)) {
            int ratType = random.nextInt(RAT_COLORS.size());
            animalType = RAT_COLORS.get(ratType);
        }
        return animalType;
    }

    public Animal pickAvailableAnimal(Game game) {
        List<Animal> availableAnimals = new ArrayList<>();
        for (List<Animal> animalList : game.getAnimalsOnMaps().values()) {
            for (Animal animal : animalList) {
                if (isAnimalAvailableForAdoption(animal)) {
                    availableAnimals.add(animal);
                }
            }
        }
        if (availableAnimals.isEmpty()) {
            return null;
        }
        return availableAnimals.get(random.nextInt(availableAnimals.size()));
    }


    public boolean isAnimalAvailableForAdoption(Animal animal) {
        return animal != null && !animal.isFavorite() && !animal.getPersonality().contains(WILD);
    }
}
