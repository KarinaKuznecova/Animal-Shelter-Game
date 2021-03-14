import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MapService {

    private File mapListFile = new File("src/main/resources/MapList.txt");
    Map<String, String> mapFiles = new HashMap<>();

    public MapService() {
        System.out.println("Loading maps list");
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

    String getMapConfig(String mapName) {
        return mapFiles.get(mapName);
    }
}
