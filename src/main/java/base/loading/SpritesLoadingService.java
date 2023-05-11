package base.loading;

import base.Game;
import base.gameobjects.*;
import base.gameobjects.interactionzones.InteractionZoneKitchen;
import base.gameobjects.services.PlantService;
import base.gameobjects.storage.StorageChest;
import base.gameobjects.tree.Oak;
import base.gameobjects.tree.Spruce;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Sprite;
import base.graphicsservice.SpriteService;
import base.gui.ContextClue;
import base.map.GameMap;
import base.map.TileService;

import static base.constants.ColorConstant.GREEN;
import static base.constants.Constants.CELL_SIZE;
import static base.constants.Constants.CHEST_TILE_ID;
import static base.constants.FilePath.QUESTION_ICON_PATH;
import static base.constants.MapConstants.FOREST_GENERATED_MAP;

public class SpritesLoadingService {

    public void cacheSprites(SpriteService spriteService, PlantService plantService, TileService tileService) {
        spriteService.setPlantPreview(plantService.getPreviews());
        spriteService.setPlantAnimatedSprites(plantService.getAnimatedSprites());
        spriteService.setSeedSprites(plantService.getSeedSprites());

        spriteService.setBowlsSprites();

        spriteService.setStorageChestSprites(tileService.getTiles().get(37).getSprite(), tileService.getTiles().get(36).getSprite());

        spriteService.setFeatherSprite(tileService.getTiles().get(Feather.TILE_ID).getSprite());
        spriteService.setMushroomSprite(tileService.getTiles().get(Mushroom.TILE_ID).getSprite());
        spriteService.setWoodSprite(tileService.getTiles().get(Wood.TILE_ID).getSprite());

        spriteService.loadBushSprite();
        spriteService.loadSpruceSprite();
        spriteService.loadOakSprite();

        for (int cookingStoveId : CookingStove.TILE_IDS) {
            spriteService.loadCookingStoveSprite(cookingStoveId, tileService.getTerrainTiles().get(cookingStoveId).getSprite());
        }

        spriteService.setSimpleMealSprite(tileService.getTiles().get(PetFood.SIMPLE_MEAL_SPRITE_ID).getSprite());
        spriteService.setTastyMealSprite(tileService.getTiles().get(PetFood.TASTY_MEAL_SPRITE_ID).getSprite());
        spriteService.setPerfectMealSprite(tileService.getTiles().get(PetFood.PERFECT_MEAL_SPRITE_ID).getSprite());

        spriteService.setFriendlyIcon(ImageLoader.getPreviewSprite("img/friendly.png"));
        spriteService.setWildIcon(ImageLoader.getPreviewSprite("img/wild.png"));
        spriteService.setHungryIcon(ImageLoader.getPreviewSprite("img/hungry.png"));
        spriteService.setLazyIcon(ImageLoader.getPreviewSprite("img/lazy.png"));
        spriteService.setThirstyIcon(ImageLoader.getPreviewSprite("img/thirsty.png"));
    }

    public void setSpritesToGameMapObjects(Game game, GameMap gameMap) {
        for (Plant plant : gameMap.getPlants()) {
            plant.setPreviewSprite(game.getSpriteService().getPlantPreviewSprite(plant.getPlantType()));
            plant.setAnimatedSprite(game.getSpriteService().getPlantAnimatedSprite(plant.getPlantType()));
        }
        for (Feather feather : gameMap.getFeathers()) {
            feather.setSprite(game.getSpriteService().getFeatherSprite());
        }
        for (Mushroom mushroom : gameMap.getMushrooms()) {
            mushroom.setSprite(game.getSpriteService().getMushroomSprite());
            mushroom.setItemName(Mushroom.ITEM_NAME);
        }
        for (Wood wood : gameMap.getWoods()) {
            wood.setSprite(game.getSpriteService().getWoodSprite());
        }
        for (Item item : gameMap.getItems()) {
            if (item.getItemName() != null && item.getItemName().contains("Meal")) {
                item.setSprite(game.getSpriteService().getMealSprite(item.getItemName()));
            } else {
                item.setSprite(game.getSpriteService().getPlantPreviewSprite(item.getItemName()));
            }
        }
        for (WaterBowl waterBowl : gameMap.getWaterBowls()) {
            waterBowl.setSprite(game.getSpriteService().getWaterBowlAnimatedSprite());
        }
        for (FoodBowl foodBowl : gameMap.getFoodBowls()) {
            foodBowl.setSprite(game.getSpriteService().getFoodBowlAnimatedSprite());
        }
        for (StorageChest storageChest : gameMap.getStorageChests()) {
            storageChest.setSpriteClosed(game.getSpriteService().getClosedChestSprite());
            storageChest.setSpriteOpen(game.getSpriteService().getOpenChestSprite());
            gameMap.setTile(storageChest.getX() / CELL_SIZE, storageChest.getY() / CELL_SIZE, CHEST_TILE_ID, 2, true);
        }
        for (Bush bush : gameMap.getBushes()) {
            bush.setSprite(game.getSpriteService().getBushSprite());
            if (gameMap.getMapName().startsWith(FOREST_GENERATED_MAP)) {
                bush.startOneTimeBush();
            } else {
                bush.startBush();
            }
        }
        for (Oak oak : gameMap.getOaks()) {
            oak.setSprite(game.getSpriteService().getOakSprite());
            oak.getRectangle().generateBorder(1, GREEN);
        }
        for (Spruce spruce : gameMap.getSpruces()) {
            spruce.setSprite(game.getSpriteService().getSpruceSprite());
            spruce.getRectangle().generateBorder(1, GREEN);
        }
        for (CookingStove cookingStove : gameMap.getCookingStoves()) {
            cookingStove.setSprite(game.getSpriteService().getCookingStoveSprite(cookingStove.getTileId()));
            cookingStove.getRectangle().generateBorder(1, GREEN);
            InteractionZoneKitchen interactionZone = new InteractionZoneKitchen(gameMap.getMapName(), cookingStove.getRectangle().getX() + 32, cookingStove.getRectangle().getY() + 32, 290);
            cookingStove.setInteractionZone(interactionZone);
            cookingStove.setContextClue(new ContextClue(new Sprite(ImageLoader.loadImage(QUESTION_ICON_PATH))));
            game.addToInteractionZones(interactionZone);
        }
        for (Fridge fridge : gameMap.getFridges()) {
            fridge.getRectangle().generateBorder(1, GREEN);
            InteractionZoneKitchen interactionZone = new InteractionZoneKitchen(gameMap.getMapName(), fridge.getRectangle().getX() + 32, fridge.getRectangle().getY() + 32, 290);
            fridge.setInteractionZone(interactionZone);
            fridge.setContextClue(new ContextClue(new Sprite(ImageLoader.loadImage(QUESTION_ICON_PATH))));
            game.addToInteractionZones(interactionZone);
        }
    }
}
