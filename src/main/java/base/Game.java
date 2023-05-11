package base;

import base.constants.MapConstants;
import base.constants.VisibleText;
import base.events.EventService;
import base.gameobjects.*;
import base.gameobjects.interactionzones.InteractionZone;
import base.gameobjects.interactionzones.InteractionZoneKitchen;
import base.gameobjects.npc.*;
import base.gameobjects.plants.Seed;
import base.gameobjects.player.Player;
import base.gameobjects.services.*;
import base.gameobjects.storage.StorageCell;
import base.gameobjects.storage.StorageChest;
import base.graphicsservice.*;
import base.graphicsservice.Rectangle;
import base.gui.*;
import base.gui.cookingmenu.CookingMenu;
import base.gui.shop.ShopService;
import base.loading.LoadingService;
import base.map.GameMap;
import base.map.MapService;
import base.map.TileService;
import base.mapgenerator.ForestMapGenerator;
import base.navigationservice.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static base.constants.Constants.*;
import static base.constants.MapConstants.*;
import static base.gameobjects.services.ItemService.STACKABLE_ITEMS;
import static base.navigationservice.NavigationService.getNextPortalToGetToCenter;
import static base.navigationservice.RouteCalculator.*;

public class Game extends JFrame implements Runnable {

    private final Canvas canvas = new Canvas();

    protected static final Logger logger = LoggerFactory.getLogger(Game.class);
    private final Random random = new Random();

    private transient GameMap gameMap;
    private transient Map<String, GameMap> gameMaps;
    private transient List<GameObject> gameObjectsList;
    private transient List<GameObject> guiList;
    private transient Map<String, List<Plant>> plantsOnMaps;
    private transient Map<String, List<Animal>> animalsOnMaps;
    private transient List<InteractionZone> interactionZones;

    private transient Player player;

    // Services
    private transient RenderHandler renderer;
    private transient TileService tileService;
    private transient AnimalService animalService;
    private transient PlantService plantService;
    private transient ItemService itemService;
    private transient GuiService guiService;
    private transient BackpackService backpackService;
    private transient RouteCalculator routeCalculator;
    private transient MapService mapService;
    private transient EventService eventService;
    private transient SpriteService spriteService;
    private transient StorageService storageService;
    private transient ShopService shopService;
    private transient PlayerService playerService;
    private final transient LoadingService loadingService;

    // Gui
    private transient GUI[] tileButtonsArray;
    private transient GUI[] terrainButtonsArray;
    private transient GUI yourAnimalsPanel;
    private transient GUI possibleAnimalPanel;
    private transient GUI plantsGui;
    private transient Backpack backpackGui;
    private transient DialogBox dialogBox;
    private transient EscMenu escMenu;
    private transient CookingMenu cookingMenu;

    // Selected items
    private boolean regularTiles = true;

    private int selectedTileId = -1;
    private String selectedAnimal = "";
    private Animal selectedYourAnimal = null;
    private String selectedPlant = "";
    private int selectedPanel = 1;
    private String selectedItem = "";

    private boolean paused;
    private boolean done;

    private final transient KeyboardListener keyboardListener = new KeyboardListener(this);
    private final transient MouseEventListener mouseEventListener = new MouseEventListener(this);

        Environment environment;

