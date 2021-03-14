import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Game extends JFrame implements Runnable {

    public static final int ALPHA = 0xFF80FF00;
    public static final int TILE_SIZE = 32;
    public static final int ZOOM = 2;

    public static final String PLAYER_SHEET_PATH = "img/betty.png";
    public static final String SPRITES_PATH = "img/sprites.png";
    public static final String TILE_LIST_PATH = "src/main/resources/Tile.txt";
    public static final String GAME_MAP_PATH = "src/main/resources/GameMap.txt";

    private final Canvas canvas = new Canvas();

    private RenderHandler renderer;
    private SpriteSheet spriteSheet;
    private GameMap gameMap;
    private GameObject[] gameObjects;
    private Player player;
    private SpriteSheet playerSheet;
    private AnimatedSprite playerAnimations;

    private KeyboardListener keyboardListener = new KeyboardListener(this);
    private MouseEventListener mouseEventListener = new MouseEventListener(this);

    public Game() {
        loadUI();
        loadControllers();
        loadPlayerAnimatedImages();
        loadMap();
        loadGameObjects();
    }

    public static void main(String[] args) {
        Game game = new Game();
        Thread gameThread = new Thread(game);
        gameThread.start();
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

        gameMap.renderMap(renderer, gameObjects, ZOOM, ZOOM);

//        for (GameObject gameObject : gameObjects) {
//            gameObject.render(renderer, ZOOM, ZOOM);
//        }

        renderer.render(graphics);

        graphics.dispose();
        bufferStrategy.show();
        renderer.clear();
    }

    private void update() {
        for (GameObject object : gameObjects) {
            object.update(this);
        }
    }

    private void loadUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 800, 500);
        setLocationRelativeTo(null);
        add(canvas);
        setVisible(true);
        canvas.createBufferStrategy(3);
        renderer = new RenderHandler(getWidth(), getHeight());
    }

    // TODO: refactor next two methods into one
    private void loadMap() {
        System.out.println("Game map loading started");

        loadSpriteSheet();
        TileService tileService = new TileService(new File(TILE_LIST_PATH), spriteSheet);
        gameMap = new GameMap(new File(GAME_MAP_PATH), tileService);

        System.out.println("Game map loaded");
    }

    void loadSecondaryMap(String mapPath) {
        System.out.println("Game map loading started");

        loadSpriteSheet();
        TileService tileService = new TileService(new File(TILE_LIST_PATH), spriteSheet);
        gameMap = new GameMap(new File(mapPath), tileService);

        System.out.println("Game map loaded");

        loadGameObjects();
    }

    private void loadSpriteSheet() {
        System.out.println("Sprite sheet loading started");

        BufferedImage bufferedImage = loadImage(SPRITES_PATH);
        spriteSheet = new SpriteSheet(bufferedImage);
        spriteSheet.loadSprites(TILE_SIZE, TILE_SIZE, 0);

        System.out.println("Sprite sheet loading done");
    }

    private BufferedImage loadImage(String path) {
        try {
            BufferedImage image = ImageIO.read(Game.class.getResource(path));
            BufferedImage formattedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            formattedImage.getGraphics().drawImage(image, 0, 0, null);
            return formattedImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
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
        System.out.println("Loading player animations");

        BufferedImage playerSheetImage = loadImage(PLAYER_SHEET_PATH);
        playerSheet = new SpriteSheet(playerSheetImage);
        playerSheet.loadSprites(TILE_SIZE, TILE_SIZE, 0);
        playerAnimations = new AnimatedSprite(playerSheet, 5);

        System.out.println("Player animations loaded");
    }

    private void loadGameObjects() {
        player = new Player(playerAnimations, getWidth()/2, getHeight()/2);

        gameObjects = new GameObject[1];
        gameObjects[0] = player;
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
