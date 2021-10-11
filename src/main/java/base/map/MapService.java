package base.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MapService {

    private File mapListFile = new File("src/main/java/base/map/config/MapList.txt");
    Map<String, String> mapFiles = new HashMap<>();

    protected static final Logger logger = LoggerFactory.getLogger(MapService.class);

    public MapService() {
        logger.info("Loading maps list");
        loadMapList();
    }

    private void loadMapList() {
        try {
            Scanner scanner = new Scanner(mapListFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitLine = line.split(":");
                if (splitLine.length >= 1) {
                    mapFiles.put(splitLine[0], splitLine[1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getMapConfig(String mapName) {
        return mapFiles.get(mapName);
    }
}
