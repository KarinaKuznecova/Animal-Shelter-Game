package base.gameobjects.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import static base.constants.Constants.LANGUAGE;
import static base.constants.FilePath.FEMALE_NAMES_FILE_PATH;
import static base.constants.FilePath.MALE_NAMES_FILE_PATH;

public class AnimalNamingService {

    private List<String> femaleNamesList = new ArrayList<>();
    private List<String> maleNamesList = new ArrayList<>();
    private Random random = new Random();

    public AnimalNamingService(){
        cacheNames();
    }

    private void cacheNames()  {
        readJsonFile(FEMALE_NAMES_FILE_PATH, true);
        readJsonFile(MALE_NAMES_FILE_PATH, false);

    }
    private void readJsonFile(String path, boolean female){
        try(BufferedReader reader = new BufferedReader(new FileReader(path))){
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            List<String> names = new ArrayList<>();
            for (JsonElement element : jsonObject.get(LANGUAGE).getAsJsonArray()) {
                names.add(element.getAsString());
            }
            if (female) {
                femaleNamesList.addAll(names);
            } else {
                maleNamesList.addAll(names);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getRandomName(boolean female) {
        if (female) {
            return femaleNamesList.get(random.nextInt(femaleNamesList.size()));
        } else {
            return maleNamesList.get(random.nextInt(maleNamesList.size()));
        }
    }


}
