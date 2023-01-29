package base.gameobjects.services;

import base.gameobjects.interactionzones.InteractionZoneStorageChest;
import base.gameobjects.storage.Storage;
import base.gameobjects.storage.StorageCell;
import base.gameobjects.storage.StorageChest;
import base.map.GameMap;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static base.constants.FilePath.STORAGES_DIRECTORY;

public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

    List<String> loadedStorages = new ArrayList<>();

    /**
     * =================================== Loading ======================================
     */

    public void loadStorageChests(GameMap gameMap) {
        for (StorageChest storageChest: gameMap.getStorageChests()) {
            int x = storageChest.getX();
            int y = storageChest.getY();
            String fileName = storageChest.getFileName();
            storageChest.setInteractionZone(new InteractionZoneStorageChest(x + 32, y + 32, 90));
            storageChest.setStorage(new Storage(6, storageChest.getRectangle(), fileName));
            loadStorageChest(storageChest);
        }
    }

    public void loadStorageChest(StorageChest storageChest) {
        File directory = new File(STORAGES_DIRECTORY);
        if (directory.listFiles() == null || directory.listFiles().length == 0) {
            logger.info("Storage chests directory doesn't exist or is empty");
        }
//        storageChest = loadStorageFromJson(storageChest);
        readFile(storageChest);
        loadedStorages.add(storageChest.getFileName());
    }

    private StorageChest loadStorageFromJson(StorageChest storageChest) {
        try {
            Gson gson = new Gson();
            Reader reader = new FileReader(STORAGES_DIRECTORY + storageChest.getFileName() + ".json");
            storageChest = gson.fromJson(reader, StorageChest.class);
            reader.close();
            return storageChest;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void readFile(StorageChest storageChest) {
        File mapFile = new File(STORAGES_DIRECTORY + storageChest.getFileName());
        try (Scanner scanner = new Scanner(mapFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitLine = line.split(":");
                String itemName = splitLine[0];
                int qty = Integer.parseInt(splitLine[1]);
                if (!itemName.startsWith(storageChest.getFileName()) && qty > 0) {
                    storageChest.getStorage().addItem(itemName, qty);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * =================================== Clean up ======================================
     */

    public void cleanUpDisconnectedChests() {
        File directory = new File(STORAGES_DIRECTORY);
        File[] storageFiles = directory.listFiles();
        if (storageFiles == null || storageFiles.length == 0) {
            logger.info("Storage chests directory doesn't exist or is empty");
        }
        List<String> filesToDelete = new ArrayList<>();
        for (File storageFile : storageFiles) {
            if (!loadedStorages.contains(storageFile.getName())) {
                filesToDelete.add(storageFile.getName());
            }
        }
        if (!filesToDelete.isEmpty()) {
            for (String fileName : filesToDelete) {
                File fileToDelete = new File(STORAGES_DIRECTORY + fileName);
                fileToDelete.delete();
            }
        }
    }

    /**
     * =================================== Saving ======================================
     */

    public void saveStorages(List<StorageChest> storageChests) {
        for (StorageChest storageChest : storageChests) {
//            saveStorageAsJson(storageChest);
            saveStorageChest(storageChest);
        }
    }

    private void saveStorageAsJson(StorageChest storageChest) {
        Gson gson = new Gson();
        try {
            File directory = new File(STORAGES_DIRECTORY);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    logger.error("Error while saving storage to json file - cannot create directory");
                }
            }
            FileWriter writer = new FileWriter(STORAGES_DIRECTORY + storageChest.getFileName() + ".json");
            gson.toJson(storageChest, writer);
            writer.flush();
            writer.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void saveStorageChest(StorageChest chest) {
        logger.info("Saving storage chest");
        File file = new File(STORAGES_DIRECTORY + chest.getFileName());
        try {
            if (file.exists()) {
                Files.deleteIfExists(file.toPath());
            }
            if (!file.createNewFile()) {
                logger.error(String.format("Unable to create file: %s", file));
                throw new IllegalArgumentException();
            }
            PrintWriter printWriter = new PrintWriter(file);
            for (StorageCell cell : chest.getStorage().getCells()) {

                printWriter.println(cell.getItemName() + ":" + cell.getObjectCount());
            }
            printWriter.close();

        } catch (IOException e) {
            logger.error("Error while saving storage chest");
            e.printStackTrace();
        }
    }

}
