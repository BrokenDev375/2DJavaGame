package interact_manager.object_interact;

import entity_manager.ObjectManager;
import input_manager.InputController;
import main.GamePanel;
import object_data.TeleportDestination;
import object_data.WorldObject;
import object_data.weapons.WeaponType;
import player_manager.Player;
import sound_manager.SoundManager;
import ui.effects.FadeUI;
import ui.effects.MessageUI;

import java.util.Objects;

public final class InteractionContext {
    private final GamePanel gp;
    private final Player player;
    private final InputController input;
    private final WorldObject object;

    public InteractionContext(GamePanel gp, Player player, InputController input, WorldObject object) {
        this.gp = Objects.requireNonNull(gp);
        this.player = Objects.requireNonNull(player);
        this.input = Objects.requireNonNull(input);
        this.object = Objects.requireNonNull(object);
    }

    public Player player() {
        return player;
    }

    public WorldObject object() {
        return object;
    }

    public boolean isPicked() {
        return input.isPicked();
    }

    public int currentMap() {
        return gp.getCurrentMap();
    }

    public int tileSize() {
        return gp.tileSize;
    }

    public ObjectManager objectManager() {
        return gp.getObjectManager();
    }

    public void showMessage(String message) {
        MessageUI msgUI = gp.getUiManager().get(MessageUI.class);
        if (msgUI != null) {
            msgUI.showTouchMessage(message, object, gp);
        }
    }

    public void playCoinSound() {
        SoundManager.getInstance().playSE(SoundManager.SoundID.COIN);
    }

    public void removeTouchedObject() {
        objectManager().removeObject(currentMap(), object);
    }

    public void equipWeapon(WeaponType type) {
        player.equipWeapon(gp.getWeaponFactory().create(type, currentMap()));
    }

    public void transitionTo(TeleportDestination destination, String completeMessage) {
        if (destination == null) {
            return;
        }

        FadeUI fadeUI = gp.getUiManager().get(FadeUI.class);
        if (fadeUI == null) {
            return;
        }

        fadeUI.startFade(() -> {
            gp.getChunkManager().loadMap(destination.mapPath());
            gp.setCurrentMap(destination.mapId());
            player.moveTo(destination.worldX(), destination.worldY());
            player.setMapIndex(destination.mapId());
            gp.getChunkManager().clearChunks();
            gp.getChunkManager().updateChunks(player.getWorldX(), player.getWorldY());
            showMessage(completeMessage);
        });
    }
}
