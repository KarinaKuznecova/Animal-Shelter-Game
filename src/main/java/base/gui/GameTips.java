package base.gui;

import base.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameTips {

    protected static final Logger logger = LoggerFactory.getLogger(GameTips.class);

    String tipsFilePath = "config/tips.txt";
    List<String> lines;

    public GameTips() {
        try {
            lines = readFromFile();
        } catch (URISyntaxException e) {
            logger.error("Error while reading file from resources ", e);
        }
    }

    private List<String> readFromFile() throws URISyntaxException {
        logger.debug("Reading tips from file");
        List<String> linesFromFile = new ArrayList<>();

        URL resource = Game.class.getResource(tipsFilePath);
        assert resource != null;
        File tipsFile = new File(resource.toURI());
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
