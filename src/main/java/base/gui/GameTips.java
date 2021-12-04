package base.gui;

import base.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameTips {

    protected static final Logger logger = LoggerFactory.getLogger(GameTips.class);

    String tipsFilePath = "config/tips.txt";
    List<String> lines;

    public GameTips() {
        lines = readFromFile();
    }

    private List<String> readFromFile() {
        logger.debug("Reading tips from file");
        List<String> linesFromFile = new ArrayList<>();

        File tipsFile = new File(tipsFilePath);
        if (!tipsFile.exists()) {
            logger.error("File with tips doesn't exist");
            return new ArrayList<>();
        }
        try (Scanner scanner = new Scanner(tipsFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                linesFromFile.add(line);
            }
        } catch (IOException ex) {
            logger.error(String.format("Could not read the file : %s", tipsFilePath));
            return linesFromFile;
        }
        logger.debug("Successfully read tips from file");
        return linesFromFile;
    }

    public List<String> getLines() {
        return lines;
    }
}
