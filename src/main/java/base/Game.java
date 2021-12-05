package base;

import base.gameobjects.*;
import base.graphicsservice.Rectangle;
import base.graphicsservice.*;
import base.gui.*;
import base.map.GameMap;
import base.map.MapTile;
import base.map.Tile;
import base.map.TileService;
import base.navigationservice.KeyboardListener;
import base.navigationservice.MouseEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game extends JFrame implements Runnable {

    public static final int ALPHA = 0xFF80FF00;
    public static final int TILE_SIZE = 32;
    public static final int ZOOM = 2;

    private int maxScreenWidth = 21 * (TILE_SIZE * ZOOM);
    private int maxScreenHeight = 21 * (TILE_SIZE * ZOOM);

    public static final String PLAYER_SHEET_PATH = "img/betty.png";
    public static final String GAME_MAP_PATH = "maps/GameMap.txt";

    private final Canvas canvas = new Canvas();

    protected static final Logger logger = LoggerFactory.getLogger(Game.class);

    private transient RenderHandler renderer;
    private transient GameMap gameMap;
    private transient List<GameObject> gameObjectsList;
    private transient List<GameObject> guiList;

    private transient Player player;
    private transient AnimatedSprite playerAnimations;

    private transient GameTips gameTips;
    private transient Map<Sprite, Integer> backpack = new HashMap<>();

    private transient TileService tileService;
    private transient AnimalService animalService;
    private transient PlantService plantService;

    private final transient GUI[] tileButtonsArray = new GUI[10];
    private final transient GUI[] terrainButtonsArray = new GUI[11];
    private transient GUI yourAnimalButtons;
    private transient GUI possibleAnimalButtons;
    private transient GUI plantsGui;
    private transient GUI backpackGui;

    private boolean regularTiles = true;

    private int selectedTileId = -1;
    private String selectedAnimal = "";
    private int selectedYourAnimal = -1;
    private String selectedPlant = "";
    private int selectedPanel = 1;

    private final transient KeyboardListener keyboardListener = new KeyboardListener(this);
    private final transient MouseEventListener mouseEventListener = new MouseEventListener(this);

    public Game() {
        initializeServices();
        loadUI();
        loadControllers();
        loadPlayerAnimatedImages();
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
        animalService = new AnimalService();
        plantService = new PlantService();
    }

    private void loadUI() {
        setSizeBasedOnScreenSize();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(0, 0, maxScreenWidth - 5, maxScreenHeight - 5);
        setLocationRelativeTo(null);
        add(canvas);
        setVisible(true);
        canvas.createBufferStrategy(3);
        renderer = new RenderHandler(getWidth(), getHeight());
    }

    private void setSizeBasedOnScreenSize() {
        GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (GraphicsDevice device : graphicsDevices) {
            if (maxScreenWidth > device.getDisplayMode().getWidth()) {
                maxScreenWidth = device.getDisplayMode().getWidth();
            }
            if (maxScreenHeight > device.getDisplayMode().getHeight()) {
                maxScreenHeight = device.getDisplayMode().getHeight();
            }
        }
        logger.info(String.format("Screen size will be %d by %d", maxScreenWidth, maxScreenHeight));
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

    private void loadPlayerAnimatedImages() {
        logger.info("Loading player animations");

        BufferedImage playerSheetImage = ImageLoader.loadImage(PLAYER_SHEET_PATH);
        SpriteSheet playerSheet = new SpriteSheet(playerSheetImage);
        playerSheet.loadSprites(TILE_SIZE, TILE_SIZE, 0);
        playerAnimations = new AnimatedSprite(playerSheet, 5, true);

        logger.info("Player animations loaded");
    }

    private void loadMap() {
        logger.info("Game map loading started");

        tileService = new TileService();
        gameMap = new GameMap(new File(GAME_MAP_PATH), tileService);

        logger.info("Game map loaded");
    }

    public void loadSecondaryMap(String mapPath) {
        logger.info("Game map loading started");

        logger.info("Saving animals on previous map");
        gameMap.saveAnimals();

        String previousMapName = gameMap.getMapName();
        logger.debug(String.format("Previous map name: %s", previousMapName));

        gameMap = new GameMap(new File(mapPath), tileService);

        logger.info(String.format("Game map %s loaded", gameMap.getMapName()));

        MapTile portalToPrevious = gameMap.getPortalTo(previousMapName);
        if (portalToPrevious != null) {
            int previousMapPortalX = gameMap.getSpawnPoint(portalToPrevious, true);
            int previousMapPortalY = gameMap.getSpawnPoint(portalToPrevious, false);
            loadGameObjects(previousMapPortalX, previousMapPortalY);
        } else {
            loadGameObjects(getWidth() / 2, getHeight() / 2);
        }
        renderer.adjustCamera(this, player);
        refreshGuiPanels();
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

        List<GUIButton> buttons = new ArrayList<>();
        for (int i = 0, j = 0; i < tiles.size(); i++, j++) {
//              Rectangle tileRectangle = new Rectangle(0, i * (TILE_SIZE * ZOOM + 2), TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);       // vertical on top left side
            Rectangle tileRectangle = new Rectangle(j * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);  //horizontal on top left
            buttons.add(new SDKButton(this, i, tiles.get(i).getSprite(), tileRectangle));
            if (i != 0 && i % 10 == 0) {
                Rectangle oneMoreTileRectangle = new Rectangle((j + 1) * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);  //one more horizontal on top left
                buttons.add(new SDKButton(this, -1, null, oneMoreTileRectangle));
                tileButtonsArray[i / 10 - 1] = new GUI(buttons, 5, 5, true);

                buttons = new ArrayList<>();
                j = -1;
            }
            if (i == tiles.size() - 1) {
                Rectangle oneMoreTileRectangle = new Rectangle((j + 1) * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);  //one more horizontal on top left
                buttons.add(new SDKButton(this, -1, null, oneMoreTileRectangle));
                int temp = (i - (i % 10)) / 10;
                tileButtonsArray[temp] = new GUI(buttons, 5, 5, true);
            }
        }

    }

    private void loadYourAnimals() {
        List<Animal> animals = getGameMap().getAnimals();
        List<GUIButton> buttons = new CopyOnWriteArrayList<>();

        for (int i = 0; i < animals.size(); i++) {
            Animal animal = animals.get(i);
            Sprite animalSprite = animal.getPreviewSprite();
            Rectangle tileRectangle = new Rectangle(this.getWidth() - (TILE_SIZE * ZOOM + TILE_SIZE), i * (TILE_SIZE * ZOOM + 2), TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
            buttons.add(new AnimalIcon(this, i, animalSprite, tileRectangle));
        }

        yourAnimalButtons = new GUI(buttons, 5, 5, true);
    }

    void loadPossibleAnimalsPanel() {
        Map<String, Sprite> previews = animalService.getAnimalPreviewSprites();
        List<GUIButton> buttons = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Sprite> entry : previews.entrySet()) {
            Sprite animalSprite = entry.getValue();
            Rectangle tileRectangle = new Rectangle(i * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);  //horizontal on top left
            buttons.add(new NewAnimalButton(this, entry.getKey(), animalSprite, tileRectangle));
            i++;
        }
        Rectangle tileRectangle = new Rectangle((previews.size()) * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);  //one more horizontal on top left
        buttons.add(new NewAnimalButton(this, "", null, tileRectangle));
        changeAnimal("");

        possibleAnimalButtons = new GUI(buttons, 5, 5, true);
    }

    void loadPlantsPanel() {
        List<GUIButton> buttons = new ArrayList<>();
        Map<String, Sprite> previews = plantService.getPreviews();

        int i = 0;
        for (Map.Entry<String, Sprite> entry : previews.entrySet()) {
            Sprite previewSprite = entry.getValue();
            Rectangle tileRectangle = new Rectangle(i * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
            buttons.add(new PlantButton(this, entry.getKey(), previewSprite, tileRectangle));
            i++;
        }
        Rectangle oneMoreTileRectangle = new Rectangle((previews.size()) * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
        buttons.add(new PlantButton(this, "", null, oneMoreTileRectangle));
        changeSelectedPlant("");

        plantsGui = new GUI(buttons, 5, 5, true);
    }

    void loadTerrainGui() {
        List<Tile> tiles = tileService.getTerrainTiles();

        List<GUIButton> buttons = new ArrayList<>();
        for (int i = 0, j = 0; i < tiles.size(); i++, j++) {
            Rectangle tileRectangle = new Rectangle(j * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
            buttons.add(new SDKButton(this, i, tiles.get(i).getSprite(), tileRectangle));
            if (i != 0 && i % 18 == 0) {
                Rectangle oneMoreTileRectangle = new Rectangle((j + 1) * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
                buttons.add(new SDKButton(this, -1, null, oneMoreTileRectangle));
                terrainButtonsArray[i / 18 - 1] = new GUI(buttons, 5, 5, true);

                buttons = new ArrayList<>();
                j = -1;
            }
            if (i == tiles.size() - 1) {
                Rectangle oneMoreTileRectangle = new Rectangle((j + 1) * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
                buttons.add(new SDKButton(this, -1, null, oneMoreTileRectangle));
                int temp = (i - (i % 18)) / 18;
                terrainButtonsArray[temp] = new GUI(buttons, 5, 5, true);
            }
        }
    }

    void loadBackpack() {
        List<GUIButton> buttons = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Rectangle buttonRectangle = new Rectangle(j * (TILE_SIZE * ZOOM + 2), i * (TILE_SIZE * ZOOM), TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
                buttons.add(new BackpackButton(this, -1, null, buttonRectangle));
            }
        }
        backpackGui = new GUI(buttons, 5, this.getHeight() - (4 * (TILE_SIZE * ZOOM + 2)), true);
    }

    void enableDefaultGui() {
        changeTile(-1);
        changeYourAnimal(-1);
        changeSelectedPlant("");

        guiList = new CopyOnWriteArrayList<>();
        guiList.add(tileButtonsArray[0]);
        guiList.add(yourAnimalButtons);
    }

    void refreshGuiPanels() {
        guiList.clear();
        switchTopPanel(selectedPanel);
    }

    private void loadGameObjects(int startX, int startY) {
        gameObjectsList = new ArrayList<>();
        player = new Player(playerAnimations, startX, startY);
        gameObjectsList.add(player);

        gameTips = new GameTips();
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

        gameMap.renderMap(renderer, gameObjectsList);

        for (GameObject gameObject : guiList) {
            gameObject.render(renderer, ZOOM, ZOOM);
        }

        renderer.render(graphics);

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
        for (Animal animal : getGameMap().getAnimals()) {
            animal.update(this);
        }
        for (Plant plant : getGameMap().getPlants()) {
            plant.update(this);
        }
    }

    public void changeTile(int tileId) {
        logger.info(String.format("changing tile to new tile : %d", tileId));
        selectedTileId = tileId;
    }

    public void changeAnimal(String animalType) {
        logger.info(String.format("changing selected animal to : %s", animalType));
        selectedAnimal = animalType;
    }

    public void changeYourAnimal(int animalId) {
        logger.info(String.format("changing your selected animal to : %d", animalId));
        selectedYourAnimal = animalId;
    }

    public void changeSelectedPlant(String plantType) {
        logger.info(String.format("changing your selected plant to : %s", plantType));
        selectedPlant = plantType;
    }

    public void leftClick(int x, int y) {
        int xAdjusted = x + renderer.getCamera().getX();
        int yAdjusted = y + renderer.getCamera().getY();
        logger.debug(String.format("Click on x: %d", xAdjusted));
        logger.debug(String.format("Click on y: %d", yAdjusted));
        Rectangle mouseRectangle = new Rectangle(x, y, 1, 1);
        boolean stoppedChecking = false;

        for (GameObject gameObject : guiList) {
            if (!stoppedChecking) {
                deselectAnimal();
                stoppedChecking = gameObject.handleMouseClick(mouseRectangle, renderer.getCamera(), ZOOM, ZOOM, this);
            }
        }
        for (GameObject gameObject : gameMap.getPlants()) {
            if (!stoppedChecking) {
                mouseRectangle = new Rectangle(xAdjusted - TILE_SIZE, yAdjusted - TILE_SIZE, TILE_SIZE, TILE_SIZE);
                stoppedChecking = gameObject.handleMouseClick(mouseRectangle, renderer.getCamera(), ZOOM, ZOOM, this);
            }
        }
        if (!stoppedChecking) {
            int smallerX = (int) Math.floor(xAdjusted / (32.0 * ZOOM));
            int smallerY = (int) Math.floor(yAdjusted / (32.0 * ZOOM));
            if (!guiList.contains(possibleAnimalButtons)) {
                if (player.getPlayerRectangle().intersects(xAdjusted, yAdjusted, TILE_SIZE, TILE_SIZE)) {
                    logger.warn("Can't place tile under player");
                    return;
                }
                gameMap.setTile(smallerX, smallerY, selectedTileId, regularTiles);
            }
            if (guiList.contains(possibleAnimalButtons)) {
                createNewAnimal(smallerX, smallerY);
            }
            if (guiList.contains(plantsGui)) {
                createNewPlant(smallerX, smallerY);
            }
        }
    }

    private void createNewAnimal(int x, int y) {
        if (selectedAnimal.isEmpty()) {
            return;
        }
        if (gameMap.getAnimals().size() >= 10) {
            logger.warn("Too many animals, can't add new");
            return;
        }
        int tileX = x * (TILE_SIZE * ZOOM);
        int tileY = y * (TILE_SIZE * ZOOM);
        Animal newAnimal = animalService.createAnimal(tileX, tileY, selectedAnimal, gameMap.getMapName());
        gameMap.addAnimal(newAnimal);
        addAnimalToPanel(newAnimal);
    }

    public void addAnimalToPanel(Animal animal) {
        int i = yourAnimalButtons.getButtonCount();
        Rectangle tileRectangle = new Rectangle(this.getWidth() - (TILE_SIZE * ZOOM + TILE_SIZE), i * (TILE_SIZE * ZOOM + 2), TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);

        AnimalIcon animalIcon = new AnimalIcon(this, i, animal.getPreviewSprite(), tileRectangle);
        yourAnimalButtons.addButton(animalIcon);
    }

    private void createNewPlant(int x, int y) {
        if (selectedPlant.isEmpty()) {
            return;
        }
        int tileX = x * (TILE_SIZE * ZOOM);
        int tileY = y * (TILE_SIZE * ZOOM);
        if (gameMap.isThereGrassOrDirt(tileX, tileY) && gameMap.isPlaceEmpty(1, tileX, tileY) && gameMap.isInsideOfMap(x, y)) {
            Plant plant = plantService.createPlant(selectedPlant, tileX, tileY, gameMap.getMapName());
            gameMap.addPlant(plant);
        }
    }

    public void pickUpPlant(Plant plant) {
        GUIButton button = backpackGui.getButton(plant.getPreviewSprite());
        if (button != null) {
            logger.info("found a slot in backpack");
            if (button.getSprite() == null) {
                logger.info("slot was empty, will put plant");
                button.setSprite(plant.getPreviewSprite());
                button.setObjectCount(1);
            } else {
                logger.info("plant is already in backpack, will increment");
                button.setObjectCount(button.getObjectCount() + 1);
            }
        } else {
            logger.info("No empty slots in backpack");
        }
        gameMap.removePlant(plant);
    }

    public void rightClick(int x, int y) {
        x = (int) Math.floor((x + renderer.getCamera().getX()) / (32.0 * ZOOM));
        y = (int) Math.floor((y + renderer.getCamera().getY()) / (32.0 * ZOOM));
        gameMap.removeTile(x, y, tileService.getLayerById(selectedTileId, regularTiles), regularTiles);
    }

    public void saveMap() {
        renderer.setTextToDraw("...saving game...", 40);
        gameMap.saveMap();
    }

    public void hideGuiPanels() {
        if (guiList.isEmpty()) {
            switchTopPanel(selectedPanel);
        } else {
            guiList.clear();
        }
    }

    public void replaceMapWithDefault() {

        logger.info("Default game map loading started");

        gameMap = new GameMap(new File(GAME_MAP_PATH), tileService);
        player.teleportToCenter(this);
        logger.info("Default game map loaded");

        renderer.adjustCamera(this, player);
        loadSDKGUI();
    }

    public void switchTopPanel(int panelId) {
        logger.info(String.format("Switching panels to id: %d", panelId));

        selectedPanel = panelId;
        if (panelId == 0) {
            if (!guiList.contains(possibleAnimalButtons)) {
                guiList.clear();
                guiList.add(possibleAnimalButtons);
                regularTiles = true;
            }
        } else if (panelId == 10) {
            if (!guiList.contains(plantsGui)) {
                selectedTileId = -1;
                guiList.clear();
                guiList.add(plantsGui);
            }
        } else if (regularTiles) {
            if (tileButtonsArray[panelId - 1] != null && !guiList.contains(tileButtonsArray[panelId - 1])) {
                guiList.clear();
                guiList.add(tileButtonsArray[panelId - 1]);
            }
        } else {
            if (terrainButtonsArray[panelId - 1] != null && !guiList.contains(terrainButtonsArray[panelId - 1])) {
                guiList.clear();
                guiList.add(terrainButtonsArray[panelId - 1]);
            }
        }
        if (!guiList.contains(yourAnimalButtons)) {
            loadYourAnimals();
            guiList.add(yourAnimalButtons);
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
        selectedTileId = -1;

        loadYourAnimals();
        guiList.add(yourAnimalButtons);

    }

    public void showBackpack() {
        if (guiList.contains(backpackGui)) {
            guiList.remove(backpackGui);
            renderer.clearNumbers();
        } else {
            guiList.add(backpackGui);
        }
    }

    public void deselectAnimal() {
        selectedYourAnimal = -1;
    }

    public void deleteAnimal() {
        if (selectedYourAnimal == -1) {
            logger.debug("Nothing to delete - no animal is selected");
            return;
        }
        logger.info("Will remove selected animal");
        int temp = selectedYourAnimal;
        gameMap.removeAnimal(selectedYourAnimal);
        gameMap.saveAnimals();
        refreshGuiPanels();

        if (gameMap.getAnimals().size() <= selectedYourAnimal) {
            selectedYourAnimal = gameMap.getAnimals().size() - 1;
        } else {
            selectedYourAnimal = temp;
        }

        logger.info("Animal removed");
    }

    public int getSelectedTileId() {
        return selectedTileId;
    }

    public String getSelectedAnimal() {
        return selectedAnimal;
    }

    public int getYourSelectedAnimal() {
        return selectedYourAnimal;
    }

    public String getSelectedPlant() {
        return selectedPlant;
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

    public void showTips() {
        if (renderer.getTextToDrawInCenter().isEmpty()) {
            logger.info("will start drawing text");
            renderer.setTextToDrawInCenter(gameTips.getLines());
        } else {
            logger.debug("removing text");
            renderer.removeText();
        }
    }
}
