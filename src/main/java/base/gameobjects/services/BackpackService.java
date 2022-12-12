package base.gameobjects.services;

import base.Game;
import base.graphicsservice.Rectangle;
import base.gui.Backpack;
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

import static base.constants.Constants.CELL_SIZE;
import static base.constants.Constants.CURRENT_GAME_VERSION;
import static base.constants.FilePath.BACKPACK_FILE_PATH;
import static base.gui.GuiService.BACKPACK_COLUMNS;
import static base.gui.GuiService.BACKPACK_ROWS;

public class BackpackService {

    ItemService itemService = new ItemService(new PlantService());

    protected static final Logger logger = LoggerFactory.getLogger(BackpackService.class);

    private BackpackButton inHand;

    public void saveBackpackToFile(Backpack backpackGui) {
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
            printWriter.println("money:" + backpackGui.getCoins());
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

    public Backpack loadBackpackFromFile() {
        logger.info("Attempt to load backpack");
        try {
            FileInputStream inputStream = new FileInputStream("backpack.bag");
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return (Backpack) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("No previous backpacks found, will load empty");
        }
        return null;
    }

    public Backpack loadBackpackFromFile(Game game) {
        logger.info("Attempt to load backpack from normal file");

        List<GUIButton> buttons = new ArrayList<>();
        int coins = 0;

        File backpackFile = new File(BACKPACK_FILE_PATH);
        if (!backpackFile.exists()) {
            Backpack migrated = getMigratedBackpack(game);
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
                if (line.startsWith("money")) {
                    String[] splitLine = line.split(":");
                    coins = Integer.parseInt(splitLine[1]);
                }
                String[] splitLine = line.split(",");
                if (splitLine.length == 3) {
                    String id = splitLine[0];
                    String itemName = splitLine[1];
                    int count = Integer.parseInt(splitLine[2]);

                    int row = Integer.parseInt(String.valueOf(id.charAt(0)));
                    int column = Integer.parseInt(String.valueOf(id.charAt(1)));

                    Rectangle buttonRectangle = new Rectangle(column * (CELL_SIZE + 2), row * (CELL_SIZE + 2), CELL_SIZE, CELL_SIZE);
                    BackpackButton button = new BackpackButton(itemName, itemService.getItemSprite(itemName), buttonRectangle, id);
                    button.setObjectCount(count);
                    buttons.add(button);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new Backpack(buttons, 5, game.getHeight() - ((BACKPACK_ROWS + 1) * (CELL_SIZE + 2)), coins);
    }

    public Backpack getMigratedBackpack(Game game) {
        Backpack old = loadBackpackFromFile();

        if (old != null) {
            List<GUIButton> buttons = new ArrayList<>();
            Backpack newEmpty = getEmptyBackpack(game, buttons);
            for (GUIButton button : old.getButtons()) {
                if (button instanceof BackpackButton) {
                    String defaultId = ((BackpackButton) button).getDefaultId();
                    String itemName = ((BackpackButton) button).getItemName();
                    int count = button.getObjectCount();

                    int row = Integer.parseInt(String.valueOf(defaultId.charAt(0)));
                    int column = Integer.parseInt(String.valueOf(defaultId.charAt(1)));

                    Rectangle buttonRectangle = new Rectangle(column * (CELL_SIZE + 2), row * (CELL_SIZE + 2), CELL_SIZE, CELL_SIZE);

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

    public Backpack getEmptyBackpack(Game game, List<GUIButton> buttons) {
        for (int i = 0; i < BACKPACK_ROWS; i++) {
            for (int j = 0; j < BACKPACK_COLUMNS; j++) {
                Rectangle buttonRectangle = new Rectangle(j * (CELL_SIZE + 2), i * (CELL_SIZE + 2), CELL_SIZE, CELL_SIZE);
                buttons.add(new BackpackButton(String.valueOf(i) + j, null, buttonRectangle, String.valueOf(i) + j));
            }
        }
        return new Backpack(buttons, 5, game.getHeight() - ((BACKPACK_ROWS + 1) * (CELL_SIZE + 2)), 0);
    }

    public String getItemNameByButtonId(GUI backpack, String buttonId) {
        for (GUIButton button : backpack.getButtons()) {
            if (button instanceof BackpackButton && ((BackpackButton) button).getDefaultId().equalsIgnoreCase(buttonId) && !buttonId.equals(((BackpackButton) button).getItemName())) {
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

    public void removeAllItemsFromButton(BackpackButton button) {
        if (button == null) {
            return;
        }
        button.setObjectCount(0);
        button.makeEmpty();
    }

    public BackpackButton getItemFromHand() {
        return inHand;
    }

    public void putItemInHand(BackpackButton inHand) {
        if (inHand != null) {
            logger.info(String.format("%s is in hand", inHand.getItemName()));
        }
        this.inHand = inHand;
    }
}
