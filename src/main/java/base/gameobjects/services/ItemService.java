package base.gameobjects.services;

import base.gameobjects.Item;
import base.gameobjects.Wood;
import base.gameobjects.plants.*;
import base.graphicsservice.Sprite;
import base.map.TileService;

import java.util.Arrays;
import java.util.List;

// TODO: don't like this at all
// should not contain plant service
// should have cached sprites
public class ItemService {

    PlantService plantService;

    public static final List<String> STACKABLE_ITEMS = Arrays.asList(Carrot.NAME, Beet.NAME, Tomato.NAME, Strawberry.NAME, Bellpepper.NAME, Corn.NAME, Potato.NAME,
            "seed" + Carrot.NAME, "seed" + Beet.NAME, "seed" + Tomato.NAME, "seed" + Strawberry.NAME, "seed" + Bellpepper.NAME, "seed" + Corn.NAME, "seed" + Potato.NAME,
            "wood");

    public ItemService(PlantService plantService) {
        this.plantService = plantService;
    }

    public Sprite getItemSprite(String itemType, TileService tileService) {
        if (itemType.startsWith("seed")) {
            itemType = itemType.substring(4);
            return plantService.getSeedSprite(itemType);
        }
        else if (PlantService.plantTypes.contains(itemType)) {
            return plantService.getPlantSprite(itemType);
        }
        else if (itemType.equalsIgnoreCase(Wood.itemName)) {
            return tileService.getTiles().get(75).getSprite();
        }
        return null;
    }

    public Item creteNewItem(String itemType, int x, int y) {
        if (itemType.startsWith("seed")) {
            itemType = itemType.substring(4);
            return new Seed(itemType, plantService.getSeedSprite(itemType), x, y);
        }
        else if (PlantService.plantTypes.contains(itemType)) {
            return new Item(x, y, itemType, plantService.getPlantSprite(itemType));
        }
        return null;
    }
}
