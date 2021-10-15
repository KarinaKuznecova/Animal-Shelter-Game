package base;

import base.gameobjects.*;
import base.graphicsservice.ImageLoader;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import base.graphicsservice.SpriteSheet;
import base.gui.GUI;
import base.gui.GUIButton;
import base.gui.SDKButton;
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
import java.util.List;

public class Game extends JFrame implements Runnable {

    public static final int ALPHA = 0xFF80FF00;
    public static final int TILE_SIZE = 32;
    public static final int ZOOM = 2;

    public static final String PLAYER_SHEET_PATH = "img/betty.png";
    public static final String SPRITES_PATH = "img/sprites.png";
    public static final String TILE_LIST_PATH = "src/main/java/base/map/config/Tile.txt";
    public static final String GAME_MAP_PATH = "src/main/java/base/map/config/GameMap.txt";

    private final Canvas canvas = new Canvas();

    protected static final Logger logger = LoggerFactory.getLogger(Game.class);

    private transient RenderHandler renderer;
    private transient SpriteSheet spriteSheet;
    private transient GameMap gameMap;
    private transient List<GameObject> gameObjectsList;
    private transient List<GameObject> guiList;

    private transient Player player;
    private transient AnimatedSprite playerAnimations;

    private transient TileService tileService;
    private transient AnimalService animalService;
    private transient ImageLoader imageLoader;

    private transient GUI tileButtons;

    private int selectedTileId = 2;

    private final transient KeyboardListener keyboardListener = new KeyboardListener(this);
    private final transient MouseEventListener mouseEventListener = new MouseEventListener(this);

    public Game() {
        initializeServices();
        loadUI();
        loadControllers();
        loadPlayerAnimatedImages();
        loadAnimalAnimatedImages();
        loadMap();
        loadSDKGUI();
        loadGameObjects(getWidth() / 2, getHeight() / 2);
    }

    public static void main(String[] args) {
        Game game = new Game();
        Thread gameThread = new Thread(game);
        gameThread.start();
    }

    private void initializeServices() {
        animalService = new AnimalService();
        imageLoader = new ImageLoader();
    }

    private void loadUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(0, 0, 1300, 900);
        setLocationRelativeTo(null);
        add(canvas);
        setVisible(true);
        canvas.createBufferStrategy(3);
        renderer = new RenderHandler(getWidth(), getHeight());
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

        BufferedImage playerSheetImage = imageLoader.loadImage(PLAYER_SHEET_PATH);
        SpriteSheet playerSheet = new SpriteSheet(playerSheetImage);
        playerSheet.loadSprites(TILE_SIZE, TILE_SIZE, 0);
        playerAnimations = new AnimatedSprite(playerSheet, 5, true);

        logger.info("Player animations loaded");
    }

    private void loadAnimalAnimatedImages() {
        logger.info("Loading animals animations");

        animalService.loadAnimatedImages(getWidth() / 2, getHeight() / 2);

        logger.info("Animals animations loaded");
    }

    private void loadMap() {
        logger.info("Game map loading started");

        loadSpriteSheet();
        tileService = new TileService(new File(TILE_LIST_PATH), spriteSheet);
        gameMap = new GameMap(new File(GAME_MAP_PATH), tileService);

        logger.info("Game map loaded");
    }

    public void loadSecondaryMap(String mapPath) {
        logger.info("Game map loading started");

        String previousMapName = gameMap.getMapName();
        logger.debug(String.format("Previous map name: %s", previousMapName));

        loadSpriteSheet();
        tileService = new TileService(new File(TILE_LIST_PATH), spriteSheet);
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
        fixStuckAnimals();
    }

    private void fixStuckAnimals() {
        for (GameObject gameObject : gameObjectsList) {
            if (gameObject instanceof Animal) {
                Animal animal = (Animal) gameObject;
                if (animal.isAnimalStuck(this)) {
                    animal.tryToMove(this);
                }
            }
        }
    }

    private void loadSpriteSheet() {
        logger.info("Sprite sheet loading started");

        BufferedImage bufferedImage = imageLoader.loadImage(SPRITES_PATH);
        if (bufferedImage == null) {
            logger.error("Buffered image is null, sprite path: " + SPRITES_PATH);
            throw new IllegalArgumentException();
        }
        spriteSheet = new SpriteSheet(bufferedImage);
        spriteSheet.loadSprites(TILE_SIZE, TILE_SIZE, 0);

        logger.info("Sprite sheet loading done");
    }

    private void loadSDKGUI() {
        List<Tile> tiles = tileService.getTiles();
        GUIButton[] buttons = new GUIButton[tiles.size() + 1];

        for (int i = 0; i < buttons.length - 1; i++) {
//            Rectangle tileRectangle = new Rectangle(0, i * (TILE_SIZE * ZOOM + 2), TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);       // vertical on top left side
            Rectangle tileRectangle = new Rectangle(i * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);  //horizontal on top left
            buttons[i] = new SDKButton(this, i, tiles.get(i).getSprite(), tileRectangle);
        }
        Rectangle tileRectangle = new Rectangle((tiles.size()) * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);  //one more horizontal on top left
        buttons[tiles.size()] = new SDKButton(this, -1, null, tileRectangle);
        changeTile(-1);

        tileButtons = new GUI(buttons, 5, 5, true);
        guiList = new ArrayList<>();
        guiList.add(tileButtons);
    }

    private void loadGameObjects(int startX, int startY) {
        gameObjectsList = new ArrayList<>();
        player = new Player(playerAnimations, startX, startY);
        gameObjectsList.add(player);

        gameObjectsList.addAll(animalService.getListOfAnimals());
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
    }

    public void changeTile(int tileId) {
        logger.info(String.format("changing tile to new tile : %d", tileId));
        selectedTileId = tileId;
    }

    public void leftClick(int x, int y) {
        Rectangle mouseRectangle = new Rectangle(x, y, 1, 1);
        boolean stoppedChecking = false;

        for (GameObject gameObject : guiList) {
            if (!stoppedChecking) {
                stoppedChecking = gameObject.handleMouseClick(mouseRectangle, renderer.getCamera(), ZOOM, ZOOM);
            }
        }
        if (!stoppedChecking) {
            x = (int) Math.floor((x + renderer.getCamera().getX()) / (32.0 * ZOOM));
            y = (int) Math.floor((y + renderer.getCamera().getY()) / (32.0 * ZOOM));
            gameMap.setTile(x, y, selectedTileId);

        }
    }

    public void rightClick(int x, int y) {
        x = (int) Math.floor((x + renderer.getCamera().getX()) / (32.0 * ZOOM));
        y = (int) Math.floor((y + renderer.getCamera().getY()) / (32.0 * ZOOM));
        gameMap.removeTile(x, y, tileService.getLayerById(selectedTileId));
    }

    public void handleCTRLandS() {
        gameMap.saveMap();
    }

    public void handleQ() {
        if (guiList.contains(tileButtons)) {
            guiList.remove(tileButtons);
        } else {
            guiList.add(tileButtons);
        }
    }

    public int getSelectedTileId() {
        return selectedTileId;
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
}
