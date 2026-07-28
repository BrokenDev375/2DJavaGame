package main;

import javax.swing.JPanel;
import java.awt.*;

import game_data.SaveManager;
import entity_manager.EntityManager;
import entity_manager.ObjectManager;
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


public class GamePanel extends JPanel {
    // ===== SCREEN SETTING =====
    public final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 25; // width
    public final int maxScreenRow = 14; // height
    public final int screenWidth = tileSize * maxScreenCol;  // 786 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // ===== WORLD SETTING =====
    public final int maxWorldCol = 32 * 3;
    public final int maxWorldRow = 32 * 3;
    public final int chunkSize = 32;

    // ===== SYSTEM =====
    private final TileManager tileM = new TileManager(this);
    private final ChunkManager chunkM = new ChunkManager(chunkSize, this);
    private final InputManager input;

    // ===== SAVE  =====
    private final SaveManager saveManager = new SaveManager();

    // ===== OTHERS =====
    private final Camera camera = new Camera(this);
    private CollisionChecker cChecker;
    private final UtilityTool uTool = new UtilityTool();
    private int frameCounter = 0;
    private Interact iR;
    // ===== ENTITY MANAGER =====
    private EntityManager em;
    private final ObjectManager om = new ObjectManager(this);

    // ===== UI SYSTEM =====
    private final UIManager uiManager = new UIManager();
    private PauseOverlay pauseOverlay;
    public static final float SCALE = 3f;

    // ===== MAP =====
    private final int numMaps = 3;
    private int currentMap = 0;

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
        resetInteractionRouter();

    }


    public void setupGame() {
        em.getPlayer().setDefaultValues();
        chunkM.loadMap("map3");
        gsm.setState(GameState.START);
    }

    public InputController getInputController() {
        return input.getKeyController();
    }

    public int getFrameCounter() {
        return frameCounter;
    }

    public int getCurrentMap() {
        return currentMap;
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
        currentMap = mapIndex;
    }

    public CollisionChecker getCollisionChecker() {
        return cChecker;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public UtilityTool getUtilityTool() {
        return uTool;
    }

    public Camera getCamera() {
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

    public SaveManager getSaveManager() {
        return saveManager;
    }

    private void resetCollisionChecker() {
        cChecker = new CollisionChecker(this);
    }

    public void resetInteractionRouter() {
        if (em != null && em.getPlayer() != null) {
            iR = new Interact(this, em.getPlayer(), getInputController());
        }
    }

    private void tickFrameCounter() {
        frameCounter++;
        if (frameCounter >= 1_000_000) {
            frameCounter = 0;
        }
    }

    public void restartGame() {
        setCurrentMap(3);
        chunkM.loadMap("map3");
        em.getPlayer().setDefaultValues();
        em.getPlayer().refillHP();
        em.getPlayer().setMapIndex(getCurrentMap());
        em.getPlayer().setLevel(1);
        em.getPlayer().setExp(0);

        object_data.weapons.Weapon defaultWeapon = new object_data.weapons.Sword(this, getCurrentMap());
        em.getPlayer().equipWeapon(defaultWeapon);
        if (om != null) {
            om.reloadMapObjects(getCurrentMap());
        }
        resetCollisionChecker();
        resetInteractionRouter();
        em.update(getCurrentMap());
        gsm.setState(GameState.PLAY);
    }

    public void startGameThread() {
        gameThread = new Thread(new GameLoop(this));
        gameThread.start();
    }

    // ===== UPDATE LOOP =====
    public void update() {
        switch (gsm.getState()) {
            case START, GAME_OVER -> uiManager.update(gsm.getState());

            case PLAY -> {
                chunkM.updateChunks(em.getPlayer().getWorldX(), em.getPlayer().getWorldY());
                setCurrentMap(uTool.mapNameToIndex(chunkM.getMapPath()));
                em.update(getCurrentMap());
                if (em.getPlayer() != null && em.getPlayer().isDead()) {
                    gsm.setState(GameState.GAME_OVER);
                    return;
                }

                uiManager.update(GameState.PLAY);
            }
            case DIALOGUE -> {
                uiManager.update(gsm.getState());
            }
            case PAUSE -> {
                uiManager.update(GameState.PAUSE);
            }
        }
    }

    // ===== DRAW LOOP =====
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Vẽ thế giới + entity
        if (gsm.getState() != GameState.START)
            tileM.draw(g2, chunkM);

        om.draw(g2, getCurrentMap() , em.getPlayer());
        em.draw(g2, getCurrentMap());
        uiManager.draw(g2, gsm.getState());

        tickFrameCounter();
        g2.dispose();
    }
}
