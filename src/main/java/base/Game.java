package base;

import base.constants.FilePath;
import base.constants.MapConstants;
import base.constants.VisibleText;
import base.events.EventService;
import base.gameobjects.*;
import base.gameobjects.interactionzones.InteractionZone;
import base.gameobjects.plants.Seed;
import base.gameobjects.services.AnimalService;
import base.gameobjects.services.BackpackService;
import base.gameobjects.services.ItemService;
import base.gameobjects.services.PlantService;
import base.gameobjects.storage.StorageCell;
import base.gameobjects.storage.StorageChest;
import base.graphicsservice.Rectangle;
import base.graphicsservice.*;
import base.gui.*;
import base.map.GameMap;
import base.map.MapService;
import base.map.Tile;
import base.map.TileService;
import base.navigationservice.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static base.constants.Constants.*;
import static base.constants.MapConstants.CITY_MAP;
import static base.constants.MapConstants.MAIN_MAP;
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
    private transient NpcLady npc;
    private transient NpcMan vendorNpc;

    private transient GameTips gameTips;

    // Services
    private transient Properties gameProperties;
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

    // Gui
    private transient GUI[] tileButtonsArray = new GUI[10];
    private transient GUI[] terrainButtonsArray;
    private transient GUI yourAnimalButtons;
    private transient GUI possibleAnimalButtons;
    private transient GUI plantsGui;
    private transient Backpack backpackGui;
    private transient DialogBox dialogBox;

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

    public Game() {
        loadGameProperties();
        initializeServices();
        loadUI();
        loadControllers();
        loadMap();
        loadGuiElements();
        enableDefaultGui();
        loadGameObjects(getWidth() / 2, getHeight() / 2);
    }

    public static void main(String[] args) {
        Game game = new Game();
        Thread gameThread = new Thread(game);
        gameThread.start();
    }

    private void loadGameProperties() {
        gameProperties = new Properties();

        try {
            gameProperties.load(new FileInputStream("config/application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DEBUG_MODE = Boolean.parseBoolean(gameProperties.getProperty(DEBUG_MODE_PROPERTY));
        CHEATS_MODE = Boolean.parseBoolean(gameProperties.getProperty(CHEATS_MODE_PROPERTY));
        TEST_MAP_MODE = Boolean.parseBoolean(gameProperties.getProperty(TEST_MAP_PROPERTY));
        LANGUAGE = gameProperties.getProperty(LANGUAGE_PROPERTY);
    }

    private void initializeServices() {
        tileService = new TileService();
        animalService = new AnimalService();
        plantService = new PlantService();
        itemService = new ItemService(plantService);
        guiService = new GuiService();
        backpackService = new BackpackService();
        routeCalculator = new RouteCalculator();
        mapService = new MapService();
        plantsOnMaps = new HashMap<>();
        animalsOnMaps = new HashMap<>();
        interactionZones = new ArrayList<>();
        gameMaps = new HashMap<>();
        eventService = new EventService();
        VisibleText.initializeTranslations();
    }

    private void loadUI() {
        setSizeBasedOnScreenSize();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(0, 0, MAX_SCREEN_WIDTH - 5, MAX_SCREEN_HEIGHT - 5);
        setLocationRelativeTo(null);
        add(canvas);
        setVisible(true);
        canvas.createBufferStrategy(3);
        renderer = new RenderHandler(getWidth(), getHeight());
    }

    private void setSizeBasedOnScreenSize() {
        GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (GraphicsDevice device : graphicsDevices) {
            if (MAX_SCREEN_WIDTH > device.getDisplayMode().getWidth()) {
                MAX_SCREEN_WIDTH = device.getDisplayMode().getWidth();
            }
            if (MAX_SCREEN_HEIGHT > device.getDisplayMode().getHeight()) {
                MAX_SCREEN_HEIGHT = device.getDisplayMode().getHeight();
            }
        }
        logger.info(String.format("Screen size will be %d by %d", MAX_SCREEN_WIDTH, MAX_SCREEN_HEIGHT));
    }

    private void loadControllers() {
        addListeners();

        canvas.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newWidth = canvas.getWidth();
                int newHeight = canvas.getHeight();

                if (newWidth > renderer.getMaxWidth())
                    newWidth = renderer.getMaxWidth();

                if (newHeight > renderer.getMaxHeight())
                    newHeight = renderer.getMaxHeight();

                renderer.getCamera().setWidth(newWidth);
                renderer.getCamera().setHeight(newHeight);
                canvas.setSize(newWidth, newHeight);
                pack();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                //not going to use it now
            }

            @Override
            public void componentShown(ComponentEvent e) {
                //not going to use it now
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                //not going to use it now
            }
        });
    }

    private void addListeners() {
        canvas.addKeyListener(keyboardListener);
        canvas.addFocusListener(keyboardListener);
        canvas.addMouseListener(mouseEventListener);
        canvas.addMouseMotionListener(mouseEventListener);
    }

    /**
     * =================================== Load Map ======================================
     */

    private void loadMap() {
        logger.info("Game map loading started");

        if (TEST_MAP_MODE) {
            gameMap = mapService.loadGameMap(MapConstants.TEST_MAP, tileService);
        } else {
            gameMap = mapService.loadGameMap(MAIN_MAP, tileService);
        }
        loadAnimalsOnMaps();

        initialCacheMaps();

        logger.info("Game map loaded");
    }

    private void loadAnimalsOnMaps() {
        List<String> mapNames = mapService.getAllMapsNames();
        for (String mapName : mapNames) {
            animalsOnMaps.put(mapName, new CopyOnWriteArrayList<>());
        }
        List<Animal> animals = animalService.loadAllAnimals();
        for (Animal animal : animals) {
            if (animalsOnMaps.get(animal.getCurrentMap()) != null) {
                animalsOnMaps.get(animal.getCurrentMap()).add(animal);
            } else {
                List<Animal> listForMap = new CopyOnWriteArrayList<>();
                listForMap.add(animal);
                animalsOnMaps.put(animal.getCurrentMap(), listForMap);
            }
        }
    }

    private void initialCacheMaps() {
        for (String mapName : mapService.getAllMapsNames()) {
            GameMap map = mapService.loadGameMap(mapName, tileService);
            gameMaps.put(mapName, map);
        }
    }

    /**
     * =================================== GUI Elements ======================================
     */

    private void loadGuiElements() {
        loadSDKGUI();
        loadTerrainGui();
        loadYourAnimals();
        loadPossibleAnimalsPanel();
        loadPlantsPanel();
        loadBackpack();
        dialogBox = guiService.loadDialogBox();
    }

    private void loadSDKGUI() {
        List<Tile> tiles = tileService.getTiles();
        tileButtonsArray = guiService.loadTerrainGui(tiles, 10);
    }

    void loadTerrainGui() {
        List<Tile> tiles = tileService.getTerrainTiles();
        terrainButtonsArray = guiService.loadTerrainGui(tiles, 18);
    }

    private void loadYourAnimals() {
        yourAnimalButtons = guiService.loadYourAnimals(this);
    }

    void loadPossibleAnimalsPanel() {
        Map<String, Sprite> previews = animalService.getAnimalPreviewSprites();
        possibleAnimalButtons = guiService.loadPossibleAnimalsPanel(this, previews);
    }

    void loadPlantsPanel() {
        Map<String, Sprite> previews = plantService.getPreviews();
        plantsGui = guiService.loadPlantsPanel(this, previews);
    }

    void loadBackpack() {
        backpackGui = backpackService.loadBackpackFromFile(this);
        if (backpackGui == null) {
            backpackGui = guiService.loadEmptyBackpack(this);
        }
    }

    /**
     * =================================== Defaults ======================================
     */

    // TODO: only one item per all buttons should be selected - issue #325 on github
    private void enableDefaultGui() {
        deselectEverything();

        guiList = new CopyOnWriteArrayList<>();
        guiList.add(tileButtonsArray[0]);
        guiList.add(yourAnimalButtons);
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
     * =================================== Game Objects ======================================
     */

    private void loadGameObjects(int startX, int startY) {
        gameObjectsList = new CopyOnWriteArrayList<>();

        loadPlayer(startX, startY);

        gameTips = new GameTips();
        cacheAllPlants();

        createVendorNpc();
    }

    private void loadPlayer(int startX, int startY) {
        AnimatedSprite playerAnimations = loadPlayerAnimatedImages();
        logger.info("Player animations loaded");
        player = new Player(playerAnimations, startX, startY);
        gameObjectsList.add(player);
    }

    private AnimatedSprite loadPlayerAnimatedImages() {
        logger.info("Loading player animations");

        BufferedImage playerSheetImage = ImageLoader.loadImage(FilePath.PLAYER_SHEET_PATH);
        SpriteSheet playerSheet = new SpriteSheet(playerSheetImage);
        playerSheet.loadSprites(TILE_SIZE, TILE_SIZE, 0);

        return new AnimatedSprite(playerSheet, 5, true);
    }

    private void cacheAllPlants() {
        logger.info("Caching plants");
        for (GameMap map : gameMaps.values()) {
            plantsOnMaps.put(map.getMapName(), map.getPlants());
        }
        logger.info("Caching plants finished");
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
            for (Animal animal : animals) {
                animal.update(this);
            }
        }
        for (List<Plant> plants : plantsOnMaps.values()) {
            for (Plant plant : plants) {
                plant.update(this);
            }
        }
        for (GameMap map : gameMaps.values()) {
            for (GameObject object : map.getInteractiveObjects()) {
                object.update(this);
            }
            for (GameObject object : map.getItems()) {
                object.update(this);
            }
        }
        eventService.update(this);
    }

    /** =================================== In Game Activities ====================================== */

    /**
     * =================================== Load another map ======================================
     */

    public void loadSecondaryMap(String mapName) {
        logger.info("Game map loading started");
        saveMaps();
        refreshCurrentMapCache();

        String previousMapName = gameMap.getMapName();
        logger.debug(String.format("Previous map name: %s", previousMapName));

        if (getGameMap(mapName) == null) {
            gameMap = mapService.loadGameMap(mapName, tileService);
        } else {
            gameMap = getGameMap(mapName);
        }

        if (plantsOnMaps.containsKey(gameMap.getMapName())) {
            gameMap.setPlants(plantsOnMaps.get(gameMap.getMapName()));
        }
        addPlantsToCache();
        logger.info(String.format("Game map %s loaded", gameMap.getMapName()));

        Portal portalToPrevious = gameMap.getPortalTo(previousMapName);
        adjustPlayerPosition(portalToPrevious);
        renderer.adjustCamera(this, player);
        refreshGuiPanels();
    }

    public void saveMaps() {
        renderer.setTextToDraw("...saving game...", 40);

        refreshCurrentMapCache();
        plantsOnMaps.put(gameMap.getMapName(), gameMap.getPlants());
        for (GameMap map : gameMaps.values()) {
            mapService.saveMap(map);
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
    }

    public void refreshCurrentMapCache() {
        gameMaps.put(gameMap.getMapName(), gameMap);
    }

    private void addPlantsToCache() {
        plantsOnMaps.put(gameMap.getMapName(), gameMap.getPlants());
    }

    private void adjustPlayerPosition(Portal portalToPrevious) {
        if (portalToPrevious != null) {
            int previousMapPortalX = gameMap.getSpawnPoint(portalToPrevious, true, player.getDirection());
            int previousMapPortalY = gameMap.getSpawnPoint(portalToPrevious, false, player.getDirection());
            logger.info("Will teleport player to x:" + previousMapPortalX + ", y: " + previousMapPortalY);
            player.teleportTo(previousMapPortalX, previousMapPortalY);
        } else {
            player.teleportToCenter(this);
        }
    }

    void refreshGuiPanels() {
        boolean backpackOpen = guiList.contains(backpackGui);
        boolean dialogBoxOpen = guiList.contains(dialogBox);
        guiList.clear();
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
        Portal portalToPrevious = gameMaps.get(animal.getCurrentMap()).getPortalTo(previousMap);
        if (portalToPrevious != null) {
            int previousMapPortalX = gameMaps.get(animal.getCurrentMap()).getSpawnPoint(portalToPrevious, true, animal.getDirection());
            int previousMapPortalY = gameMaps.get(animal.getCurrentMap()).getSpawnPoint(portalToPrevious, false, animal.getDirection());
            animal.teleportAnimalTo(previousMapPortalX, previousMapPortalY);
            Route routeToAdjust = new Route();
            routeToAdjust.addStep(animal.getDirection());
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
        if (!guiList.contains(yourAnimalButtons)) {
            yourAnimalButtons = guiService.loadYourAnimals(this);
            guiList.add(yourAnimalButtons);
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
        if (!guiList.contains(possibleAnimalButtons)) {
            guiList.clear();
            guiList.add(possibleAnimalButtons);
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
        for (GameObject gameObject : gameMap.getPlants()) {
            if (!stoppedChecking) {
                Rectangle newMouseRectangle = new Rectangle(xMapRelated - TILE_SIZE, yMapRelated - TILE_SIZE, TILE_SIZE, TILE_SIZE);
                stoppedChecking = gameObject.handleMouseClick(newMouseRectangle, renderer.getCamera(), ZOOM, this);
            }
        }
        for (GameObject gameObject : getGameMap().getInteractiveObjects()) {
            if (!stoppedChecking) {
                Rectangle newMouseRectangle = new Rectangle(xMapRelated - TILE_SIZE, yMapRelated - TILE_SIZE, TILE_SIZE, TILE_SIZE);
                stoppedChecking = gameObject.handleMouseClick(newMouseRectangle, renderer.getCamera(), ZOOM, this);
            }
        }
        for (Item item : gameMap.getItems()) {
            if (!stoppedChecking) {
                mouseRectangle = new Rectangle(xMapRelated - TILE_SIZE, yMapRelated - TILE_SIZE, TILE_SIZE, TILE_SIZE);
                stoppedChecking = item.handleMouseClick(mouseRectangle, renderer.getCamera(), ZOOM, this);
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
            }
            else if (guiList.contains(possibleAnimalButtons)) {
                createNewAnimal(smallerX, smallerY);
            }
            else {
                setNewTile(xMapRelated, yMapRelated, smallerX, smallerY);
            }
        }
        refreshCurrentMapCache();
    }

    private void setNewTile(int xMapRelated, int yMapRelated, int smallerX, int smallerY) {
        if (selectedTileId != -1) {
            if (player.getRectangle().intersects(xMapRelated, yMapRelated, TILE_SIZE, TILE_SIZE)) {
                logger.warn("Can't place tile under player");
            } else {
                int xAlligned = xMapRelated - (xMapRelated % CELL_SIZE);
                int yAlligned = yMapRelated - (yMapRelated % CELL_SIZE);
                if (selectedTileId == BOWL_TILE_ID) {
                    FoodBowl foodBowl = new FoodBowl(xAlligned, yAlligned);
                    getGameMap().addObject(foodBowl);
                } else if (selectedTileId == WATER_BOWL_TILE_ID) {
                    WaterBowl waterBowl = new WaterBowl(xAlligned, yAlligned);
                    getGameMap().addObject(waterBowl);
                } else if (selectedTileId == CHEST_TILE_ID) {
                    StorageChest chest = new StorageChest(xAlligned, yAlligned, tileService.getTiles().get(36).getSprite(), tileService.getTiles().get(37).getSprite());
                    getGameMap().addObject(chest);
                    gameMap.setTile(smallerX, smallerY, CHEST_TILE_ID, regularTiles);
                }
                else {
                    gameMap.setTile(smallerX, smallerY, selectedTileId, regularTiles);
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

    // TODO: change this when refactoring sprites and objects
    private void putItemOnTheGround(int xAdjusted, int yAdjusted, String itemType, boolean justDrop) {
        int xAlligned = xAdjusted - (xAdjusted % CELL_SIZE);
        int yAlligned = yAdjusted - (yAdjusted % CELL_SIZE);
        if (itemType.equalsIgnoreCase(Wood.ITEM_NAME)) {
            Wood wood = new Wood(tileService.getTiles().get(Wood.TILE_ID).getSprite(), xAlligned, yAlligned);
            gameMap.addObject(wood);
            guiService.decreaseNumberOnButton(this, getSelectedButton());
            return;
        }
        if (itemType.equalsIgnoreCase(Feather.ITEM_NAME)) {
            Feather feather = new Feather(tileService.getTiles().get(Feather.TILE_ID).getSprite(), xAlligned, yAlligned);
            gameMap.addObject(feather);
            guiService.decreaseNumberOnButton(this, getSelectedButton());
            return;
        }
        if (itemType.equalsIgnoreCase(Mushroom.ITEM_NAME)) {
            Mushroom mushroom = new Mushroom(tileService.getTiles().get(Mushroom.TILE_ID).getSprite(), xAlligned, yAlligned);
            gameMap.addObject(mushroom);
            guiService.decreaseNumberOnButton(this, getSelectedButton());
            return;
        }
        Item item = itemService.creteNewItem(itemType, xAlligned, yAlligned);
        if (item instanceof Seed) {
            if (justDrop || !createNewPlant(((Seed) item).getPlantType(), xAlligned / 64, yAlligned / 64)) {
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
        Animal newAnimal = animalService.createAnimal(tileX, tileY, selectedAnimal, gameMap.getMapName());
        animalsOnMaps.get(gameMap.getMapName()).add(newAnimal);
        addAnimalToPanel(newAnimal);
    }

    public void addAnimalToPanel(Animal animal) {
        int i = yourAnimalButtons.getButtonCount();
        Rectangle tileRectangle = new Rectangle(this.getWidth() - (CELL_SIZE + TILE_SIZE), i * (CELL_SIZE + 2), CELL_SIZE, CELL_SIZE);

        AnimalIcon animalIcon = new AnimalIcon(this, animal, animal.getPreviewSprite(), tileRectangle);
        yourAnimalButtons.addButton(animalIcon);
    }

    private boolean createNewPlant(String plantType, int x, int y) {
        if (plantType.isEmpty()) {
            return false;
        }
        int tileX = x * CELL_SIZE;
        int tileY = y * CELL_SIZE;
        if (gameMap.isThereGrassOrDirt(tileX, tileY) && gameMap.isPlaceEmpty(1, tileX, tileY) && gameMap.isInsideOfMap(x, y)) {
            Plant plant = plantService.createPlant(plantType, tileX, tileY);
            gameMap.addPlant(plant);
            return true;
        }
        return false;
    }

    public void pickUpPlant(Plant plant) {
        GUIButton button = backpackGui.getButtonBySprite(plant.getPreviewSprite());
        if (button == null) {
            putItemOnTheGround(plant.getRectangle().getX(), plant.getRectangle().getY(), plant.getPlantType(), true);
        } else {
            int amount = 1 + random.nextInt(3);
            pickUp(plant.getPlantType(), plant.getPreviewSprite(), button, amount);
        }

        int seedAmount = 1 + random.nextInt(2);
        Sprite seedSprite = plantService.getSeedSprite(plant.getPlantType());
        GUIButton buttonForSeed = backpackGui.getButtonBySprite(seedSprite);
        if (buttonForSeed == null) {
            putItemOnTheGround(plant.getRectangle().getX() + (ZOOM * TILE_SIZE), plant.getRectangle().getY(), "seed" + plant.getPlantType(), true);
        } else {
            pickUp("seed" + plant.getPlantType(), seedSprite, buttonForSeed, seedAmount);
        }
        if (plant.isRefreshable()) {
            if (random.nextInt(3) == 1) {
                gameMap.removePlant(plant);
                List<Plant> plantList = plantsOnMaps.get(getGameMap().getMapName());
                plantList.remove(plant);
            }
            plant.setGrowingStage(1);
            plant.setGrowingTicks(0);
        } else {
            gameMap.removePlant(plant);
            List<Plant> plantList = plantsOnMaps.get(getGameMap().getMapName());
            plantList.remove(plant);
        }
    }

    public void pickUpItem(String itemName, Sprite sprite, Rectangle rectangle) {
        GUIButton button = backpackGui.getButtonBySprite(sprite);
        if (pickUp(itemName, sprite, button, 1)) {
            gameMap.removeItem(itemName, rectangle);
        }
    }

    public void getItem(String itemName, Sprite sprite) {
        GUIButton button = backpackGui.getButtonBySprite(sprite);
        pickUp(itemName, sprite, button, 1);
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
        int xAlligned = xMapRelated - (xMapRelated % CELL_SIZE);
        int yAlligned = yMapRelated - (yMapRelated % CELL_SIZE);
        if (!selectedItem.isEmpty()) {
            deselectBagItem();
            return;
        }
        if (selectedTileId == BOWL_TILE_ID) {
            FoodBowl foodBowl = new FoodBowl(xAlligned, yAlligned);
            getGameMap().removeObject(foodBowl);
        } else if (selectedTileId == WATER_BOWL_TILE_ID) {
            WaterBowl waterBowl = new WaterBowl(xAlligned, yAlligned);
            getGameMap().removeObject(waterBowl);
        } else {
            gameMap.removeTile(xAdjusted, yAdjusted, tileService.getLayerById(selectedTileId, regularTiles), regularTiles, selectedTileId);
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
        loadSDKGUI();
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

    public void showTips() {
        if (renderer.getTextToDrawInCenter().isEmpty()) {
            logger.info("will start drawing text");
            renderer.setTextToDrawInCenter(gameTips.getLines());
        } else {
            logger.debug("removing text");
            renderer.removeText();
        }
    }

    public void editAnimalName(Animal animal) {
        ChangeAnimalNameWindow changeAnimalNameWindow = new ChangeAnimalNameWindow(getWidth() / 5 * 3, getHeight() / 3);
        changeAnimalNameWindow.editAnimalName(this, animal);
    }

    public boolean isNearWater(Animal animal) {
        return gameMap.isThereWaterTile(animal.getRectangle());
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

    /**
     * =================================== ROUTE CALCULATORS ======================================
     */

    public Route calculateRouteToFood(Animal animal) {
        return routeCalculator.calculateRoute(getGameMap(animal.getCurrentMap()), animal, FOOD);
    }

    public Route calculateRouteToWater(Animal animal) {
        Route route = routeCalculator.calculateRoute(getGameMap(animal.getCurrentMap()), animal, WATER);
        if (!route.isEmpty()) {
            return route;
        }
        if (animal.getCurrentMap().equalsIgnoreCase(MAIN_MAP)) {
            return routeCalculator.calculateRoute(getGameMap(animal.getCurrentMap()), animal, LAKE_WATER);
        } else {
            return calculateRouteToOtherMap(animal, getNextPortalToGetToCenter(animal.getCurrentMap()));
        }
    }

    public Route calculateRouteToPillow(Animal animal) {
        return routeCalculator.calculateRoute(getGameMap(animal.getCurrentMap()), animal, PILLOW);
    }

    public Route calculateRouteToNpc(Animal animal) {
        if (animal.getCurrentMap().equalsIgnoreCase(npc.getCurrentMap())) {
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

    public void spawnNpc(Animal wantedAnimal) {
        logger.info("Spawning npc");
        npc = new NpcLady(1408, 1600, wantedAnimal);

        if (getGameMap().getMapName().equals(MAIN_MAP)) {
            getGameMap().addObject(npc);
        } else {
            getGameMap(MAIN_MAP).addObject(npc);
        }
        npc.setCurrentMap(MAIN_MAP);
        refreshCurrentMapCache();
        gameObjectsList.add(npc);
        interactionZones.add(npc.getInteractionZone());
    }

    public NpcSpot getNpcSpot() {
        return gameMaps.get(MAIN_MAP).getNpcSpot();
    }

    public void interact() {
        for (InteractionZone zone : interactionZones) {
            if (zone.isPlayerInRange()) {
                zone.action(this);
            }
        }
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

    public void sendNpcAway() {
        Route route = calculateRouteToCity(npc);
        npc.goAway(route);
    }

    public void removeNpc(Npc npc) {
        logger.info("Removing npc");
        gameObjectsList.remove(npc);
        for (GameMap map : gameMaps.values()) {
            map.removeObject(npc);
        }
    }

    public void giveAnimal() {
        Animal adoptedAnimal = npc.getWantedAnimal();
        Route route = calculateRouteToNpc(adoptedAnimal);
        adoptedAnimal.sendToNpc(route);
    }

    public void sendAnimalAway(Animal adoptedAnimal) {
        logger.info(String.format("%s is going AWAY with NPC", adoptedAnimal));
        Route route = routeCalculator.calculateRoute(getGameMap(adoptedAnimal.getCurrentMap()), adoptedAnimal, "city");
        adoptedAnimal.goAway(route);
        npc.setSpeed(1);
        adoptedAnimal.setSpeed(2);
        sendNpcAway();
        int randomDrop = random.nextInt(2);
        if (randomDrop == 1) {
            dropRandomCoins();
        } else {
            dropRandomFood();
        }
    }

    public void dropRandomFood() {
        int xPosition = npc.getRectangle().getX();
        int yPosition = npc.getRectangle().getY();
        String plantType = plantService.plantTypes.get(random.nextInt(plantService.plantTypes.size()));
        Sprite sprite = plantService.getPlantSprite(plantType);
        Item item = new Item(xPosition, yPosition, plantType, sprite);
        getGameMap(MAIN_MAP).addItem(item);
    }

    public void dropRandomCoins() {
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

    public void createVendorNpc() {
        logger.info("Spawning vendor npc");
        Rectangle spot = getGameMap(CITY_MAP).getNpcSpot().getRectangle();
        vendorNpc = new NpcMan(spot.getX(), spot.getY(), plantService);
        getGameMap(CITY_MAP).addObject(vendorNpc);

        vendorNpc.setCurrentMap(CITY_MAP);
        refreshCurrentMapCache();
        gameObjectsList.add(vendorNpc);
        interactionZones.add(vendorNpc.getInteractionZone());
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

    public boolean isFoodSelected() {
        GUIButton button = backpackGui.findButtonByDefaultId(selectedItem);
        if (button instanceof BackpackButton) {
            String itemName = ((BackpackButton) button).getItemName();
            return itemName.length() > 2;
        }
        return false;
    }

    public KeyboardListener getKeyboardListener() {
        return keyboardListener;
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

    public ItemService getItemService() {
        return itemService;
    }

    public BackpackService getBackpackService() {
        return backpackService;
    }

    public boolean isThereNpc() {
        return gameObjectsList.contains(npc);
    }

    public NpcLady getAdoptionNpc() {
        return npc;
    }

    public NpcMan getVendorNpc() {
        return vendorNpc;
    }

    public String getItemNameByButtonId() {
        String itemNameFromBackpack = backpackService.getItemNameByButtonId(backpackGui, selectedItem);
        if (itemNameFromBackpack != null) {
            return itemNameFromBackpack;
        }
        List<StorageChest> storageChests = getGameMap().getStorages();
        for (StorageChest chest : storageChests) {
            for (StorageCell cell : chest.getStorage().getCells()) {
                if (cell.getDefaultId().equals(selectedItem)) {
                    return cell.getItemName();
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
        List<StorageChest> storageChests = getGameMap().getStorages();
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
}
