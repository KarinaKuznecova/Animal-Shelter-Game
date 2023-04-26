package base.gui;

import base.graphicsservice.Position;
import base.graphicsservice.RenderHandler;
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

    private final List<String> lines;

    private final int xPosition;
    private final int yPosition;

    public GameTips(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
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

    public void render(RenderHandler renderHandler) {
        int interval = 20;
        int spacingToSides = 40;
        int buttonHeight = 60;
        for (int i = 0; i < getLines().size(); i++) {
            int yPos = yPosition + spacingToSides + buttonHeight + (interval * i);
            int xPos = xPosition + spacingToSides;
            renderHandler.renderText(getLines().get(i), new Position(xPos, yPos));
        }
    }
}
