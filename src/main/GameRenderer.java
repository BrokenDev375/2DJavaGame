package main;

import entity_manager.EntityManager;
import entity_manager.ObjectManager;
import tile.ChunkManager;
import tile.TileManager;
import ui.base.UIManager;

import java.awt.Graphics2D;

public final class GameRenderer {
    private final TileManager tileManager;
    private final ChunkManager chunkManager;
    private final ObjectManager objectManager;
    private final EntityManager entityManager;
    private final UIManager uiManager;
    private final GameStateManager gameStateManager;
    private final GameSession session;

    public GameRenderer(
            TileManager tileManager,
            ChunkManager chunkManager,
            ObjectManager objectManager,
            EntityManager entityManager,
            UIManager uiManager,
            GameStateManager gameStateManager,
            GameSession session
    ) {
        this.tileManager = tileManager;
        this.chunkManager = chunkManager;
        this.objectManager = objectManager;
        this.entityManager = entityManager;
        this.uiManager = uiManager;
        this.gameStateManager = gameStateManager;
        this.session = session;
    }

    public void draw(Graphics2D g2) {
        GameState state = gameStateManager.getState();

        if (state != GameState.START) {
            tileManager.draw(g2, chunkManager);
        }

        int currentMap = session.getCurrentMap();
        objectManager.draw(g2, currentMap, entityManager.getPlayer());
        entityManager.draw(g2, currentMap);
        uiManager.draw(g2, state);
        session.tickFrameCounter();
    }
}
