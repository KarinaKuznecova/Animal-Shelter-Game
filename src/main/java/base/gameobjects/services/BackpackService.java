package base.gameobjects.services;

import base.Game;
import base.graphicsservice.Rectangle;
import base.gui.BackpackButton;
import base.gui.GUI;
import base.gui.GUIButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static base.constants.Constants.*;
import static base.constants.FilePath.BACKPACK_FILE_NAME;
import static base.constants.FilePath.BACKPACK_FILE_PATH;
import static base.gui.GuiService.BACKPACK_COLUMNS;
import static base.gui.GuiService.BACKPACK_ROWS;

public class BackpackService {

    ItemService itemService = new ItemService(new PlantService());

    protected static final Logger logger = LoggerFactory.getLogger(BackpackService.class);

    @Deprecated
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

    public void saveBackpackToFile(GUI backpackGui) {
        logger.info("Attempt to save backpack");

        File backpackFile = new File(BACKPACK_FILE_PATH);

        try {
            if (backpackFile.exists()) {
                Files.deleteIfExists(backpackFile.toPath());
            }
            if (!backpackFile.createNewFile()) {
                logger.error(String.format("Unable to create file: %s", backpackFile));
                throw new IllegalArgumentException();
            }

            PrintWriter printWriter = new PrintWriter(backpackFile);
            printWriter.println("Game version:" + CURRENT_GAME_VERSION);
            printWriter.println("// id, item name, count");

            for (GUIButton button : backpackGui.getButtons()) {
                if (button instanceof BackpackButton) {
                    String defaultId = ((BackpackButton) button).getDefaultId();
                    String itemName = ((BackpackButton) button).getItemName();
                    int count = button.getObjectCount();

                    printWriter.println(defaultId + "," + itemName + "," + count);
                }
            }

            printWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
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

    public GUI loadBackpackFromFile(Game game) {
        logger.info("Attempt to load backpack from normal file");

        List<GUIButton> buttons = new ArrayList<>();

        File backpackFile = new File(BACKPACK_FILE_PATH);
        if (!backpackFile.exists()) {
            GUI migrated = getMigratedBackpack(game);
            if (migrated == null) {
                return getEmptyBackpack(game, buttons);
            } else {
                return migrated;
            }
        }
        try (Scanner scanner = new Scanner(backpackFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("//") || line.startsWith("Game version")) {
                    continue;
                }
                String[] splitLine = line.split(",");
                if (splitLine.length == 3) {
                    String id = splitLine[0];
                    String itemName = splitLine[1];
                    int count = Integer.parseInt(splitLine[2]);

                    int row = Integer.parseInt(String.valueOf(id.charAt(0)));
                    int column = Integer.parseInt(String.valueOf(id.charAt(1)));

                    Rectangle buttonRectangle = new Rectangle(column * (TILE_SIZE * ZOOM + 2), row * (TILE_SIZE * ZOOM + 2), TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
                    BackpackButton button = new BackpackButton(itemName, itemService.getItemSprite(itemName), buttonRectangle, id);
                    button.setObjectCount(count);
                    buttons.add(button);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new GUI(buttons, 5, game.getHeight() - ((BACKPACK_ROWS + 1) * (TILE_SIZE * ZOOM + 2)), true);
    }

    public GUI getMigratedBackpack(Game game) {
        GUI old = loadBackpackFromFile();

        if (old != null) {
            List<GUIButton> buttons = new ArrayList<>();
            GUI newEmpty = getEmptyBackpack(game, buttons);
            for (GUIButton button : old.getButtons()) {
                if (button instanceof BackpackButton) {
                    String defaultId = ((BackpackButton) button).getDefaultId();
                    String itemName = ((BackpackButton) button).getItemName();
                    int count = button.getObjectCount();

                    int row = Integer.parseInt(String.valueOf(defaultId.charAt(0)));
                    int column = Integer.parseInt(String.valueOf(defaultId.charAt(1)));

                    Rectangle buttonRectangle = new Rectangle(column * (TILE_SIZE * ZOOM + 2), row * (TILE_SIZE * ZOOM + 2), TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);

                    boolean buttonExists = false;
                    for (GUIButton emptyButton : newEmpty.getButtons()) {
                        if (emptyButton instanceof BackpackButton && ((BackpackButton) emptyButton).getDefaultId().equalsIgnoreCase(defaultId)) {
                            ((BackpackButton) emptyButton).setItem(itemName);
                            emptyButton.setSprite(itemService.getItemSprite(itemName));
                            emptyButton.setObjectCount(count);
                            buttonExists = true;
                            break;
                        }
                    }
                    if (!buttonExists) {
                        BackpackButton newButton = new BackpackButton(itemName, itemService.getItemSprite(itemName), buttonRectangle, defaultId);
                        newButton.setObjectCount(count);
                        buttons.add(newButton);
                    }
                }
            }
            return newEmpty;
        }
        return null;
    }

    public GUI getEmptyBackpack(Game game, List<GUIButton> buttons) {
        for (int i = 0; i < BACKPACK_ROWS; i++) {
            for (int j = 0; j < BACKPACK_COLUMNS; j++) {
                Rectangle buttonRectangle = new Rectangle(j * (TILE_SIZE * ZOOM + 2), i * (TILE_SIZE * ZOOM + 2), TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
                buttons.add(new BackpackButton(String.valueOf(i) + j, null, buttonRectangle, String.valueOf(i) + j));
            }
        }
        return new GUI(buttons, 5, game.getHeight() - ((BACKPACK_ROWS + 1) * (TILE_SIZE * ZOOM + 2)), true);
    }

    public String getItemNameByButtonId(GUI backpack, String buttonId) {
        for (GUIButton button : backpack.getButtons()) {
            if (button instanceof BackpackButton && ((BackpackButton) button).getDefaultId().equalsIgnoreCase(buttonId)) {
                return ((BackpackButton) button).getItemName();
            }
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
