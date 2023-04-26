package base.loading;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static base.constants.Constants.*;
import static base.constants.Constants.LANGUAGE_PROPERTY;

public class GamePropertiesLoadingService {

    public void loadGameProperties() {
        Properties gameProperties = new Properties();

        try (FileInputStream input = new FileInputStream("config/application.properties")) {
            gameProperties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DEBUG_MODE = Boolean.parseBoolean(gameProperties.getProperty(DEBUG_MODE_PROPERTY));
        CHEATS_MODE = Boolean.parseBoolean(gameProperties.getProperty(CHEATS_MODE_PROPERTY));
        TEST_MAP_MODE = Boolean.parseBoolean(gameProperties.getProperty(TEST_MAP_PROPERTY));
        LANGUAGE = gameProperties.getProperty(LANGUAGE_PROPERTY);
    }
}
