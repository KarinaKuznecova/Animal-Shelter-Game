package base.gameobjects.services;

import base.gameobjects.Item;
import base.gameobjects.plants.*;
import base.graphicsservice.Sprite;

import java.util.Arrays;
import java.util.List;

public class ItemService {

    PlantService plantService;

    public static final List<String> STACKABLE_ITEMS = Arrays.asList(Carrot.NAME, Beet.NAME, Tomato.NAME, Strawberry.NAME, Bellpepper.NAME,
            "seed" + Carrot.NAME, "seed" + Beet.NAME, "seed" + Tomato.NAME, "seed" + Strawberry.NAME, "seed" + Bellpepper.NAME, "seed" + Corn.NAME);

    public ItemService(PlantService plantService) {
        this.plantService = plantService;
    }

    public Sprite getItemSprite(String itemType) {
        if (itemType.startsWith("seed")) {
            itemType = itemType.substring(4);
            return plantService.getSeedSprite(itemType);
        }
        else if (plantService.plantTypes.contains(itemType)) {
            return plantService.getPlantSprite(itemType);
        }
        return null;
    }

    public Item creteNewItem(String itemType, int x, int y) {
        if (itemType.startsWith("seed")) {
            itemType = itemType.substring(4);
            return new Seed(itemType, plantService.getSeedSprite(itemType), x, y);
        }
        else if (plantService.plantTypes.contains(itemType)) {
            return new Item(x, y, itemType, plantService.getPlantSprite(itemType));
        }
        return null;
    }
}
