package base.gameobjects;

import base.gui.GUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class BackpackService {

    protected static final Logger logger = LoggerFactory.getLogger(BackpackService.class);

    public void saveBackpack(GUI backpackGui) {
        logger.info("Attempt to save backpack");
        try {
            FileOutputStream outputStream = new FileOutputStream("backpack.bag");
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
            FileInputStream inputStream = new FileInputStream("backpack.bag");
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return (GUI) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("No previous backpacks found, will load empty");
        }
        return null;
    }
}
