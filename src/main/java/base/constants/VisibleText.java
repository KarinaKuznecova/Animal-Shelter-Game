package base.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static base.constants.FilePath.TRANSLATION_FILE_PATH;

public class VisibleText {

    protected static final Logger logger = LoggerFactory.getLogger(VisibleText.class);

    public static String age = "Age";
    public static String backyard = "Backyard";
    public static String bottomCenterMap = "Bottom Center Map";
    public static String bottomLeftMap = "Bottom Left Map";
    public static String bottomRightMap = "Bottom Right Map";
    public static String energy = "Energy";
    public static String home = "Home";
    public static String home2 = "Home 2";
    public static String hunger = "Hunger";
    public static String island = "Island";
    public static String location = "Location";
    public static String name = "Name";
    public static String newName = "New Name";
    public static String secondMap = "Second map";
    public static String startingMap = "Starting map";
    public static String thirst = "Thirst";

    public VisibleText() {
        initializeTranslations();
    }

    public void initializeTranslations() {
        logger.debug("Reading translations from file");

        File translationFile = new File(TRANSLATION_FILE_PATH);
        if (!translationFile.exists()) {
            logger.error("File with translations doesn't exist");
            return;
        }
        try (Scanner scanner = new Scanner(translationFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
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
                if (line.startsWith("Energy")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                    energy = splitLine[1];
                    }
                    continue;
                }
                if (line.startsWith("Home")) {
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
                if (line.startsWith("Name")) {
                    String[] splitLine = line.split(":");
                    if (splitLine.length > 1) {
                    name = splitLine[1];
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
            }
        } catch (IOException ex) {
            logger.error(String.format("Could not read the file : %s", TRANSLATION_FILE_PATH));
        }
        logger.debug("Successfully read tips from file");
    }
}
