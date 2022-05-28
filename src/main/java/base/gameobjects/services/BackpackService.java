package base.gameobjects.services;

import base.Game;
import base.gui.GUI;
import base.gui.GUIButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

import static base.constants.FilePath.BACKPACK_FILE_NAME;

public class BackpackService {

    protected static final Logger logger = LoggerFactory.getLogger(BackpackService.class);

    public void saveBackpack(GUI backpackGui) {
        logger.info("Attempt to save backpack");
        try {
            FileOutputStream outputStream = new FileOutputStream(BACKPACK_FILE_NAME);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(backpackGui);
            objectOutputStream.close();
        } catch (IOException e) {
            logger.error("Unable to save backpack");
        }
    }

    public GUI loadBackpackFromFile() {
        logger.info("Attempt to load backpack");
        try {
            FileInputStream inputStream = new FileInputStream(BACKPACK_FILE_NAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return (GUI) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("No previous backpacks found, will load empty", e);
        }
        return null;
    }

    public boolean isBackPackEmpty(Game game) {
        List<GUIButton> buttons = game.getBackpackGui().getButtons();
        for (GUIButton button : buttons) {
            if (button.getObjectCount() > 0) {
                return false;
            }
        }
        return true;
    }
}