    public Game() {
        loadingService = new LoadingService();
        loadGameProperties();
        initializeServices();
        cacheSprites();
        loadUI();
        loadControllers();
        loadGameMap();
        loadGuiElements();
        enableDefaultGui();
        loadGameObjects();
        if (isEnvironmentOn()) {
//        environment = new Environment(this);
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        Thread gameThread = new Thread(game);
        gameThread.start();
    }

    /**
     * =================================== Loading ======================================
     */

    private void loadGameProperties() {
        loadingService.getGamePropertiesLoadingService().loadGameProperties();
    }

    private void initializeServices() {
        tileService = new TileService();
        animalService = new AnimalService();
        plantService = new PlantService();
        itemService = new ItemService();
        guiService = new GuiService();
        backpackService = new BackpackService();
        routeCalculator = new RouteCalculator();
        mapService = new MapService();
        plantsOnMaps = new ConcurrentHashMap<>();
        animalsOnMaps = new ConcurrentHashMap<>();
        interactionZones = new CopyOnWriteArrayList<>();
        gameMaps = new ConcurrentHashMap<>();
        eventService = new EventService();
        spriteService = new SpriteService();
        storageService = new StorageService();
        shopService = new ShopService();
        playerService = new PlayerService();
        VisibleText.initializeTranslations();
    }

    private void cacheSprites() {
        loadingService.getSpritesLoadingService().cacheSprites(spriteService, plantService, tileService);
    }

    private void loadUI() {
        loadingService.getGameUILoadingService().loadUI(this, canvas);
        renderer = new RenderHandler(getWidth(), getHeight());
    }

    private void loadControllers() {
        loadingService.getControllersLoadingService().loadControllers(this, canvas, renderer);
    }

    private void loadGameMap() {
        gameMap = loadingService.getGameMapLoadingService().loadMap(this);
    }

    private void loadGuiElements() {
        loadingService.getGuiElementsLoadingService().loadGuiElements(this);
    }

    private void loadGameObjects() {
        gameObjectsList = new CopyOnWriteArrayList<>();
        loadingService.getPlayerLoadingService().loadPlayer(this);
        loadingService.getShopLoadingService().loadShop(this);
        loadingService.getNpcLoadingService().loadVendorNpc(this);
        cacheAllPlants();
    }

    private void cacheAllPlants() {
        logger.info("Caching plants");
        for (GameMap map : gameMaps.values()) {
            plantsOnMaps.put(map.getMapName(), map.getPlants());
        }
        logger.info("Caching plants finished");
    }

    /**
     * =================================== Defaults ======================================
     */

    private void enableDefaultGui() {
        deselectEverything();

        guiList = new CopyOnWriteArrayList<>();
        guiList.add(tileButtonsArray[0]);
        guiList.add(yourAnimalsPanel);
    }

    private void deselectEverything() {
        changeTile(-1);
        changeYourAnimal(null);
        changeSelectedPlant("");
        deselectBagItem();
    }

    public void changeTile(int tileId) {
        deselectBagItem();
        logger.info(String.format("changing tile to new tile : %d", tileId));
        selectedTileId = tileId;
    }

    public void changeYourAnimal(Animal animal) {
        deselectBagItem();
        deselectTile();
        if (animal == null) {
            logger.info("changing your selected animal to null");
        } else {
            logger.info(String.format("changing your selected animal to : %s", animal));
        }
        selectedYourAnimal = animal;
    }

    public void changeSelectedPlant(String plantType) {
        deselectBagItem();
        deselectTile();
        logger.info(String.format("changing your selected plant to : %s", plantType));
        selectedPlant = plantType;
    }

    public void deselectItem() {
        changeSelectedItem("");
    }

    public void changeSelectedItem(String item) {
        deselectAnimal();
        deselectTile();
        deselectPlant();
        logger.info(String.format("changing your selected item to : %s", item));
        selectedItem = item;
    }

    public void changeSelectedItem(String item, BackpackButton button) {
        deselectAnimal();
        deselectTile();
        deselectPlant();
        logger.info(String.format("changing your selected item to : %s", item));
        selectedItem = item;
        backpackService.putItemInHand(button);
    }

    private void deselectBagItem() {
        selectedItem = "";
        backpackService.putItemInHand(null);
    }

    private void deselectTile() {
        selectedTileId = -1;
    }

    public void deselectAnimal() {
        selectedYourAnimal = null;
    }

    private void deselectPlant() {
        selectedPlant = "";
    }

    /**
     * =================================== RUN && RENDER && UPDATE ======================================
     */

    public void run() {
        long lastTime = System.nanoTime(); //long 2^63
        double nanoSecondConversion = 1000000000.0 / 60; //60 frames per second
        double changeInSeconds = 0;

        while (!done) {
            long now = System.nanoTime();

            changeInSeconds += (now - lastTime) / nanoSecondConversion;
            while (changeInSeconds >= 1) {
                if (!paused) {
                    update();
                } else {
                    updatePausedElements();
                }
                changeInSeconds--;
            }

            render();
            lastTime = now;
        }
    }

    private void render() {
        BufferStrategy bufferStrategy = canvas.getBufferStrategy();
        Graphics graphics = bufferStrategy.getDrawGraphics();
        super.paint(graphics);

        renderer.renderMap(this, gameMap);

        for (GameObject gameObject : guiList) {
            gameObject.render(renderer, ZOOM);
        }

        renderer.render(this, graphics);

        if (isEnvironmentOn()) {
            environment.draw(graphics);
        }

        graphics.dispose();
        bufferStrategy.show();

        renderer.clear();
    }

    private void update() {
        for (GameObject object : gameObjectsList) {
            object.update(this);
        }
        for (GameObject gui : guiList) {
            gui.update(this);
        }
        for (List<Animal> animals : animalsOnMaps.values()) {
            Iterator<Animal> animalIterator = animals.iterator();
            while (animalIterator.hasNext()) {
                animalIterator.next().update(this);
            }
        }
        Iterator<GameObject> gameObjectIterator = gameMap.getGameMapObjects().iterator();
        while (gameObjectIterator.hasNext()) {
            gameObjectIterator.next().update(this);
        }
        for (GameMap map : gameMaps.values()) {
            Iterator<GameObject> iterator = map.getGameMapObjects().iterator();
            while (iterator.hasNext()) {
                iterator.next().update(this);
            }
        }
        eventService.update(this);
    }

    private void updatePausedElements() {
        for (GameObject gui : guiList) {
            gui.update(this);
        }
    }

    /**
     * =================================== Load another map ======================================
     */

    public void loadSecondaryMap(Portal portal) {
        logger.info("Game map loading started");
        String mapName = portal.getDirection();

        String previousMapName = gameMap.getMapName();
        logger.debug(String.format("Previous map name: %s", previousMapName));

        if (mapName.startsWith(FOREST_GENERATED_MAP)) {
            ForestMapGenerator mapGenerator = new ForestMapGenerator(this);
            gameMap = mapGenerator.generateMap(40, 40, mapName);
            loadingService.getGameMapLoadingService().finalizeLoadingMap(this, gameMap);
        } else if (getGameMap(mapName) == null) {
            gameMap = loadingService.getGameMapLoadingService().loadMap(this, mapName);
        } else {
            gameMap = getGameMap(mapName);
        }

        logger.info(String.format("Game map %s loaded", gameMap.getMapName()));

        Portal portalToPrevious = mapService.getPortalTo(gameMap, previousMapName);
        adjustPlayerPosition(portalToPrevious);
        if (previousMapName.startsWith(FOREST_GENERATED_MAP)) {
            List<Animal> animals = animalsOnMaps.get(previousMapName);
            if (animals != null && !animals.isEmpty()) {
                for (Animal animal : animals) {
                    moveAnimalToAnotherMap(animal, portal);
                }
            }
            gameMaps.remove(previousMapName);
        }
        saveMaps();
        refreshCurrentMapCache();
        renderer.adjustCamera(this, player);
        refreshGuiPanels();
    }

    public void saveMaps() {
        renderer.setTextToDraw("...saving game...", 40);

        refreshCurrentMapCache();
        plantsOnMaps.put(gameMap.getMapName(), gameMap.getPlants());
        for (GameMap map : gameMaps.values()) {
            if (map.getMapName().startsWith(FOREST_GENERATED_MAP)) {
                continue;
            }
            mapService.saveMapToJson(map);
            storageService.saveStorages(map.getStorageChests());
        }

        for (List<Animal> animals : animalsOnMaps.values()) {
            if (animals.isEmpty()) {
                continue;
            }
            logger.info("Deleting all animal files");
            animalService.deleteAnimalFiles(animals);

            logger.info("Saving animals to file");
            animalService.saveAllAnimals(animals);
        }
        backpackService.saveBackpackToFile(backpackGui);
        tileService.saveTilesAsJson();
        playerService.saveToFile(player);
    }

    public void refreshCurrentMapCache() {
        gameMaps.put(gameMap.getMapName(), gameMap);
    }

    private void adjustPlayerPosition(Portal portalToPrevious) {
        if (portalToPrevious != null) {
            int previousMapPortalX = mapService.getSpawnPoint(portalToPrevious, true, player.getDirection(), gameMap);
            int previousMapPortalY = mapService.getSpawnPoint(portalToPrevious, false, player.getDirection(), gameMap);
            logger.info(String.format("Will teleport player to x: %d, y: %d", previousMapPortalX, previousMapPortalY));
            player.teleportTo(previousMapPortalX, previousMapPortalY);
        } else {
            player.teleportToCenter(this);
        }
    }

    void refreshGuiPanels() {
        boolean backpackOpen = guiList.contains(backpackGui);
        boolean dialogBoxOpen = guiList.contains(dialogBox);
        guiList.clear();
        loadingService.getGuiElementsLoadingService().loadYourAnimals(this);
        switchTopPanel(selectedPanel);
        if (backpackOpen) {
            guiList.add(backpackGui);
        }
        if (dialogBoxOpen) {
            guiList.add(dialogBox);
        }
    }

    public void moveAnimalToAnotherMap(Animal animal, Portal portal) {
        String destination = portal.getDirection();

        String previousMap = animal.getCurrentMap();
        animalsOnMaps.get(animal.getCurrentMap()).remove(animal);
        animalsOnMaps.get(destination).add(animal);

        animalService.deleteAnimalFiles(animal);

        animal.setCurrentMap(destination);
        adjustAnimalPosition(animal, previousMap);

        animalService.saveAnimalToFile(animal);

        refreshCurrentMapCache();
        refreshGuiPanels();
    }

    private void adjustAnimalPosition(Animal animal, String previousMap) {
        Portal portalToPrevious = mapService.getPortalTo(gameMaps.get(animal.getCurrentMap()), previousMap);
        if (portalToPrevious != null) {
            int previousMapPortalX = mapService.getSpawnPoint(portalToPrevious, true, animal.getDirection(), gameMaps.get(animal.getCurrentMap()));
            int previousMapPortalY = mapService.getSpawnPoint(portalToPrevious, false, animal.getDirection(), gameMaps.get(animal.getCurrentMap()));
            animal.teleportAnimalTo(previousMapPortalX, previousMapPortalY);
            Route routeToAdjust = new Route();
            routeToAdjust.addStep(animal.getDirection());
            animal.setRoute(routeToAdjust);
            animal.setRoute(routeToAdjust);
        } else {
            animal.teleportAnimalTo(getWidth() / 2, getHeight() / 2);
        }
    }

    /**
     * =================================== Interact with Gui panels ======================================
     */

    public void hideGuiPanels() {
        if (guiList.isEmpty()) {
            switchTopPanel(selectedPanel);
        } else {
            guiList.clear();
            renderer.clearRenderedText();
        }
    }

    public void switchTopPanel(int panelId) {
        logger.info(String.format("Switching panels to id: %d", panelId));

        boolean backpackOpen = guiList.contains(backpackGui);

        selectedPanel = panelId;

        renderer.clearRenderedText();
        if (isNewAnimalsPanel(panelId)) {
            if (CHEATS_MODE) {
                showNewAnimalsPanel();
            }
        } else if (isPlantsPanel(panelId)) {
            if (CHEATS_MODE) {
                showNewPlantsPanel();
            }
        } else if (regularTiles) {
            showNewTilesPanel(panelId, tileButtonsArray);
        } else {
            showNewTilesPanel(panelId, terrainButtonsArray);
        }
        showYourAnimals();
        if (backpackOpen) {
            guiList.add(backpackGui);
        }
    }

    private boolean isNewAnimalsPanel(int panelId) {
        return panelId == 0;
    }

    private boolean isPlantsPanel(int panelId) {
        return panelId == 10;
    }

    private void showYourAnimals() {
        if (!guiList.contains(yourAnimalsPanel)) {
            if (yourAnimalsPanel == null) {
                loadingService.getGuiElementsLoadingService().loadYourAnimals(this);
            }
            guiList.add(yourAnimalsPanel);
        }
    }

    private void showNewPlantsPanel() {
        if (!guiList.contains(plantsGui)) {
            deselectTile();
            guiList.clear();
            guiList.add(plantsGui);
        }
    }

    private void showNewAnimalsPanel() {
        if (!guiList.contains(possibleAnimalPanel)) {
            guiList.clear();
            guiList.add(possibleAnimalPanel);
            regularTiles = true;
        }
    }

    private void showNewTilesPanel(int panelId, GUI[] tileButtonsArray) {
        if (tileButtonsArray[panelId - 1] != null && !guiList.contains(tileButtonsArray[panelId - 1])) {
            guiList.clear();
            guiList.add(tileButtonsArray[panelId - 1]);
        }
    }

    public void openTerrainMenu() {
        if (!CHEATS_MODE) {
            return;
        }
        guiList.clear();

        if (regularTiles) {
            regularTiles = false;
            logger.info("Opening terrain menu");
            selectedPanel = 1;
            guiList.add(terrainButtonsArray[0]);
        } else {
            regularTiles = true;
            logger.info("Switching terrain menu to regular menu");
            selectedPanel = 1;
            guiList.add(tileButtonsArray[0]);
        }
        deselectTile();
        showYourAnimals();
    }

    public void switchBackpack() {
        if (guiList.contains(backpackGui)) {
            guiList.remove(backpackGui);
            changeSelectedItem("");
            renderer.clearRenderedText();
        } else {
            guiList.add(backpackGui);
        }
    }

    public void openBackpack() {
        if (!guiList.contains(backpackGui)) {
            guiList.add(backpackGui);
        }
    }

    public void closeBackpack() {
        if (guiList.contains(backpackGui)) {
            guiList.remove(backpackGui);
            changeSelectedItem("");
            renderer.clearRenderedText();
        }
    }

    public void changeAnimal(String animalType) {
        deselectBagItem();
        deselectTile();
        logger.info(String.format("changing selected animal to : %s", animalType));
        selectedAnimal = animalType;
    }

    public void openShopMenu(GUI shop) {
        if (!guiList.contains(shop)) {
            guiList.add(shop);
        }
    }

    public void closeShopMenu(GUI shop) {
        if (guiList.contains(shop)) {
            guiList.remove(shop);
            renderer.clearRenderedText();
        }
    }

    public void showCookingMenu() {
        if (!isCookingMenuOpen()) {
            cookingMenu.updateCookingSkill(player.getSkills().getCookingSkill().getCurrentLevel());
            guiList.add(cookingMenu);
            if (!guiList.contains(backpackGui)) {
                openBackpack();
            }
        } else {
            hideCookingMenu();
            closeBackpack();
        }
    }

    public boolean isCookingMenuOpen() {
        return guiList.contains(cookingMenu);
    }

    public void hideCookingMenu() {
        guiList.remove(cookingMenu);
    }

    /**
     * =================================== Left Mouse Click ======================================
     */

    public void leftClick(int xScreenRelated, int yScreenRelated) {
        int xMapRelated = xScreenRelated + renderer.getCamera().getX();
        int yMapRelated = yScreenRelated + renderer.getCamera().getY();
        logger.debug(String.format("Click on x: %d", xMapRelated));
        logger.debug(String.format("Click on y: %d", yMapRelated));
        Rectangle mouseRectangle = new Rectangle(xScreenRelated, yScreenRelated, 1, 1);
        boolean stoppedChecking = false;

        for (GameObject gameObject : guiList) {
            if (!stoppedChecking) {
                stoppedChecking = gameObject.handleMouseClick(mouseRectangle, renderer.getCamera(), ZOOM, this);
            }
        }
        if (!stoppedChecking) {
            deselectAnimal();
        }
        for (GameObject gameObject : getGameMap().getGameMapObjects()) {
            if (!stoppedChecking) {
                Rectangle newMouseRectangle = new Rectangle(xMapRelated - TILE_SIZE, yMapRelated - TILE_SIZE, TILE_SIZE, TILE_SIZE);
                stoppedChecking = gameObject.handleMouseClick(newMouseRectangle, renderer.getCamera(), ZOOM, this);
            }
        }
        for (Animal animal : animalsOnMaps.get(gameMap.getMapName())) {
            if (!stoppedChecking) {
                mouseRectangle = new Rectangle(xMapRelated - TILE_SIZE, yMapRelated - TILE_SIZE, TILE_SIZE, TILE_SIZE);
                stoppedChecking = animal.handleMouseClick(mouseRectangle, renderer.getCamera(), ZOOM, this);
            }
        }
        if (!stoppedChecking) {
            int smallerX = (int) Math.floor(xMapRelated / (32.0 * ZOOM));
            int smallerY = (int) Math.floor(yMapRelated / (32.0 * ZOOM));
            if (guiList.contains(plantsGui)) {
                createNewPlant(selectedPlant, smallerX, smallerY);
            } else if (guiList.contains(possibleAnimalPanel)) {
                createNewAnimal(smallerX, smallerY);
            } else {
                setNewTile(xMapRelated, yMapRelated, smallerX, smallerY);
            }
        }
    }

    private void setNewTile(int xMapRelated, int yMapRelated, int smallerX, int smallerY) {
        if (selectedTileId != -1) {
            if (player.getRectangle().intersects(xMapRelated, yMapRelated, TILE_SIZE, TILE_SIZE)) {
                logger.warn("Can't place tile under player");
            } else {
                int xAligned = xMapRelated - (xMapRelated % CELL_SIZE);
                int yAligned = yMapRelated - (yMapRelated % CELL_SIZE);
                int layer = tileService.getLayerById(selectedTileId, regularTiles);
                if (selectedTileId == BOWL_TILE_ID) {
                    FoodBowl foodBowl = new FoodBowl(xAligned, yAligned, spriteService.getFoodBowlAnimatedSprite());
                    getGameMap().addFoodBowl(foodBowl);
                } else if (selectedTileId == WATER_BOWL_TILE_ID) {
                    WaterBowl waterBowl = new WaterBowl(xAligned, yAligned, spriteService.getWaterBowlAnimatedSprite());
                    getGameMap().addWaterBowl(waterBowl);
                } else if (selectedTileId == CHEST_TILE_ID) {
                    StorageChest chest = new StorageChest(xAligned, yAligned, spriteService.getClosedChestSprite(), spriteService.getOpenChestSprite());
                    getGameMap().addStorageChest(chest);
                    gameMap.setTile(smallerX, smallerY, CHEST_TILE_ID, layer, regularTiles);
                } else if (CookingStove.TILE_IDS.contains(selectedTileId)) {
                    CookingStove cookingStove = new CookingStove(gameMap.getMapName(), xAligned, yAligned, spriteService.getCookingStoveSprite(selectedTileId), selectedTileId);
                    getGameMap().addObject(cookingStove);
                    interactionZones.add(cookingStove.getInteractionZone());
                    gameMap.setTile(smallerX, smallerY, selectedTileId, layer, regularTiles);
                } else if (Fridge.TILE_ID == selectedTileId) {
                    Fridge fridge = new Fridge(gameMap.getMapName(), xAligned, yAligned);
                    getGameMap().addObject(fridge);
                    interactionZones.add(fridge.getInteractionZone());
                    gameMap.setTile(smallerX, smallerY, selectedTileId, layer, regularTiles);
                } else {
                    gameMap.setTile(smallerX, smallerY, selectedTileId, layer, regularTiles);
                }
            }
        }
        String selectedItemInInventory = backpackService.getItemNameByButtonId(backpackGui, selectedItem);
        if (selectedItemInInventory != null) {
            logger.info(String.format("Will put item on the ground - %s", selectedItemInInventory));
            putItemOnTheGround(xMapRelated, yMapRelated, selectedItemInInventory);
        }
    }

    private void putItemOnTheGround(int xAdjusted, int yAdjusted, String selectedItem) {
        putItemOnTheGround(xAdjusted, yAdjusted, selectedItem, false);
    }

    private void putItemOnTheGround(int xAdjusted, int yAdjusted, String itemType, boolean justDrop) {
        int xAligned = xAdjusted - (xAdjusted % CELL_SIZE);
        int yAligned = yAdjusted - (yAdjusted % CELL_SIZE);
        if (Wood.ITEM_NAME.equalsIgnoreCase(itemType)) {
            Wood wood = new Wood(xAligned, yAligned, spriteService.getWoodSprite());
            gameMap.addObject(wood);
            guiService.decreaseNumberOnButton(this, getSelectedButton());
            return;
        }
        if (Feather.ITEM_NAME.equalsIgnoreCase(itemType)) {
            Feather feather = new Feather(xAligned, yAligned, spriteService.getFeatherSprite());
            gameMap.addObject(feather);
            guiService.decreaseNumberOnButton(this, getSelectedButton());
            return;
        }
        if (Mushroom.ITEM_NAME.equalsIgnoreCase(itemType)) {
            Mushroom mushroom = new Mushroom(xAligned, yAligned, spriteService.getMushroomSprite());
            gameMap.addObject(mushroom);
            guiService.decreaseNumberOnButton(this, getSelectedButton());
            return;
        }
        Item item = itemService.createNewItem(spriteService, itemType, xAligned, yAligned);
        if (item instanceof Seed) {
            if (justDrop || !createNewPlant(((Seed) item).getPlantType(), xAligned / 64, yAligned / 64)) {
                gameMap.addItem(item);
            }
        } else {
            gameMap.addItem(item);
        }
        guiService.decreaseNumberOnButton(this, getSelectedButton());
    }

    public void createNewAnimal(int x, int y) {
        if (selectedAnimal.isEmpty()) {
            return;
        }
        if (getAnimalCount() >= ANIMAL_LIMIT) {
            logger.warn("Too many animals, can't add new");
            return;
        }
        int tileX = x * CELL_SIZE;
        int tileY = y * CELL_SIZE;
        Animal newAnimal = animalService.createNewAnimal(tileX, tileY, selectedAnimal, gameMap.getMapName());
        animalsOnMaps.get(gameMap.getMapName()).add(newAnimal);
        addAnimalToPanel(newAnimal);
    }

    public void addAnimalToPanel(Animal animal) {
        int i = yourAnimalsPanel.getButtonCount();
        Rectangle tileRectangle = new Rectangle(this.getWidth() - (CELL_SIZE + TILE_SIZE), i * (CELL_SIZE + 2), CELL_SIZE, CELL_SIZE);

        AnimalIcon animalIcon = new AnimalIcon(this, animal, animal.getPreviewSprite(), tileRectangle);
        yourAnimalsPanel.addButton(animalIcon);
    }

    private boolean createNewPlant(String plantType, int x, int y) {
        if (plantType.isEmpty()) {
            return false;
        }
        int tileX = x * CELL_SIZE;
        int tileY = y * CELL_SIZE;
        if (mapService.isThereGrassOrDirt(gameMap, tileX, tileY) && mapService.isPlaceEmpty(gameMap, 1, tileX, tileY) && mapService.isInsideOfMap(gameMap, x, y)) {
            Plant plant = plantService.createPlant(spriteService, plantType, tileX, tileY);
            gameMap.addPlant(plant);
            player.getSkills().getGardeningSkill().getExperienceSmall(getRenderer());
            return true;
        }
        return false;
    }

    public void pickUpPlant(Plant plant) {
        GUIButton button = backpackGui.getButtonBySprite(plant.getPreviewSprite());
        if (button == null) {
            putItemOnTheGround(plant.getRectangle().getX(), plant.getRectangle().getY(), plant.getPlantType(), true);
        } else {
            int amount = player.getSkills().getGardeningSkill().getHarvestedAmount();
            pickUp(plant.getPlantType(), plant.getPreviewSprite(), button, amount);
        }

        int seedAmount = player.getSkills().getGardeningSkill().getSeedsAmount();
        Sprite seedSprite = spriteService.getSeedSprite(plant.getPlantType());
        GUIButton buttonForSeed = backpackGui.getButtonBySprite(seedSprite);
        if (buttonForSeed == null) {
            putItemOnTheGround(plant.getRectangle().getX() + (ZOOM * TILE_SIZE), plant.getRectangle().getY(), "seed" + plant.getPlantType(), true);
        } else {
            pickUp("seed" + plant.getPlantType(), seedSprite, buttonForSeed, seedAmount);
        }
        if (plant.isRefreshable() && player.getSkills().getGardeningSkill().keepPlant()) {
            plant.setGrowingStage(1);
            plant.setGrowingTicks(0);
        } else {
            gameMap.removePlant(plant);
            List<Plant> plantList = plantsOnMaps.get(getGameMap().getMapName());
            plantList.remove(plant);
        }
        if (plant.isWild()) {
            player.getSkills().getGardeningSkill().getExperienceMedium(getRenderer());
        } else {
            player.getSkills().getGardeningSkill().getExperienceSmall(getRenderer());
        }
    }

    public void pickUpItem(String itemName, Sprite sprite, Rectangle rectangle) {
        GUIButton button = backpackGui.getButtonBySprite(sprite);
        if (pickUp(itemName, sprite, button, 1)) {
            gameMap.removeItem(itemName, rectangle);
        }
    }

    public void getItem(String itemName, Sprite sprite, int amount) {
        GUIButton button = backpackGui.getButtonBySprite(sprite);
        pickUp(itemName, sprite, button, amount);
    }

    private boolean pickUp(String itemName, Sprite sprite, GUIButton button, int amount) {
        int limit = STACKABLE_ITEMS.contains(itemName) ? INVENTORY_LIMIT : 1;
        if (limit == 1) {
            button = backpackGui.getEmptyButton();
        }
        if (button instanceof BackpackButton) {
            logger.info("found a slot in backpack");
            if (button.getSprite() == null) {
                logger.info("slot was empty, will put plant");
                button.setSprite(sprite);
                if (amount <= limit) {
                    button.setObjectCount(amount);
                } else {
                    button.setObjectCount(limit);
                    pickUp(itemName, sprite, backpackGui.getButtonBySprite(sprite), amount - limit);
                }
                ((BackpackButton) button).setItem(itemName);
            } else {
                logger.info("item is already in backpack, will increment");
                int newAmount = button.getObjectCount() + amount;
                if (newAmount <= limit) {
                    button.setObjectCount(newAmount);
                } else {
                    button.setObjectCount(limit);
                    pickUp(itemName, sprite, backpackGui.getButtonBySprite(sprite), newAmount - limit);
                }
            }
        } else {
            logger.info("No empty slots in backpack");
            return false;
        }
        return true;
    }

    public void pickUpCoins(Coin coin) {
        backpackGui.addCoins(coin.getAmount());
        getGameMap().removeObject(coin);
    }

    public void removeItemFromInventory(String itemName) {
        guiService.decreaseNumberOnButton(this, (BackpackButton) backpackGui.findButtonByDefaultId(itemName));
    }

    /**
     * =================================== Right Mouse Click ======================================
     */

    public void rightClick(int x, int y) {
        int xAdjusted = (int) Math.floor((x + renderer.getCamera().getX()) / (32.0 * ZOOM));
        int yAdjusted = (int) Math.floor((y + renderer.getCamera().getY()) / (32.0 * ZOOM));

        int xMapRelated = x + renderer.getCamera().getX();
        int yMapRelated = y + renderer.getCamera().getY();
        int xAligned = xMapRelated - (xMapRelated % CELL_SIZE);
        int yAligned = yMapRelated - (yMapRelated % CELL_SIZE);
        if (!selectedItem.isEmpty()) {
            deselectBagItem();
            return;
        }
        boolean removed;
        if (BOWL_TILE_ID == selectedTileId) {
            FoodBowl foodBowl = new FoodBowl(xAligned, yAligned);
            removed = getGameMap().removeBowl(foodBowl);
        } else if (WATER_BOWL_TILE_ID == selectedTileId) {
            WaterBowl waterBowl = new WaterBowl(xAligned, yAligned);
            removed = getGameMap().removeBowl(waterBowl);
        } else if (CHEST_TILE_ID == selectedTileId) {
            removed = getGameMap().removeStorageChest(xAligned, yAligned);
            gameMap.removeTile(xAdjusted, yAdjusted, tileService.getLayerById(selectedTileId, regularTiles), regularTiles, selectedTileId);
        } else if (CookingStove.TILE_IDS.contains(selectedTileId)) {
            removed = getGameMap().removeCookingStove(xAligned, yAligned);
            gameMap.removeTile(xAdjusted, yAdjusted, tileService.getLayerById(selectedTileId, regularTiles), regularTiles, selectedTileId);
        } else if (Fridge.TILE_ID == selectedTileId) {
            removed = getGameMap().removeFridge(xAligned, yAligned);
            gameMap.removeTile(xAdjusted, yAdjusted, tileService.getLayerById(selectedTileId, regularTiles), regularTiles, selectedTileId);
        } else {
            removed = gameMap.removeTile(xAdjusted, yAdjusted, tileService.getLayerById(selectedTileId, regularTiles), regularTiles, selectedTileId);
        }
        if (!removed) {
            deselectTile();
        }
        refreshCurrentMapCache();
    }

    /**
     * =================================== Other features ======================================
     */

    public void teleportToStarterMap() {
        logger.info("Starting game map loading started");

        refreshCurrentMapCache();
        gameMap = gameMaps.get(MapConstants.MAIN_MAP);
        player.teleportToCenter(this);
        logger.info("Starting game map loaded");

        renderer.adjustCamera(this, player);
    }

    public void deleteAnimal() {
        if (!CHEATS_MODE) {
            return;
        }
        if (selectedYourAnimal == null) {
            logger.debug("Nothing to delete - no animal is selected");
            return;
        }
        logger.info("Will remove selected animal");
        animalService.deleteAnimalFiles(animalsOnMaps.get(selectedYourAnimal.getCurrentMap()));
        animalsOnMaps.get(selectedYourAnimal.getCurrentMap()).remove(selectedYourAnimal);
        animalService.saveAllAnimals(animalsOnMaps.get(gameMap.getMapName()));
        refreshGuiPanels();

        logger.info("Animal removed");
    }

    public void editAnimalName(Animal animal) {
        ChangeAnimalNameWindow changeAnimalNameWindow = new ChangeAnimalNameWindow(getWidth() / 5 * 3, getHeight() / 3);
        changeAnimalNameWindow.editAnimalName(this, animal);
    }

    public boolean isNearWater(Animal animal) {
        return getGameMap(animal.getCurrentMap()).isThereWaterTile(animal.getRectangle());
    }

    public void updateAnimalIcon(Animal animal) {
        for (GUIButton animalIcon : yourAnimalsPanel.getButtons()) {
            if (animalIcon instanceof AnimalIcon && ((AnimalIcon) animalIcon).getAnimal().equals(animal)) {
                animalIcon.update(this);
            }
        }
    }

    public boolean isPetFoodSelected() {
        String itemName = getItemNameByButtonId();
        return (PetFood.mealTypes.contains(itemName));
    }

    public boolean isAnyKitchenContextClueVisible() {
        for (Fridge fridge : gameMap.getFridges()) {
            if (fridge.getContextClue().isVisible()) {
                return true;
            }
        }
        for (CookingStove cookingStove : gameMap.getCookingStoves()) {
            if (cookingStove.getContextClue().isVisible()) {
                return true;
            }
        }
        return false;
    }

    /**
     * =================================== PAUSE ======================================
     */

    public void pause() {
        paused = true;
    }

    public void unpause() {
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public void drawNewEscMenu() {
        if (!guiList.contains(escMenu)) {
            escMenu.updateSkillsInfo(player.getSkills());
            guiList.add(0, escMenu);
        } else {
            removeEscMenu();
        }
    }

    public void removeEscMenu() {
        guiList.remove(escMenu);
        renderer.removeText();
        renderer.clearRenderedText();
        renderer.clear();
    }

    public void changeEscMenuColor(int newColor) {
        escMenu.changeColor(newColor);
    }

    /**
     * =================================== ROUTE CALCULATORS ======================================
     */

    public Route calculateRouteToFood(Animal animal) {
        return routeCalculator.calculateRoute(getGameMap(animal.getCurrentMap()), animal, FOOD);
    }

    public Route calculateRouteToPlant(Animal animal) {
        return routeCalculator.calculateRoute(getGameMap(animal.getCurrentMap()), animal, PLANT);
    }

    public Route calculateRouteToWater(Animal animal) {
        Route route = routeCalculator.calculateRoute(getGameMap(animal.getCurrentMap()), animal, WATER);
        if (!route.isEmpty()) {
            return route;
        }
        if (MAIN_MAP.equalsIgnoreCase(animal.getCurrentMap())) {
            return routeCalculator.calculateRoute(getGameMap(animal.getCurrentMap()), animal, LAKE_WATER);
        } else {
            return calculateRouteToOtherMap(animal, getNextPortalToGetToCenter(animal.getCurrentMap()));
        }
    }

    public Route calculateRouteToPillow(Animal animal) {
        return routeCalculator.calculateRoute(getGameMap(animal.getCurrentMap()), animal, PILLOW);
    }

    public Route calculateRouteToNpc(Animal animal) {
        NpcAdoption npcAdoption = getGameMap(MAIN_MAP).getAdoptionNpc();
        if (animal.getCurrentMap().equalsIgnoreCase(npcAdoption.getCurrentMap())) {
            return routeCalculator.calculateRoute(getGameMap(animal.getCurrentMap()), animal, NPC);
        }
        return calculateRouteToOtherMap(animal, getNextPortalToGetToCenter(animal.getCurrentMap()));
    }

    public Route calculateRouteToOtherMap(Animal animal, String destination) {
        return routeCalculator.calculateRoute(getGameMap(animal.getCurrentMap()), animal, destination);
    }

    public Route calculateRouteToNpcSpot(Npc npc) {
        return routeCalculator.calculateRoute(getGameMap(MAIN_MAP), npc, NPC_SPOT);
    }

    public Route calculateRouteToCity(Npc npc) {
        return routeCalculator.calculateRoute(getGameMap(MAIN_MAP), npc, CITY);
    }

    public String getNearestMapWithFood(String currentMap) {
        List<String> mapsToCheck = NavigationService.getNearestMaps(currentMap);
        for (String mapName : mapsToCheck) {
            GameMap map = getGameMap(mapName);
            if (!map.getItems().isEmpty()) {
                return mapName;
            }
            for (FoodBowl bowl : map.getFoodBowls()) {
                if (bowl.isFull()) {
                    return mapName;
                }
            }
        }
        return currentMap;
    }

    public String getNearestMapWithPlants(String currentMap) {
        List<String> mapsToCheck = NavigationService.getNearestMaps(currentMap);
        for (String mapName : mapsToCheck) {
            GameMap map = getGameMap(mapName);
            if (!map.getPlants().isEmpty()) {
                return mapName;
            }
        }
        return currentMap;
    }

    public String getNearestMapWithWater(String currentMap) {
        List<String> mapsToCheck = NavigationService.getNearestMaps(currentMap);
        for (String mapName : mapsToCheck) {
            GameMap map = getGameMap(mapName);
            for (WaterBowl bowl : map.getWaterBowls()) {
                if (bowl.isFull()) {
                    return mapName;
                }
            }
        }
        return MAIN_MAP;
    }

    /**
     * =================================== NPC DIALOG ======================================
     */

    public void spawnAdoptionNpc(Animal wantedAnimal, String mapName) {
        logger.info("Spawning npc");
        GameMap mapToSpawn = getGameMap(mapName);
        NpcSpawnSpot spawnSpot = mapToSpawn.getNpcSpawnSpotByType(NpcType.ADOPTION);
        NpcAdoption npc;
        if (spawnSpot != null) {
            npc = new NpcAdoption(spawnSpot.getRectangle().getX(), spawnSpot.getRectangle().getY(), wantedAnimal);
        } else {
            npc = new NpcAdoption((mapToSpawn.getMapWidth() * TILE_SIZE) / 2, (mapToSpawn.getMapHeight() * TILE_SIZE) / 2, wantedAnimal);
        }
        mapToSpawn.addObject(npc);

        npc.setCurrentMap(mapName);
        interactionZones.add(npc.getInteractionZone());
    }

    public NpcSpot getNpcSpot(NpcType npcType) {
        return gameMaps.get(MAIN_MAP).getNpcSpot(npcType);
    }

    public void switchDialogBox() {
        if (!guiList.contains(dialogBox)) {
            showDialogBox();
        } else {
            hideDialogBox();
        }
    }

    private void showDialogBox() {
        guiList.add(dialogBox);
    }

    public void hideDialogBox() {
        guiList.remove(dialogBox);
        renderer.clearRenderedText();
    }

    public void setDialogText(String text) {
        dialogBox.setDialogText(text);
    }

    public void removeNpc(Npc npc) {
        logger.info("Removing npc");
        gameObjectsList.remove(npc);
        for (GameMap map : gameMaps.values()) {
            if (map.removeObject(npc)) {
                return;
            }
        }
    }

    public void giveAnimal() {
        NpcAdoption adoptionNpc = getGameMap(MAIN_MAP).getAdoptionNpc();
        Animal adoptedAnimal = adoptionNpc.getWantedAnimal();
        if (adoptedAnimal == null) {
            sendNpcAway(adoptionNpc);
            return;
        }
        Route route = calculateRouteToNpc(adoptedAnimal);
        adoptedAnimal.sendToNpc(route);
    }

    public void sendAnimalAway(Animal adoptedAnimal) {
        logger.info(String.format("%s is going AWAY with NPC", adoptedAnimal));
        adoptedAnimal.setSpeed(2);
        Route route = routeCalculator.calculateRoute(getGameMap(adoptedAnimal.getCurrentMap()), adoptedAnimal, "city");
        adoptedAnimal.goAway(route);
        NpcAdoption adoptionNpc = getGameMap(MAIN_MAP).getAdoptionNpc();
        sendNpcAway(adoptionNpc);
        int randomDrop = random.nextInt(2);
        if (randomDrop == 1) {
            dropRandomCoins(adoptionNpc);
        } else {
            dropRandomFood(adoptionNpc);
        }
    }

    public void sendNpcAway(NpcAdoption npc) {
        Route route = calculateRouteToCity(npc);
        npc.goAway(route);
    }

    public void dropRandomFood(NpcAdoption npc) {
        int xPosition = npc.getRectangle().getX();
        int yPosition = npc.getRectangle().getY();
        String plantType = PlantService.plantTypes.get(random.nextInt(PlantService.plantTypes.size()));
        Sprite sprite = spriteService.getPlantPreviewSprite(plantType);
        Item item = new Item(xPosition, yPosition, plantType, sprite);
        getGameMap(MAIN_MAP).addItem(item);
    }

    public void dropRandomCoins(NpcAdoption npc) {
        int xPosition = npc.getRectangle().getX();
        int yPosition = npc.getRectangle().getY();
        Coin coin = new Coin(xPosition, yPosition, random.nextInt(4) + 1);
        getGameMap(MAIN_MAP).addObject(coin);
    }

    public void removeAnimal(Animal animal) {
        logger.info("Removing animal");
        String map = animal.getCurrentMap();
        animalService.deleteAnimalFiles(animalsOnMaps.get(map));
        animalsOnMaps.get(map).remove(animal);
        animalService.saveAllAnimals(animalsOnMaps.get(map));
        refreshGuiPanels();
    }

    /**
     * =================================== INTERACTION ZONES ======================================
     */

    public void interact() {
        for (InteractionZone zone : interactionZones) {
            if (zone.isPlayerInRange()) {
                zone.action(this);
            }
        }
    }

    public boolean isInRangeOfAnyKitchen() {
        for (InteractionZone zone : interactionZones) {
            if (zone instanceof InteractionZoneKitchen && zone.isPlayerInRange()) {
                return true;
            }
        }
        return false;
    }

    /**
     * =================================== GETTERS ======================================
     */

    public boolean isBackpackEmpty() {
        return backpackService.isBackPackEmpty(this);
    }

    public int getSelectedTileId() {
        return selectedTileId;
    }

    public String getSelectedAnimal() {
        return selectedAnimal;
    }

    public Animal getYourSelectedAnimal() {
        return selectedYourAnimal;
    }

    public String getSelectedPlant() {
        return selectedPlant;
    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public KeyboardListener getKeyboardListener() {
        return keyboardListener;
    }

    public MouseEventListener getMouseEventListener() {
        return mouseEventListener;
    }

    public RenderHandler getRenderer() {
        return renderer;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public GameMap getGameMap(String mapName) {
        return gameMaps.get(mapName);
    }

    public Map<String, List<Animal>> getAnimalsOnMaps() {
        return animalsOnMaps;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isRegularTiles() {
        return regularTiles;
    }

    public Backpack getBackpackGui() {
        return backpackGui;
    }

    public TileService getTileService() {
        return tileService;
    }

    public AnimalService getAnimalService() {
        return animalService;
    }

    public PlantService getPlantService() {
        return plantService;
    }

    public BackpackService getBackpackService() {
        return backpackService;
    }

    public boolean isThereNpc() {
        return !getGameMap(MAIN_MAP).getNpcs().isEmpty();
    }

    public NpcAdoption getAdoptionNpc(String mapName) {
        return getGameMap(mapName).getAdoptionNpc();
    }

    public NpcVendor getVendorNpc() {
        return (NpcVendor) getGameMap(CITY_MAP).getNpcs().get(0);
    }

    public String getItemNameByButtonId() {
        String itemNameFromBackpack = backpackService.getItemNameByButtonId(backpackGui, selectedItem);
        if (itemNameFromBackpack != null) {
            return itemNameFromBackpack;
        }
        List<StorageChest> storageChests = getGameMap().getStorageChests();
        for (StorageChest chest : storageChests) {
            if (chest.getStorage().getCells() != null) {
                for (StorageCell cell : chest.getStorage().getCells()) {
                    if (cell.getDefaultId().equals(selectedItem)) {
                        return cell.getItemName();
                    }
                }
            }
        }
        return null;
    }

    public BackpackButton getSelectedButton() {
        BackpackButton backpackButton = (BackpackButton) backpackGui.findButtonByDefaultId(selectedItem);
        if (backpackButton != null) {
            return backpackButton;
        }
        List<StorageChest> storageChests = getGameMap().getStorageChests();
        for (StorageChest chest : storageChests) {
            for (StorageCell cell : chest.getStorage().getCells()) {
                if (cell.getDefaultId().equals(selectedItem)) {
                    return cell;
                }
            }
        }
        return null;
    }

    public int getAnimalCount() {
        return animalsOnMaps.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    public MapService getMapService() {
        return mapService;
    }

    public SpriteService getSpriteService() {
        return spriteService;
    }

    public ShopService getShopService() {
        return shopService;
    }

    public CookingMenu getCookingMenu() {
        return cookingMenu;
    }

    public Map<String, GameMap> getGameMaps() {
        return gameMaps;
    }

    public StorageService getStorageService() {
        return storageService;
    }

    public void addToInteractionZones(InteractionZone interactionZone) {
        interactionZones.add(interactionZone);
    }

    public LoadingService getLoadingService() {
        return loadingService;
    }

    public void setTileButtonsArray(GUI[] tileButtonsArray) {
        this.tileButtonsArray = tileButtonsArray;
    }

    public void setTerrainButtonsArray(GUI[] terrainButtonsArray) {
        this.terrainButtonsArray = terrainButtonsArray;
    }

    public void setYourAnimalsPanel(GUI yourAnimalsPanel) {
        this.yourAnimalsPanel = yourAnimalsPanel;
    }

    public void setPossibleAnimalPanel(GUI possibleAnimalPanel) {
        this.possibleAnimalPanel = possibleAnimalPanel;
    }

    public void setPlantsGui(GUI plantsGui) {
        this.plantsGui = plantsGui;
    }

    public void setBackpackGui(Backpack backpackGui) {
        this.backpackGui = backpackGui;
    }

    public void setDialogBox(DialogBox dialogBox) {
        this.dialogBox = dialogBox;
    }

    public void setEscMenu(EscMenu escMenu) {
        this.escMenu = escMenu;
    }

    public void setCookingMenu(CookingMenu cookingMenu) {
        this.cookingMenu = cookingMenu;
    }

    public void setPlayer(Player player) {
        gameObjectsList.add(player);
        this.player = player;
    }

    public List<GameObject> getGameObjectsList() {
        return gameObjectsList;
    }

    private boolean isEnvironmentOn() {
        return false;
    }

    public Map<String, List<Plant>> getPlantsOnMaps() {
        return plantsOnMaps;
    }
}
