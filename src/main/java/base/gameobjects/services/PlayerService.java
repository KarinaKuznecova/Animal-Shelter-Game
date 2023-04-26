package base.gameobjects.services;

import base.gameobjects.player.Player;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static base.constants.FilePath.CONFIG_DIRECTORY;
import static base.constants.FilePath.PLAYER_CONFIG_FILE_PATH;

public class PlayerService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    public void saveToFile(Player player) {
        Gson gson = new Gson();
        try {
            File directory = new File(CONFIG_DIRECTORY);
            if (!directory.exists() && !directory.mkdirs()) {
                logger.error("Error while saving player to json file - cannot create config directory");
            }
            FileWriter writer = new FileWriter(PLAYER_CONFIG_FILE_PATH);
            gson.toJson(player, writer);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
