package base.gameobjects.services;

import base.Game;
import base.gui.Backpack;
import base.gui.BackpackButton;
import base.gui.GUI;
import base.gui.GUIButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

import static base.constants.Constants.CURRENT_GAME_VERSION;
import static base.constants.FilePath.BACKPACK_FILE_PATH;

public class BackpackService {

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
