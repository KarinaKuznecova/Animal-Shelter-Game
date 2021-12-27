package base;

import base.constants.FilePath;
import base.gameobjects.*;
import base.graphicsservice.Rectangle;
import base.graphicsservice.*;
import base.gui.*;
import base.map.*;
import base.navigationservice.KeyboardListener;
import base.navigationservice.MouseEventListener;
import base.navigationservice.Route;
import base.navigationservice.RouteCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static base.constants.Constants.*;

public class Game extends JFrame implements Runnable {

    private final Canvas canvas = new Canvas();

    protected static final Logger logger = LoggerFactory.getLogger(Game.class);

    private transient RenderHandler renderer;
    private transient GameMap gameMap;
    private transient List<GameObject> gameObjectsList;
    private transient List<GameObject> guiList;
    private transient Map<String, List<Plant>> plantsOnMaps;
    private transient Map<String, List<Animal>> animalsOnMaps;
    private transient Map<String, GameMap> gameMaps;

    private transient Player player;
    private transient GameTips gameTips;

    private transient TileService tileService;
    private transient AnimalService animalService;
    private transient PlantService plantService;
    private transient GuiService guiService;
    private transient BackpackService backpackService;
    private transient RouteCalculator routeCalculator;
    private transient MapService mapService;

    private transient GUI[] tileButtonsArray = new GUI[10];
    private transient GUI[] terrainButtonsArray;
    private transient GUI yourAnimalButtons;
    private transient GUI possibleAnimalButtons;
    private transient GUI plantsGui;
    private transient GUI backpackGui;

    private boolean regularTiles = true;

    private int selectedTileId = -1;
    private String selectedAnimal = "";
    private Animal selectedYourAnimal = null;
    private String selectedPlant = "";
    private int selectedPanel = 1;
    private String selectedItem = "";

    private final transient KeyboardListener keyboardListener = new KeyboardListener(this);
    private final transient MouseEventListener mouseEventListener = new MouseEventListener(this);

