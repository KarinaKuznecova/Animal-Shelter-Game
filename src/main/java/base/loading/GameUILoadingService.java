package base.loading;

import base.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static base.constants.Constants.MAX_SCREEN_HEIGHT;
import static base.constants.Constants.MAX_SCREEN_WIDTH;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class GameUILoadingService {

    protected static final Logger logger = LoggerFactory.getLogger(GameUILoadingService.class);

    public void loadUI(Game game, Canvas canvas) {
        setSizeBasedOnScreenSize();
        game.setDefaultCloseOperation(EXIT_ON_CLOSE);
        game.setBounds(0, 0, MAX_SCREEN_WIDTH - 5, MAX_SCREEN_HEIGHT - 5);
        game.setLocationRelativeTo(null);
        game.add(canvas);
        game.setVisible(true);
        game.setTitle("Animal shelter game");
        canvas.createBufferStrategy(3);
    }
    private void setSizeBasedOnScreenSize() {
        GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (GraphicsDevice device : graphicsDevices) {
            if (MAX_SCREEN_WIDTH > device.getDisplayMode().getWidth()) {
                MAX_SCREEN_WIDTH = device.getDisplayMode().getWidth();
            }
            if (MAX_SCREEN_HEIGHT > device.getDisplayMode().getHeight()) {
                MAX_SCREEN_HEIGHT = device.getDisplayMode().getHeight();
            }
        }
        logger.info(String.format("Screen size will be %d by %d", MAX_SCREEN_WIDTH, MAX_SCREEN_HEIGHT));
    }
}
