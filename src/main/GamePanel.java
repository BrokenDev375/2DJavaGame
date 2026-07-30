package main;

import javax.swing.JPanel;
import java.awt.*;

import game_data.SaveManager;
import entity.Entity;
import entity_manager.EntityManager;
import entity_manager.ObjectManager;
import object_data.WorldObject;
import object_data.WorldObjectFactory;
import object_data.weapons.WeaponFactory;
import object_data.weapons.WeaponType;
import tile.Chunk;
import tile.ChunkManager;
import tile.TileManager;
import input_manager.InputController;
import input_manager.InputManager;
import interact_manager.Interact;
import ui.health.HealthUI;
import ui.health.MonsterHealthUI;
import ui.base.UIManager;
import ui.effects.FadeUI;
import ui.effects.MessageUI;
import ui.health.PlayerStatusUI;
import ui.screens.gameover.GameOverUI;
import ui.screens.mainmenu.MainMenuUI;
import ui.screens.pause.PauseOverlay;

import java.util.List;


public class GamePanel extends JPanel implements WorldQuery, RenderContext {
    private final GameConfig config = GameConfig.defaults();

    // ===== SCREEN SETTING =====
    public final int originalTileSize = config.originalTileSize(); // 16x16 tile
    final int scale = config.scale();
    public final int tileSize = config.tileSize(); // 48x48 tile
    public final int maxScreenCol = config.maxScreenCol(); // width
    public final int maxScreenRow = config.maxScreenRow(); // height
    public final int screenWidth = config.screenWidth();
    public final int screenHeight = config.screenHeight();

    // ===== WORLD SETTING =====
    public final int maxWorldCol = config.maxWorldCol();
    public final int maxWorldRow = config.maxWorldRow();
    public final int chunkSize = config.chunkSize();

    // ===== SYSTEM =====
    private final UtilityTool uTool = new UtilityTool();
    private final AssetLoader assetLoader = new AssetLoader(uTool);
    private final TileManager tileM = new TileManager(this, assetLoader);
    private final ChunkManager chunkM = new ChunkManager(chunkSize, this);
    private final InputManager input;

    // ===== SAVE  =====
    private final SaveManager saveManager = new SaveManager();

    // ===== OTHERS =====
    private final Camera camera = new Camera(config);
    private CollisionChecker cChecker;
    private final WeaponFactory weaponFactory = new WeaponFactory(this);
    private final WorldObjectFactory worldObjectFactory = new WorldObjectFactory(this, weaponFactory);
    private Interact iR;
    // ===== ENTITY MANAGER =====
    private EntityManager em;
    private final ObjectManager om = new ObjectManager(this);
    private GameSession session;
    private GameRenderer renderer;

    // ===== UI SYSTEM =====
    private final UIManager uiManager = new UIManager();
    private PauseOverlay pauseOverlay;
    public static final float SCALE = 3f;

    // ===== MAP =====
    private final int numMaps = config.numMaps();

    // ===== GAME STATE =====
    private final GameStateManager gsm = new GameStateManager();

    // ===== THREAD =====
    Thread gameThread;

    public GamePanel() {
        // Window setup
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        // ===== UI INITIALIZATION =====
        pauseOverlay = new PauseOverlay(this);
        uiManager.add(pauseOverlay);
        uiManager.add(new MainMenuUI(this));
        uiManager.add(new MessageUI(this));
        uiManager.add(new FadeUI(this));
        uiManager.add(new HealthUI(this));
        uiManager.add(new PlayerStatusUI(this));
        uiManager.add(new MonsterHealthUI(this));
        uiManager.add(new GameOverUI(this));
        uiManager.add(new ui.effects.DialogueUI(this));
        // Input
        this.input = new InputManager(this, this);

        // Core managers
        em = new EntityManager(this, input.getKeyController());
        resetCollisionChecker();
        session = new GameSession(
                chunkM,
                em,
                om,
                uiManager,
                gsm,
                uTool,
                mapIndex -> weaponFactory.create(WeaponType.SWORD, mapIndex),
                this::resetCollisionChecker,
                this::resetInteractionRouter
        );
        renderer = new GameRenderer(tileM, chunkM, om, em, uiManager, gsm, session);
        resetInteractionRouter();

    }


    public void setupGame() {
        session.setupGame();
    }

    public InputController getInputController() {
        return input.getKeyController();
    }

    @Override
    public GameConfig getConfig() {
        return config;
    }

    public int getFrameCounter() {
        return session == null ? 0 : session.getFrameCounter();
    }

    @Override
    public int frameCounter() {
        return getFrameCounter();
    }

    public int getCurrentMap() {
        return session == null ? 0 : session.getCurrentMap();
    }

    @Override
    public int currentMap() {
        return getCurrentMap();
    }

    public int getNumMaps() {
        return numMaps;
    }

    public GameState getGameState() {
        return gsm.getState();
    }

    public void setGameState(GameState state) {
        gsm.setState(state);
    }

    public void setCurrentMap(int mapIndex) {
        if (session != null) {
            session.setCurrentMap(mapIndex);
        }
    }

    public CollisionChecker getCollisionChecker() {
        return cChecker;
    }

    @Override
    public CollisionChecker collisionChecker() {
        return cChecker;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public UtilityTool getUtilityTool() {
        return uTool;
    }

    public AssetLoader getAssetLoader() {
        return assetLoader;
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public Camera camera() {
        return camera;
    }

    public TileManager getTileManager() {
        return tileM;
    }

    public ChunkManager getChunkManager() {
        return chunkM;
    }

    public UIManager getUiManager() {
        return uiManager;
    }

    public ObjectManager getObjectManager() {
        return om;
    }

    public WeaponFactory getWeaponFactory() {
        return weaponFactory;
    }

    public WorldObjectFactory getWorldObjectFactory() {
        return worldObjectFactory;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    @Override
    public List<WorldObject> objectsOnMap(int mapId) {
        return om.getObjects(mapId);
    }

    @Override
    public Entity player() {
        return em == null ? null : em.getPlayer();
    }

    @Override
    public Iterable<Chunk> activeChunks() {
        return chunkM.getActiveChunks();
    }

    @Override
    public boolean isTileCollidable(int tileNum) {
        return tileM.isTileCollidable(tileNum);
    }

    private void resetCollisionChecker() {
        cChecker = new CollisionChecker(this);
    }

    public void resetInteractionRouter() {
        if (em != null && em.getPlayer() != null) {
            iR = new Interact(this, em.getPlayer(), getInputController());
        }
    }

    public void restartGame() {
        session.restartGame();
    }

    public void startGameThread() {
        gameThread = new Thread(new GameLoop(this));
        gameThread.start();
    }

    // ===== UPDATE LOOP =====
    public void update() {
        session.update();
    }

    // ===== DRAW LOOP =====
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        renderer.draw(g2);

        g2.dispose();
    }
}