    public Game() {
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

    private void initializeServices() {
        tileService = new TileService();
        animalService = new AnimalService();
        plantService = new PlantService();
        guiService = new GuiService();
        backpackService = new BackpackService();
        routeCalculator = new RouteCalculator();
        mapService = new MapService();
        plantsOnMaps = new HashMap<>();
        animalsOnMaps = new HashMap<>();
        gameMaps = new HashMap<>();
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

    private void loadMap() {
        logger.info("Game map loading started");

        gameMap = mapService.loadGameMap("MainMap", tileService);
        loadAnimalsOnMaps();

        initialCacheMaps();

        logger.info("Game map loaded");
    }

    private void initialCacheMaps() {
        for (String mapName : mapService.getAllMapsNames()) {
            GameMap map = mapService.loadGameMap(mapName, tileService);
            gameMaps.put(mapName, map);
        }
    }

    public void loadSecondaryMap(String mapName) {
        logger.info("Game map loading started");
        saveMap();

        String previousMapName = gameMap.getMapName();
        logger.debug(String.format("Previous map name: %s", previousMapName));

        gameMap = mapService.loadGameMap(mapName, tileService);

        if (plantsOnMaps.containsKey(gameMap.getMapName())) {
            gameMap.setPlants(plantsOnMaps.get(gameMap.getMapName()));
        }
        addPlantsToCache();
        logger.info(String.format("Game map %s loaded", gameMap.getMapName()));

        MapTile portalToPrevious = gameMap.getPortalTo(previousMapName);
        adjustPlayerPosition(portalToPrevious);
        renderer.adjustCamera(this, player);
        refreshGuiPanels();
    }

    private void adjustPlayerPosition(MapTile portalToPrevious) {
        if (portalToPrevious != null) {
            int previousMapPortalX = gameMap.getSpawnPoint(portalToPrevious, true);
            int previousMapPortalY = gameMap.getSpawnPoint(portalToPrevious, false);
            player.teleportTo(previousMapPortalX, previousMapPortalY);
        } else {
            player.teleportToCenter(this);
        }
    }

    private void loadGuiElements() {
        loadSDKGUI();
        loadTerrainGui();
        loadYourAnimals();
        loadPossibleAnimalsPanel();
        loadPlantsPanel();
        loadBackpack();
    }

    private void loadSDKGUI() {
        List<Tile> tiles = tileService.getTiles();
        tileButtonsArray = guiService.loadTerrainGui(this, tiles, 10);
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

    void loadTerrainGui() {
        List<Tile> tiles = tileService.getTerrainTiles();
        terrainButtonsArray = guiService.loadTerrainGui(this, tiles, 18);
    }

    void loadBackpack() {
        backpackGui = backpackService.loadBackpackFromFile();
        if (backpackGui != null) {
            backpackGui.setGame(this);
        } else {
            backpackGui = guiService.loadEmptyBackpack(this);
        }
    }

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
        changeSelectedItem("");
    }

    void refreshGuiPanels() {
        boolean backpackOpen = guiList.contains(backpackGui);
        guiList.clear();
        switchTopPanel(selectedPanel);
        if (backpackOpen) {
            guiList.add(backpackGui);
        }
    }

    private void loadGameObjects(int startX, int startY) {
        gameObjectsList = new CopyOnWriteArrayList<>();

        loadPlayer(startX, startY);

        gameTips = new GameTips();
        cacheAllPlants();
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
        List<String> mapNames = mapService.getAllMapsNames();
        for (String mapName : mapNames) {
            plantsOnMaps.put(mapName, mapService.getOnlyPlantsFromMap(mapName));
        }
        logger.info("Caching plants finished");
    }

    private void addPlantsToCache() {
        plantsOnMaps.put(gameMap.getMapName(), gameMap.getPlants());
    }

    private void loadAnimalsOnMaps() {
        List<String> mapNames = mapService.getAllMapsNames();
        for (String mapName : mapNames) {
            animalsOnMaps.put(mapName, new CopyOnWriteArrayList<>());
        }
        List<Animal> animals = animalService.loadAllAnimals();
        for (Animal animal : animals) {
            if (animalsOnMaps.get(animal.getHomeMap()) != null) {
                animalsOnMaps.get(animal.getHomeMap()).add(animal);
            } else {
                List<Animal> listForMap = new CopyOnWriteArrayList<>();
                listForMap.add(animal);
                animalsOnMaps.put(animal.getHomeMap(), listForMap);
            }
        }
    }

    public void run() {
        long lastTime = System.nanoTime(); //long 2^63
        double nanoSecondConversion = 1000000000.0 / 60; //60 frames per second
        double changeInSeconds = 0;

        while (true) {
            long now = System.nanoTime();

            changeInSeconds += (now - lastTime) / nanoSecondConversion;
            while (changeInSeconds >= 1) {
                update();
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
            gameObject.render(renderer, ZOOM, ZOOM);
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
    }

    public void changeTile(int tileId) {
        deselectBagItem();
        logger.info(String.format("changing tile to new tile : %d", tileId));
        selectedTileId = tileId;
    }

    public void changeAnimal(String animalType) {
        deselectBagItem();
        deselectTile();
        logger.info(String.format("changing selected animal to : %s", animalType));
        selectedAnimal = animalType;
    }

    public void changeYourAnimal(Animal animal) {
        deselectBagItem();
        deselectTile();
        if (animal == null) {
            logger.info("changing your selected animal to null");
        } else {
            logger.info(String.format("changing your selected animal to : %s", animal.getAnimalName()));
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

    private void deselectPlant() {
        selectedPlant = "";
    }

    private void deselectTile() {
        selectedTileId = -1;
    }

    private void deselectBagItem() {
        selectedItem = "";
    }

    public void deselectAnimal() {
        selectedYourAnimal = null;
    }

    public void leftClick(int xScreenRelated, int yScreenRelated) {
        int xMapRelated = xScreenRelated + renderer.getCamera().getX();
        int yMapRelated = yScreenRelated + renderer.getCamera().getY();
        logger.debug(String.format("Click on x: %d", xMapRelated));
        logger.debug(String.format("Click on y: %d", yMapRelated));
        Rectangle mouseRectangle = new Rectangle(xScreenRelated, yScreenRelated, 1, 1);
        boolean stoppedChecking = false;

        for (GameObject gameObject : guiList) {
            if (!stoppedChecking) {
                deselectAnimal();
                stoppedChecking = gameObject.handleMouseClick(mouseRectangle, renderer.getCamera(), ZOOM, ZOOM, this);
            }
        }
        for (GameObject gameObject : gameMap.getPlants()) {
            if (!stoppedChecking) {
                mouseRectangle = new Rectangle(xMapRelated - TILE_SIZE, yMapRelated - TILE_SIZE, TILE_SIZE, TILE_SIZE);
                stoppedChecking = gameObject.handleMouseClick(mouseRectangle, renderer.getCamera(), ZOOM, ZOOM, this);
            }
        }
        for (GameObject gameObject : getGameMap().getInteractiveObjects()) {
            if (!stoppedChecking) {
                mouseRectangle = new Rectangle(xMapRelated - TILE_SIZE, yMapRelated - TILE_SIZE, TILE_SIZE, TILE_SIZE);
                stoppedChecking = gameObject.handleMouseClick(mouseRectangle, renderer.getCamera(), ZOOM, ZOOM, this);
            }
        }
        for (Item item : gameMap.getItems()) {
            if (!stoppedChecking) {
                mouseRectangle = new Rectangle(xMapRelated - TILE_SIZE, yMapRelated - TILE_SIZE, TILE_SIZE, TILE_SIZE);
                stoppedChecking = item.handleMouseClick(mouseRectangle, renderer.getCamera(), ZOOM, ZOOM, this);
            }
        }
        for (Animal animal : animalsOnMaps.get(gameMap.getMapName())) {
            if (!stoppedChecking) {
                mouseRectangle = new Rectangle(xMapRelated - TILE_SIZE, yMapRelated - TILE_SIZE, TILE_SIZE, TILE_SIZE);
                stoppedChecking = animal.handleMouseClick(mouseRectangle, renderer.getCamera(), ZOOM, ZOOM, this);
            }
        }
        if (!stoppedChecking) {
            int smallerX = (int) Math.floor(xMapRelated / (32.0 * ZOOM));
            int smallerY = (int) Math.floor(yMapRelated / (32.0 * ZOOM));
            if (!guiList.contains(possibleAnimalButtons)) {
                setNewTile(xMapRelated, yMapRelated, smallerX, smallerY);
            }
            if (guiList.contains(possibleAnimalButtons)) {
                createNewAnimal(smallerX, smallerY);
            }
            if (guiList.contains(plantsGui)) {
                createNewPlant(smallerX, smallerY);
            }
        }
        refreshCurrentMapCache();
    }

    private void setNewTile(int xMapRelated, int yMapRelated, int smallerX, int smallerY) {
        if (selectedTileId != -1) {
            if (player.getPlayerRectangle().intersects(xMapRelated, yMapRelated, TILE_SIZE, TILE_SIZE)) {
                logger.warn("Can't place tile under player");
            } else {
                if (selectedTileId == BOWL_TILE_ID) {
                    logger.debug("Food bowl!");
                    int xAlligned = xMapRelated - (xMapRelated % (TILE_SIZE * ZOOM));
                    int yAlligned = yMapRelated - (yMapRelated % (TILE_SIZE * ZOOM));
                    FoodBowl foodBowl = new FoodBowl(xAlligned, yAlligned);
                    getGameMap().addObject(foodBowl);
                } else {
                    gameMap.setTile(smallerX, smallerY, selectedTileId, regularTiles);
                }
            }
        }
        if (selectedItem.length() > 2) {
            logger.info("Will put item on the ground");
            putItemOnTheGround(xMapRelated, yMapRelated);
        }
    }

    private void putItemOnTheGround(int xAdjusted, int yAdjusted) {
        int xAlligned = xAdjusted - (xAdjusted % (TILE_SIZE * ZOOM));
        int yAlligned = yAdjusted - (yAdjusted % (TILE_SIZE * ZOOM));
        Sprite sprite = plantService.getPlantSprite(selectedItem);
        Item item = new Item(xAlligned, yAlligned, selectedItem, sprite);
        gameMap.addItem(item);
        guiService.decreaseNumberOnButton(this, (BackpackButton) backpackGui.getButtonBySprite(sprite));
    }

    private void createNewAnimal(int x, int y) {
        if (selectedAnimal.isEmpty()) {
            return;
        }
        if (animalsOnMaps.get(gameMap.getMapName()).size() >= 10) {
            logger.warn("Too many animals, can't add new");
            return;
        }
        int tileX = x * (TILE_SIZE * ZOOM);
        int tileY = y * (TILE_SIZE * ZOOM);
        Animal newAnimal = animalService.createAnimal(tileX, tileY, selectedAnimal, gameMap.getMapName());
        animalsOnMaps.get(gameMap.getMapName()).add(newAnimal);
        addAnimalToPanel(newAnimal);
    }

    public void addAnimalToPanel(Animal animal) {
        int i = yourAnimalButtons.getButtonCount();
        Rectangle tileRectangle = new Rectangle(this.getWidth() - (TILE_SIZE * ZOOM + TILE_SIZE), i * (TILE_SIZE * ZOOM + 2), TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);

        AnimalIcon animalIcon = new AnimalIcon(this, animal, animal.getPreviewSprite(), tileRectangle);
        yourAnimalButtons.addButton(animalIcon);
    }

    private void createNewPlant(int x, int y) {
        if (selectedPlant.isEmpty()) {
            return;
        }
        int tileX = x * (TILE_SIZE * ZOOM);
        int tileY = y * (TILE_SIZE * ZOOM);
        if (gameMap.isThereGrassOrDirt(tileX, tileY) && gameMap.isPlaceEmpty(1, tileX, tileY) && gameMap.isInsideOfMap(x, y)) {
            Plant plant = plantService.createPlant(selectedPlant, tileX, tileY);
            gameMap.addPlant(plant);
            List<Plant> plantList = plantsOnMaps.get(getGameMap().getMapName());
            plantList.add(plant);
        }
    }

    public void pickUpPlant(Plant plant) {
        GUIButton button = backpackGui.getButtonBySprite(plant.getPreviewSprite());
        pickUp(plant.getPlantType(), plant.getPreviewSprite(), button);
        gameMap.removePlant(plant);
        List<Plant> plantList = plantsOnMaps.get(getGameMap().getMapName());
        plantList.remove(plant);
    }

    public void pickUpItem(String itemName, Sprite sprite, Rectangle rectangle) {
        GUIButton button = backpackGui.getButtonBySprite(sprite);
        pickUp(itemName, sprite, button);
        gameMap.removeItem(itemName, rectangle);
    }

    private void pickUp(String itemName, Sprite sprite, GUIButton button) {
        if (button instanceof BackpackButton) {
            logger.info("found a slot in backpack");
            if (button.getSprite() == null) {
                logger.info("slot was empty, will put plant");
                button.setSprite(sprite);
                button.setObjectCount(1);
                ((BackpackButton) button).setItem(itemName);
            } else {
                logger.info("plant is already in backpack, will increment");
                button.setObjectCount(button.getObjectCount() + 1);
            }
        } else {
            logger.info("No empty slots in backpack");
        }
    }

    public void removeItemFromInventory(String itemName) {
        guiService.decreaseNumberOnButton(this, (BackpackButton) backpackGui.getButtonByItemName(itemName));
    }

    public void rightClick(int x, int y) {
        int xAdjusted = (int) Math.floor((x + renderer.getCamera().getX()) / (32.0 * ZOOM));
        int yAdjusted = (int) Math.floor((y + renderer.getCamera().getY()) / (32.0 * ZOOM));
        if (selectedTileId == BOWL_TILE_ID) {
            logger.debug("Food bowl to remove!");
            int xMapRelated = x + renderer.getCamera().getX();
            int yMapRelated = y + renderer.getCamera().getY();
            int xAlligned = xMapRelated - (xMapRelated % (TILE_SIZE * ZOOM));
            int yAlligned = yMapRelated - (yMapRelated % (TILE_SIZE * ZOOM));
            FoodBowl foodBowl = new FoodBowl(xAlligned, yAlligned);
            getGameMap().removeObject(foodBowl);
        } else {
            gameMap.removeTile(xAdjusted, yAdjusted, tileService.getLayerById(selectedTileId, regularTiles), regularTiles, selectedTileId);
        }
        refreshCurrentMapCache();
    }

    public void saveMap() {
        renderer.setTextToDraw("...saving game...", 40);

        refreshCurrentMapCache();
        plantsOnMaps.put(gameMap.getMapName(), gameMap.getPlants());
        mapService.saveMap(gameMap);

        for (List<Animal> animals : animalsOnMaps.values()) {
            if (animals.isEmpty()) {
                continue;
            }
            logger.info("Deleting all animal files");
            animalService.deleteAnimalFiles(animals);

            logger.info("Saving animals to file");
            animalService.saveAllAnimals(animals);
        }
        backpackService.saveBackpack(backpackGui);
    }

    public void hideGuiPanels() {
        if (guiList.isEmpty()) {
            switchTopPanel(selectedPanel);
        } else {
            guiList.clear();
        }
    }

    public void teleportToStarterMap() {
        logger.info("Starting game map loading started");

        refreshCurrentMapCache();
        gameMap = gameMaps.get(MAIN_MAP);
        player.teleportToCenter(this);
        logger.info("Starting game map loaded");

        renderer.adjustCamera(this, player);
        loadSDKGUI();
    }

    public void switchTopPanel(int panelId) {
        logger.info(String.format("Switching panels to id: %d", panelId));

        boolean backpackOpen = guiList.contains(backpackGui);

        selectedPanel = panelId;

        renderer.clearRenderedText();
        if (isNewAnimalsPanel(panelId)) {
            showNewAnimalsPanel();
        } else if (isPlantsPanel(panelId)) {
            showNewPlantsPanel();
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

    public void showBackpack() {
        if (guiList.contains(backpackGui)) {
            guiList.remove(backpackGui);
            changeSelectedItem("");
            renderer.clearRenderedText();
        } else {
            guiList.add(backpackGui);
        }
    }

    public void deleteAnimal() {
        if (selectedYourAnimal == null) {
            logger.debug("Nothing to delete - no animal is selected");
            return;
        }
        logger.info("Will remove selected animal");
        animalService.deleteAnimalFiles(animalsOnMaps.get(selectedYourAnimal.getHomeMap()));
        animalsOnMaps.get(selectedYourAnimal.getHomeMap()).remove(selectedYourAnimal);
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

    public void moveAnimalToAnotherMap(Animal animal, MapTile portal) {
        String destination = portal.getPortalDirection();

        String previousMap = animal.getHomeMap();
        animalsOnMaps.get(animal.getHomeMap()).remove(animal);
        animalsOnMaps.get(destination).add(animal);

        animalService.deleteAnimalFiles(animal);

        animal.setHomeMap(destination);
        adjustAnimalPosition(animal, previousMap);

        animalService.saveAnimalToFile(animal);

        refreshCurrentMapCache();
        refreshGuiPanels();
    }

    private void adjustAnimalPosition(Animal animal, String previousMap) {
        MapTile portalToPrevious = gameMaps.get(animal.getHomeMap()).getPortalTo(previousMap);
        if (portalToPrevious != null) {
            int previousMapPortalX = gameMap.getSpawnPoint(portalToPrevious, true);
            int previousMapPortalY = gameMap.getSpawnPoint(portalToPrevious, false);
            animal.teleportAnimalTo(previousMapPortalX, previousMapPortalY);
        } else {
            animal.teleportAnimalTo(getWidth() / 2, getHeight() / 2);
        }
    }

    public Route calculateRouteToFood(Animal animal) {
        return routeCalculator.calculateRouteToFood(getGameMap(animal.getHomeMap()), animal);
    }

    public Route calculateRouteToMap(Animal animal, String destination) {
        logger.debug(String.format("Looking how to get to %s for %s", destination, animal.getAnimalName()));
        return routeCalculator.calculateRouteToPortal(getGameMap(animal.getHomeMap()), animal, destination);
    }

    public void refreshCurrentMapCache() {
        gameMaps.put(gameMap.getMapName(), gameMap);
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
}
