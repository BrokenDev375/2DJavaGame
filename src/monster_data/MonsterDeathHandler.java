package monster_data;

import main.DebugLog;
import main.GamePanel;
import object_data.ObjectDropRequest;
import player_manager.Player;
import player_manager.PlayerProgressionNotifier;
import player_manager.PlayerProgressionResult;

public final class MonsterDeathHandler {
    private static final PlayerProgressionNotifier PROGRESSION_NOTIFIER = new PlayerProgressionNotifier();

    private MonsterDeathHandler() {}

    public static void apply(GamePanel gp, MonsterDeathResult result) {
        if (result == null) {
            return;
        }

        DebugLog.info(result.deathLog());
        awardExp(gp, result);
        logPotionRoll(result);
        spawnDrops(gp, result);
    }

    private static void awardExp(GamePanel gp, MonsterDeathResult result) {
        Player player = gp != null && gp.getEntityManager() != null
                ? gp.getEntityManager().getPlayer()
                : null;
        if (player == null) {
            DebugLog.info("[EXP] player not found");
            return;
        }

        DebugLog.info("[EXP] +" + result.expReward() + " for " + result.monsterName());
        PlayerProgressionResult progressionResult = player.gainExp(result.expReward());
        PROGRESSION_NOTIFIER.showLevelUp(gp, progressionResult);
    }

    private static void logPotionRoll(MonsterDeathResult result) {
        result.healthPotionDrop().ifPresent(drop -> {
            DebugLog.info("[DROP] roll=" + drop.roll());
            DebugLog.info(drop.dropped() ? "[DROP] health potion" : "[DROP] none");
        });
    }

    private static void spawnDrops(GamePanel gp, MonsterDeathResult result) {
        if (gp == null || gp.getObjectManager() == null) {
            return;
        }

        for (ObjectDropRequest request : result.dropRequests()) {
            gp.getObjectManager().spawnDrop(request);
        }
    }
}
