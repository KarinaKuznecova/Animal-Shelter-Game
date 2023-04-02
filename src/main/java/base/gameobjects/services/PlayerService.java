package base.gameobjects.services;

import base.constants.FilePath;
import base.gameobjects.AnimatedSprite;
import base.gameobjects.player.Player;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Rectangle;
import base.graphicsservice.SpriteSheet;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.*;

import static base.constants.Constants.PLAYER_SPRITE_SIZE;
import static base.constants.FilePath.CONFIG_DIRECTORY;
import static base.constants.FilePath.PLAYER_CONFIG_FILE_PATH;

public class PlayerService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    public void saveToFile(Player player) {
        Gson gson = new Gson();
        try {
            File directory = new File(CONFIG_DIRECTORY);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    logger.error("Error while saving player to json file - cannot create config directory");
                }
            }
            FileWriter writer = new FileWriter(PLAYER_CONFIG_FILE_PATH);
            gson.toJson(player, writer);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Player readFromFile(int startX, int startY) {
        File file = new File(PLAYER_CONFIG_FILE_PATH);
        if (file.exists()) {
            Gson gson = new Gson();
            try (Reader reader = new FileReader(PLAYER_CONFIG_FILE_PATH)) {
                Player player = gson.fromJson(reader, Player.class);
                player.setAnimatedSprite(loadPlayerAnimatedImages());
                player.setPlayerRectangle(new Rectangle(startX, startY, PLAYER_SPRITE_SIZE, PLAYER_SPRITE_SIZE));
                player.getRectangle().generateBorder(1, 123);
                player.setSpeed(5);
                return player;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private AnimatedSprite loadPlayerAnimatedImages() {
        logger.info("Loading player animations");

        BufferedImage playerSheetImage = ImageLoader.loadImage(FilePath.PLAYER_SHEET_PATH);
        SpriteSheet playerSheet = new SpriteSheet(playerSheetImage);
        playerSheet.loadSprites(PLAYER_SPRITE_SIZE, PLAYER_SPRITE_SIZE, 0);

        return new AnimatedSprite(playerSheet, 5, true);
    }
}
