package base.loading;

import base.Game;
import base.constants.FilePath;
import base.graphicsservice.AnimatedSprite;
import base.gameobjects.player.Player;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Rectangle;
import base.graphicsservice.SpriteSheet;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import static base.constants.Constants.PLAYER_SPRITE_SIZE;
import static base.constants.FilePath.PLAYER_CONFIG_FILE_PATH;

public class PlayerLoadingService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerLoadingService.class);

    public void loadPlayer(Game game) {
        int startX = game.getWidth() / 2;
        int startY = game.getHeight() / 2;

        File file = new File(PLAYER_CONFIG_FILE_PATH);
        if (file.exists()) {
            Gson gson = new Gson();
            try (Reader reader = new FileReader(PLAYER_CONFIG_FILE_PATH)) {
                Player player = gson.fromJson(reader, Player.class);
                player.setAnimatedSprite(loadPlayerAnimatedImages());
                player.setPlayerRectangle(new Rectangle(startX, startY, PLAYER_SPRITE_SIZE, PLAYER_SPRITE_SIZE));
                player.getRectangle().generateBorder(1, 123);
                player.setSpeed(5);
                game.setPlayer(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private AnimatedSprite loadPlayerAnimatedImages() {
        logger.info("Loading player animations");

        BufferedImage playerSheetImage = ImageLoader.loadImage(FilePath.PLAYER_SHEET_PATH);
        SpriteSheet playerSheet = new SpriteSheet(playerSheetImage);
        playerSheet.loadSprites(PLAYER_SPRITE_SIZE, PLAYER_SPRITE_SIZE, 0);

        return new AnimatedSprite(playerSheet, 5, true);
    }
}
