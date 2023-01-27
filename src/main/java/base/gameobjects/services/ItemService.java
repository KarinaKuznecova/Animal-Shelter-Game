package base.gameobjects.services;

import base.gameobjects.Feather;
import base.gameobjects.Item;
import base.gameobjects.Mushroom;
import base.gameobjects.Wood;
import base.gameobjects.plants.*;
import base.graphicsservice.Sprite;
import base.graphicsservice.SpriteService;
import base.map.TileService;

import java.util.Arrays;
import java.util.List;

// TODO: don't like this at all, should not contain plant service, should have cached sprites, issue #344
public class ItemService {

    PlantService plantService;

    public static final List<String> STACKABLE_ITEMS = Arrays.asList(Carrot.NAME, Beet.NAME, Tomato.NAME, Strawberry.NAME, Bellpepper.NAME, Corn.NAME, Potato.NAME,
            "seed" + Carrot.NAME, "seed" + Beet.NAME, "seed" + Tomato.NAME, "seed" + Strawberry.NAME, "seed" + Bellpepper.NAME, "seed" + Corn.NAME, "seed" + Potato.NAME,
            Wood.ITEM_NAME, Feather.ITEM_NAME, Mushroom.ITEM_NAME);

    public ItemService(PlantService plantService) {
        this.plantService = plantService;
    }

    public Sprite getItemSprite(String itemType, TileService tileService) {
        if (itemType != null && itemType.startsWith("seed")) {
            itemType = itemType.substring(4);
            return plantService.getSeedSprite(itemType);
        }
        else if (PlantService.plantTypes.contains(itemType)) {
            return plantService.getPlantSprite(itemType);
        }
        else if (itemType.equalsIgnoreCase(Wood.ITEM_NAME)) {
            return tileService.getTiles().get(Wood.TILE_ID).getSprite();
        }
        else if (itemType.equalsIgnoreCase(Feather.ITEM_NAME)) {
            return tileService.getTiles().get(Feather.TILE_ID).getSprite();
        }
        else if (itemType.equalsIgnoreCase(Mushroom.ITEM_NAME)) {
            return tileService.getTiles().get(Mushroom.TILE_ID).getSprite();
        }
        return null;
    }

    public Item createNewItem(SpriteService spriteService, String itemType, int x, int y) {
        Item item = createNewItem(itemType, x, y);
        item.setSprite(spriteService.getPlantPreviewSprite(itemType));
        return item;
    }

    public Item createNewItem(String itemType, int x, int y) {
        if (itemType.startsWith("seed")) {
            itemType = itemType.substring(4);
            return new Seed(itemType, x, y);
        }
        else if (PlantService.plantTypes.contains(itemType)) {
            return new Item(x, y, itemType);
        }
        return null;
    }
}
