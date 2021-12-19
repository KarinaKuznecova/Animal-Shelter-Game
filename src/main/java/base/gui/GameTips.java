package base.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static base.constants.FilePath.TIPS_FILE_PATH;

public class GameTips {

    protected static final Logger logger = LoggerFactory.getLogger(GameTips.class);

    List<String> lines;

    public GameTips() {
        lines = readFromFile();
    }

    private List<String> readFromFile() {
        logger.debug("Reading tips from file");
        List<String> linesFromFile = new ArrayList<>();

        File tipsFile = new File(TIPS_FILE_PATH);
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
            logger.error(String.format("Could not read the file : %s", TIPS_FILE_PATH));
            return linesFromFile;
        }
        logger.debug("Successfully read tips from file");
        return linesFromFile;
    }

    public List<String> getLines() {
        return lines;
    }
}
