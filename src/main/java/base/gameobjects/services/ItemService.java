package base.gameobjects.services;

import base.gameobjects.Item;
import base.gameobjects.plants.Seed;
import base.graphicsservice.Sprite;

public class ItemService {

    PlantService plantService;

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
