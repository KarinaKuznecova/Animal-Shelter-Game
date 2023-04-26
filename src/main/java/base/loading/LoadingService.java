package base.loading;

public class LoadingService {

    private final GamePropertiesLoadingService gamePropertiesLoadingService;
    private final SpritesLoadingService spritesLoadingService;
    private final GameUILoadingService gameUILoadingService;
    private final ControllersLoadingService controllersLoadingService;
    private final GameMapLoadingService gameMapLoadingService;
    private final GuiElementsLoadingService guiElementsLoadingService;
    private final NpcLoadingService npcLoadingService;
    private final PlayerLoadingService playerLoadingService;

    public LoadingService() {
        gamePropertiesLoadingService = new GamePropertiesLoadingService();
        spritesLoadingService = new SpritesLoadingService();
        gameUILoadingService = new GameUILoadingService();
        controllersLoadingService = new ControllersLoadingService();
        gameMapLoadingService = new GameMapLoadingService();
        guiElementsLoadingService = new GuiElementsLoadingService();
        npcLoadingService = new NpcLoadingService();
        playerLoadingService = new PlayerLoadingService();
    }

    public GamePropertiesLoadingService getGamePropertiesLoadingService() {
        return gamePropertiesLoadingService;
    }

    public SpritesLoadingService getSpritesLoadingService() {
        return spritesLoadingService;
    }

    public GameUILoadingService getGameUILoadingService() {
        return gameUILoadingService;
    }

    public ControllersLoadingService getControllersLoadingService() {
        return controllersLoadingService;
    }

    public GameMapLoadingService getGameMapLoadingService() {
        return gameMapLoadingService;
    }

    public GuiElementsLoadingService getGuiElementsLoadingService() {
        return guiElementsLoadingService;
    }

    public NpcLoadingService getNpcLoadingService() {
        return npcLoadingService;
    }

    public PlayerLoadingService getPlayerLoadingService() {
        return playerLoadingService;
    }
}
