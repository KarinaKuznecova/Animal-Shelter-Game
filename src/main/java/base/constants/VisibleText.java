package base.constants;

import base.gameobjects.Animal;
import base.gameobjects.animals.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static base.constants.FilePath.TRANSLATION_FILE_PATH;

public class VisibleText {

    protected static final Logger logger = LoggerFactory.getLogger(VisibleText.class);

    // keep in alphabetical order
    public static String adoptionDialogPattern = "I want to adopt %s, is it ok?";
    public static String age = "Age";
    public static String backyard = "Backyard";
    public static String bottomCenterMap = "Bottom Center Map";
    public static String bottomLeftMap = "Bottom Left Map";
    public static String bottomRightMap = "Bottom Right Map";
    public static String city = "City";
    public static String energy = "Energy";
    public static String favorite = "Favorite";
    public static String forest = "Forest";
    public static String gardening = "Gardening";
    public static String home = "Home";
    public static String home2 = "Home 2";
    public static String hunger = "Hunger";
    public static String island = "Island";
    public static String location = "Location";
    public static String name = "Name";
    public static String named = "named";
    public static String newName = "New Name";
    public static String secondMap = "Second map";
    public static String startingMap = "Starting map";
    public static String thirst = "Thirst";

    public static Map<String, String> ANIMAL_TYPES;

    // animal types, keep in alphabetical order
    public static String bunny = "Bunny";
    public static String butterfly = "Butterfly";
    public static String cat = "Cat";
    public static String chicken = "Chicken";
    public static String dog = "Dog";
    public static String mouse = "Mouse";
    public static String pig = "Pig";
    public static String rat = "Rat";
    public static String wolf = "Wolf";

    public static String getAdoptionDialog(Animal wantedAnimal) {
        return String.format(adoptionDialogPattern, wantedAnimal);
    }

    public static void initializeTranslations() {
        logger.debug("Reading translations from file");

        File translationFile = new File(TRANSLATION_FILE_PATH);
        if (!translationFile.exists()) {
            logger.error("File with translations doesn't exist");
            return;
        }
        try (Scanner scanner = new Scanner(translationFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("Adoption")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        adoptionDialogPattern = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Age")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        age = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Backyard")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        backyard = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Bottom Center Map")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        bottomCenterMap = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Bottom Left Map")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        bottomLeftMap = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Bottom Right Map")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        bottomRightMap = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Bunny")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        bunny = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Butterfly")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        butterfly = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Cat:")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        cat = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Chicken")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        chicken = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("City")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        city = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Dog")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        dog = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Energy")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        energy = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Favorite")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        favorite = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Forest")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        forest = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Gardening:")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        gardening = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Home 1")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        home = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Home 2")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        home2 = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Hunger")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        hunger = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Island")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        island = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Location")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        location = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Mouse")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        mouse = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Name:")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        name = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Named:")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        named = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("New Name")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        newName = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Pig")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        pig = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Rat")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        rat = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Second map")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        secondMap = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Starting map")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        startingMap = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Thirst")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        thirst = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Wolf")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                        wolf = splitLine[1];
                    }
                    continue;
                }
            }
        } catch (IOException ex) {
            logger.error(String.format("Could not read the file : %s", TRANSLATION_FILE_PATH));
        }

        initializeAnimalTypesTranslations();

        logger.debug("Successfully read translations from file");
    }

    private static void initializeAnimalTypesTranslations() {
        ANIMAL_TYPES = new HashMap<>();
        ANIMAL_TYPES.put(Bunny.TYPE, bunny);
        ANIMAL_TYPES.put(Butterfly.TYPE, butterfly);
        ANIMAL_TYPES.put(Cat.TYPE, cat);
        ANIMAL_TYPES.put(Chicken.TYPE, chicken);
        ANIMAL_TYPES.put(Dog.TYPE, dog);
        ANIMAL_TYPES.put(Mouse.TYPE, mouse);
        ANIMAL_TYPES.put(Pig.TYPE, pig);
        ANIMAL_TYPES.put(Rat.TYPE, rat);
        ANIMAL_TYPES.put(Wolf.TYPE, wolf);
    }

    public static String getAnimalType(String type) {
        return ANIMAL_TYPES.get(type);
    }
}
