package main;

import entity_manager.EntityManager;
import entity_manager.ObjectManager;
import object_data.weapons.Weapon;
import tile.ChunkManager;
import ui.base.UIManager;

import java.util.function.IntFunction;

public final class GameSession {
    private final ChunkManager chunkManager;
    private final EntityManager entityManager;
    private final ObjectManager objectManager;
    private final UIManager uiManager;
    private final GameStateManager gameStateManager;
    private final UtilityTool utilityTool;
    private final IntFunction<Weapon> defaultWeaponFactory;

    private int currentMap = 0;
    private int frameCounter = 0;

    public GameSession(
            ChunkManager chunkManager,
            EntityManager entityManager,
            ObjectManager objectManager,
            UIManager uiManager,
            GameStateManager gameStateManager,
            UtilityTool utilityTool,
            IntFunction<Weapon> defaultWeaponFactory
    ) {
        this.chunkManager = chunkManager;
        this.entityManager = entityManager;
        this.objectManager = objectManager;
        this.uiManager = uiManager;
        this.gameStateManager = gameStateManager;
        this.utilityTool = utilityTool;
        this.defaultWeaponFactory = defaultWeaponFactory;
    }

    public void setupGame() {
        entityManager.getPlayer().resetToDefaults();
        chunkManager.loadMap("map3");
        gameStateManager.setState(GameState.START);
    }

    public void restartGame() {
        setCurrentMap(3);
        chunkManager.loadMap("map3");
        entityManager.getPlayer().resetToDefaults();
        entityManager.getPlayer().refillHP();
        entityManager.getPlayer().placeOnMap(getCurrentMap());
        entityManager.getPlayer().resetProgression();

        Weapon defaultWeapon = defaultWeaponFactory.apply(getCurrentMap());
        entityManager.getPlayer().equipWeapon(defaultWeapon);
        objectManager.reloadMapObjects(getCurrentMap());
        entityManager.update(getCurrentMap());
        gameStateManager.setState(GameState.PLAY);
    }

    public void update() {
        switch (gameStateManager.getState()) {
            case START, GAME_OVER -> uiManager.update(gameStateManager.getState());
            case PLAY -> updatePlay();
            case DIALOGUE -> uiManager.update(gameStateManager.getState());
            case PAUSE -> uiManager.update(GameState.PAUSE);
        }
    }

    private void updatePlay() {
        var player = entityManager.getPlayer();
        chunkManager.updateChunks(player.getWorldX(), player.getWorldY());
        setCurrentMap(utilityTool.mapNameToIndex(chunkManager.getMapPath()));
        entityManager.update(getCurrentMap());

        if (player != null && player.isDead()) {
            gameStateManager.setState(GameState.GAME_OVER);
            return;
        }

        uiManager.update(GameState.PLAY);
    }

    public int getCurrentMap() {
        return currentMap;
    }

    void setCurrentMap(int currentMap) {
        this.currentMap = currentMap;
    }

    public int getFrameCounter() {
        return frameCounter;
    }

    public void tickFrameCounter() {
        frameCounter++;
        if (frameCounter >= 1_000_000) {
            frameCounter = 0;
        }
    }
}
