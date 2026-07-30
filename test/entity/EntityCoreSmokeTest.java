package entity;

import combat.DamageFormula;
import entity_manager.MonsterSpawnPlan;
import game_data.GameData;
import game_data.LoadResult;
import game_data.ObjectData;
import game_data.PlayerData;
import game_data.SaveManager;
import game_data.SaveRepository;
import main.AssetLoadException;
import main.AssetLoader;
import interact_manager.object_interact.ObjectInteractionFactory;
import main.CollisionChecker;
import main.GameConfig;
import monster_data.LootDropPolicy;
import monster_data.LootDropResult;
import monster_data.MonsterAttackPlanner;
import monster_data.MonsterDeathResult;
import monster_data.MonsterFactory;
import monster_data.MonsterType;
import object_data.ObjectDropRequest;
import object_data.ObjectSpawnPlan;
import object_data.TeleportDestination;
import object_data.WorldObjectSpawnTable;
import object_data.WorldObjectType;
import object_data.weapons.WeaponFactory;
import object_data.weapons.WeaponType;
import player_manager.PlayerCombatInput;
import player_manager.PlayerProgression;
import player_manager.PlayerProgressionResult;
import player_manager.PlayerProgressionStats;
import world.WorldBody;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class EntityCoreSmokeTest {
    private EntityCoreSmokeTest() {}

    public static void main(String[] args) throws Exception {
        gameConfigDerivesStableDimensions();
        directionDeltaAndOpposite();
        mapPlacementTracksPositionAndMap();
        entitySizeClampsToPositiveValues();
        entityStatsClampHealthAndDamage();
        damageFormulaUsesRatioMitigation();
        damageStateTracksInvulnerabilityAndAttacker();
        knockbackStateTracksVelocityAndDuration();
        animationFrameAdvancesAndResets();
        collisionAreaUsesDefensiveCopies();
        collisionContractsUseNamedMissValue();
        objectInteractionFactoryReturnsOptional();
        objectCreationTypesResolveKnownIds();
        monsterFactoryMatchesSavedNames();
        weaponFactoryResolvesNamesWithoutGamePanel();
        objectSpawnTableKeepsDefaultPlans();
        playerProgressionCalculatesStatsAndLevels();
        playerCombatInputDebouncesAttack();
        lootDropPolicyUsesInjectedRandom();
        monsterDeathResultCollectsRewardsAndDrops();
        monsterAttackPlannerCalculatesReachAndFacing();
        saveRepositoryRoundTripsGameData();
        saveManagerReportsMissingAndCorruptedSave();
        assetLoaderReportsMissingAssets();
        worldBodyProjectsSolidAreaIntoWorldSpace();
    }

    private static void gameConfigDerivesStableDimensions() {
        GameConfig config = GameConfig.defaults();

        assertEquals(16, config.originalTileSize(), "original tile size");
        assertEquals(3, config.scale(), "scale");
        assertEquals(48, config.tileSize(), "tile size");
        assertEquals(1200, config.screenWidth(), "screen width");
        assertEquals(672, config.screenHeight(), "screen height");
        assertEquals(32, config.chunkSize(), "chunk size");
        assertEquals(3, config.numMaps(), "map count");
    }

    private static void directionDeltaAndOpposite() {
        assertEquals(0, Direction.UP.dx(), "UP dx");
        assertEquals(-1, Direction.UP.dy(), "UP dy");
        assertEquals(-12, Direction.LEFT.scaledDx(12), "LEFT scaled dx");
        assertEquals(12, Direction.DOWN.scaledDy(12), "DOWN scaled dy");
        assertSame(Direction.RIGHT, Direction.LEFT.opposite(), "LEFT opposite");
        assertSame(Direction.UP, Direction.DOWN.opposite(), "DOWN opposite");
    }

    private static void animationFrameAdvancesAndResets() {
        EntityAnimationState state = new EntityAnimationState();

        assertTrue(state.isFirstFrame(), "starts on first frame");
        state.advanceFrame(1);
        assertTrue(state.isFirstFrame(), "delay keeps first frame");
        state.advanceFrame(1);
        assertFalse(state.isFirstFrame(), "frame toggles after delay");

        state.resetFrame();
        assertTrue(state.isFirstFrame(), "reset returns to first frame");
    }

    private static void mapPlacementTracksPositionAndMap() {
        MapPlacement placement = new MapPlacement();

        placement.moveTo(10, 20);
        placement.moveBy(3, -4);
        assertEquals(13, placement.getWorldX(), "world x");
        assertEquals(16, placement.getWorldY(), "world y");

        assertTrue(placement.isOnMap(0), "default map");
        placement.placeOnMap(2);
        assertFalse(placement.isOnMap(0), "old map");
        assertTrue(placement.isOnMap(2), "new map");
    }

    private static void damageStateTracksInvulnerabilityAndAttacker() {
        DamageState damage = new DamageState();

        assertFalse(damage.isInvulnerable(), "starts vulnerable");
        damage.configureInvulnerabilityFrames(2);
        damage.startInvulnerability();
        assertTrue(damage.isInvulnerable(), "starts invulnerability");

        damage.tickInvulnerability();
        assertTrue(damage.isInvulnerable(), "keeps invulnerability before counter ends");
        damage.tickInvulnerability();
        assertFalse(damage.isInvulnerable(), "ends invulnerability");

        damage.markHitBy(null);
        assertSame(null, damage.getLastHitBy(), "tracks null attacker");
        damage.clearLastHitBy();
        assertSame(null, damage.getLastHitBy(), "clears attacker");
    }

    private static void knockbackStateTracksVelocityAndDuration() {
        KnockbackState knockback = new KnockbackState();

        assertFalse(knockback.isActive(), "starts inactive");
        knockback.configureDurationFrames(2);
        knockback.start(3, -4);

        assertTrue(knockback.isActive(), "starts knockback");
        assertTrue(knockback.hasVelocity(), "tracks velocity");
        assertEquals(3, knockback.velocityX(), "velocity x");
        assertEquals(-4, knockback.velocityY(), "velocity y");

        knockback.stopVelocityX();
        assertEquals(0, knockback.velocityX(), "stops x velocity");
        assertEquals(-4, knockback.velocityY(), "keeps y velocity");

        knockback.tickDuration();
        assertTrue(knockback.isActive(), "still active before duration ends");
        knockback.tickDuration();
        assertTrue(knockback.isFinished(), "duration ends");

        knockback.finish();
        assertFalse(knockback.hasVelocity(), "finish clears velocity");
    }

    private static void entitySizeClampsToPositiveValues() {
        EntitySize size = new EntitySize();

        assertEquals(1, size.getWidth(), "default width");
        assertEquals(1, size.getHeight(), "default height");

        size.resizeTo(0, -4);
        assertEquals(1, size.getWidth(), "clamped width");
        assertEquals(1, size.getHeight(), "clamped height");

        size.resizeTo(32, 48);
        assertEquals(32, size.getWidth(), "custom width");
        assertEquals(48, size.getHeight(), "custom height");
    }

    private static void entityStatsClampHealthAndDamage() {
        EntityStats stats = new EntityStats();

        stats.configure(20, 5, 2);
        assertEquals(20, stats.hp(), "set fills health");
        assertEquals(20, stats.maxHp(), "max health");
        assertEquals(5, stats.attack(), "attack");
        assertEquals(2, stats.defense(), "defense");

        assertEquals(4, stats.damageAfterDefense(5), "damage applies ratio defense");
        assertEquals(1, stats.damageAfterDefense(0), "damage minimum");

        int oldHp = stats.reduceHp(7);
        assertEquals(20, oldHp, "reduce returns old health");
        assertEquals(13, stats.hp(), "reduce health");

        stats.healPercent(0.10);
        assertEquals(15, stats.hp(), "percent heal");

        stats.restoreHp(1_000);
        assertEquals(20, stats.hp(), "restore clamps to max");

        stats.kill();
        assertTrue(stats.isDead(), "kill marks dead");
    }

    private static void damageFormulaUsesRatioMitigation() {
        assertEquals(10, DamageFormula.afterDefense(10, 0), "zero defense keeps raw damage");
        assertEquals(7, DamageFormula.afterDefense(10, 5), "defense mitigates by ratio");
        assertEquals(1, DamageFormula.afterDefense(0, 5), "damage floor");
        assertEquals(5, DamageFormula.afterDefense(5, -4), "negative defense clamps");
    }

    private static void collisionAreaUsesDefensiveCopies() {
        EntityCollision collision = new EntityCollision();
        collision.defineSolidArea(new Rectangle(4, 5, 10, 11));

        Rectangle local = collision.getSolidArea(99, 99);
        local.x = 100;

        assertRect(collision.getSolidArea(99, 99), 4, 5, 10, 11, "local solid area copy");
        assertRect(collision.getSolidAreaAt(20, 30, 99, 99), 24, 35, 10, 11, "world solid area");

        assertTrue(collision.canMove(), "default can move");
        collision.markCollisionX();
        assertFalse(collision.canMove(), "x collision blocks movement");
        collision.clearCollisionXState();
        assertTrue(collision.canMove(), "clearing x restores movement");
    }

    private static void collisionContractsUseNamedMissValue() {
        assertEquals(-1, CollisionChecker.NO_HIT, "collision miss contract");
    }

    private static void objectInteractionFactoryReturnsOptional() {
        assertFalse(ObjectInteractionFactory.getHandler((WorldObjectType) null).isPresent(), "null object handler");
        assertFalse(ObjectInteractionFactory.getHandler(WorldObjectType.SHOP).isPresent(), "object without handler");
        assertTrue(ObjectInteractionFactory.getHandler(WorldObjectType.KEY).isPresent(), "known object handler");

        Object keyHandler = ObjectInteractionFactory.getHandler(WorldObjectType.KEY).orElse(null);
        assertSame(
                keyHandler,
                ObjectInteractionFactory.getHandler(WorldObjectType.KEY).orElse(null),
                "object handler is cached"
        );
    }

    private static void objectCreationTypesResolveKnownIds() {
        assertSame(MonsterType.SLIME, MonsterType.fromId("slime").orElse(null), "monster type id lookup");
        assertFalse(MonsterType.fromId("missing").isPresent(), "unknown monster type id");

        assertSame(WeaponType.SWORD, WeaponType.fromName("Argonaut Hero's Sword").orElse(null), "weapon display lookup");
        assertSame(WeaponType.PICK, WeaponType.fromName("Steve Pick").orElse(null), "weapon alias lookup");
        assertFalse(WeaponType.fromName("missing").isPresent(), "unknown weapon name");

        MonsterSpawnPlan plan = new MonsterSpawnPlan(1, 20, 30, MonsterType.BAT, 25_000L);
        assertSame(MonsterType.BAT, plan.monsterType(), "monster spawn plan keeps type");
        assertEquals(1, plan.mapId(), "monster spawn plan keeps map");
        assertEquals(20, plan.worldX(), "monster spawn plan keeps x");
        assertEquals(30, plan.worldY(), "monster spawn plan keeps y");
    }

    private static void monsterFactoryMatchesSavedNames() {
        assertTrue(MonsterFactory.matchesSavedName("Green Slime", "green_slime"), "green slime saved identity");
        assertTrue(MonsterFactory.matchesSavedName("RedSlime", "red slime"), "red slime display identity");
        assertTrue(MonsterFactory.matchesSavedName("RedSlime", "SLIME"), "generic slime accepts variant");
        assertTrue(MonsterFactory.matchesSavedName("Skeleton Lord", "boss"), "boss alias identity");
        assertFalse(MonsterFactory.matchesSavedName("RedSlime", "Green Slime"), "different slime variant");
        assertFalse(MonsterFactory.matchesSavedName("Bat", "Orc"), "different monster identity");
    }

    private static void weaponFactoryResolvesNamesWithoutGamePanel() {
        assertSame(WeaponType.SWORD, WeaponFactory.resolveType("Argonaut Hero's Sword").orElse(null), "weapon factory sword lookup");
        assertSame(WeaponType.PICK, WeaponFactory.resolveType("Steve Pick").orElse(null), "weapon factory alias lookup");
        assertFalse(WeaponFactory.resolveType("missing").isPresent(), "weapon factory unknown name");
    }

    private static void objectSpawnTableKeepsDefaultPlans() {
        List<ObjectSpawnPlan> plans = WorldObjectSpawnTable.defaultPlans(48);

        assertEquals(7, plans.size(), "default object spawn count");
        ObjectSpawnPlan first = plans.get(0);
        assertSame(WorldObjectType.SHOP, first.type(), "first object spawn type");
        assertEquals(0, first.mapId(), "first object spawn map");
        assertEquals(46 * 48, first.worldX(), "first object spawn x");
        assertEquals(15 * 48, first.worldY(), "first object spawn y");

        ObjectSpawnPlan door = plans.get(1);
        assertSame(WorldObjectType.DOOR, door.type(), "door spawn type");
        assertTrue(door.teleportDestination().isPresent(), "door has destination");
        TeleportDestination doorDestination = door.teleportDestination().orElse(null);
        assertEquals(3, doorDestination.mapId(), "door destination map");
        assertEquals(15 * 48 + 22, doorDestination.worldX(), "door destination x");
        assertEquals(23 * 48, doorDestination.worldY(), "door destination y");

        ObjectSpawnPlan portal = plans.get(2);
        assertSame(WorldObjectType.PORTAL, portal.type(), "portal spawn type");
        assertTrue(portal.teleportDestination().isPresent(), "portal has destination");
        TeleportDestination portalDestination = portal.teleportDestination().orElse(null);
        assertEquals(1, portalDestination.mapId(), "portal destination map");
        assertEquals(47 * 48 + 12, portalDestination.worldX(), "portal destination x");
        assertEquals(47 * 48 + 12 + 48, portalDestination.worldY(), "portal destination y offset");
    }

    private static void playerProgressionCalculatesStatsAndLevels() {
        PlayerProgression progression = new PlayerProgression();

        PlayerProgressionStats levelOne = progression.statsForCurrentLevel();
        assertEquals(15, levelOne.maxHp(), "level one hp");
        assertEquals(3, levelOne.attack(), "level one attack");
        assertEquals(2, levelOne.defense(), "level one defense");

        PlayerProgressionResult firstLevelUp = progression.gainExp(10);
        assertTrue(firstLevelUp.leveledUp(), "level up at threshold");
        assertEquals(2, progression.level(), "level after threshold");
        assertEquals(0, progression.exp(), "exp rolls over at threshold");
        assertEquals(PlayerProgression.calcExpToNext(2), progression.expToNext(), "level two threshold");

        PlayerProgressionStats levelTwo = progression.statsForCurrentLevel();
        assertEquals(18, levelTwo.maxHp(), "level two hp");
        assertEquals(4, levelTwo.attack(), "level two attack");
        assertEquals(3, levelTwo.defense(), "level two defense");

        progression.restore(3, -5);
        assertEquals(3, progression.level(), "set level clamps high value");
        assertEquals(0, progression.exp(), "set exp clamps negative");
        assertEquals(PlayerProgression.calcExpToNext(3), progression.expToNext(), "set level recalculates threshold");
    }

    private static void playerCombatInputDebouncesAttack() {
        PlayerCombatInput input = new PlayerCombatInput(2);

        assertTrue(input.shouldStartAttack(true, true), "first attack starts");
        assertEquals(2, input.attackButtonLock(), "attack lock set");
        assertFalse(input.shouldStartAttack(true, true), "locked attack ignored");
        assertFalse(input.shouldStartAttack(false, true), "lock ticks without attack");
        assertFalse(input.shouldStartAttack(true, false), "cannot start attack still consumes press");
        assertEquals(2, input.attackButtonLock(), "failed start still debounces press");

        input.reset();
        assertEquals(0, input.attackButtonLock(), "reset clears attack lock");
    }

    private static void lootDropPolicyUsesInjectedRandom() {
        LootDropPolicy dropPolicy = new LootDropPolicy(() -> 0.24);
        LootDropResult dropped = dropPolicy.rollHealthPotion(2, 30, 40);

        assertTrue(dropped.dropped(), "roll below chance drops potion");
        ObjectDropRequest potion = dropped.dropRequest().orElse(null);
        assertSame(WorldObjectType.HEALTH_POSION, potion.type(), "potion drop type");
        assertEquals(2, potion.mapIndex(), "potion drop map");
        assertEquals(30, potion.worldX(), "potion drop x");
        assertEquals(40, potion.worldY(), "potion drop y");

        LootDropPolicy noDropPolicy = new LootDropPolicy(() -> LootDropPolicy.HEALTH_POTION_DROP_CHANCE);
        assertFalse(noDropPolicy.rollHealthPotion(0, 0, 0).dropped(), "threshold roll does not drop");

        ObjectDropRequest sword = dropPolicy.guaranteed(WorldObjectType.SWORD, 1, 5, 6);
        assertSame(WorldObjectType.SWORD, sword.type(), "guaranteed drop type");
    }

    private static void monsterDeathResultCollectsRewardsAndDrops() {
        LootDropResult potionDrop = new LootDropPolicy(() -> 0.0).rollHealthPotion(1, 10, 20);
        MonsterDeathResult result = MonsterDeathResult.of("Green Slime", 4, potionDrop)
                .withDrop(ObjectDropRequest.of(WorldObjectType.SWORD, 1, 10, 20));

        assertEquals("Green Slime", result.monsterName(), "death result monster name");
        assertEquals(4, result.expReward(), "death result exp reward");
        assertEquals("[DEATH] Green Slime", result.deathLog(), "death result log");
        assertEquals(2, result.dropRequests().size(), "death result combines drops");
        assertSame(WorldObjectType.HEALTH_POSION, result.dropRequests().get(0).type(), "death potion drop");
        assertSame(WorldObjectType.SWORD, result.dropRequests().get(1).type(), "death extra drop");
    }

    private static void monsterAttackPlannerCalculatesReachAndFacing() {
        MonsterAttackPlanner planner = new MonsterAttackPlanner();
        WorldBody attacker = new FakeBody(100, 100, new Rectangle(0, 0, 48, 48));
        WorldBody nearRight = new FakeBody(150, 100, new Rectangle(0, 0, 48, 48));
        WorldBody farRight = new FakeBody(260, 100, new Rectangle(0, 0, 48, 48));
        WorldBody above = new FakeBody(100, 30, new Rectangle(0, 0, 48, 48));

        assertTrue(planner.canReachTarget(attacker, Direction.RIGHT, nearRight, 48, 48), "right reach hits target");
        assertFalse(planner.canReachTarget(attacker, Direction.RIGHT, farRight, 48, 48), "right reach misses far target");
        assertSame(Direction.RIGHT, planner.directionToward(attacker, nearRight), "facing right target");
        assertSame(Direction.UP, planner.directionToward(attacker, above), "facing upper target");
    }

    private static void saveRepositoryRoundTripsGameData() throws Exception {
        Path dir = Files.createTempDirectory("codex-save-repo-test");
        Path file = dir.resolve("savegame.json");
        SaveRepository repository = new SaveRepository(file);

        List<ObjectData> monsters = new ArrayList<>();
        monsters.add(new ObjectData("Green Slime", 11, 12, true, 100, 200, 5, 7));
        List<ObjectData> worldObjects = new ArrayList<>();
        worldObjects.add(new ObjectData("KEY", 30, 40, true, 30, 40, 3));
        GameData saved = new GameData(
                new PlayerData(1, 2, 3, 4, "Steve's pick", 5, 6, 7, 2),
                monsters,
                5,
                "map5",
                worldObjects
        );

        repository.save(saved);
        assertTrue(repository.exists(), "save repository writes file");

        GameData loaded = repository.load();
        assertEquals(GameData.CURRENT_VERSION, loaded.getVersion(), "save version");
        assertEquals(1, loaded.getPlayer().getWorldX(), "loaded player x");
        assertEquals(5, loaded.getMapIndex(), "loaded map index");
        assertEquals(2, loaded.getPlayer().getKeyCount(), "loaded key count");
        assertEquals(1, loaded.getObjects().size(), "loaded monster count");
        ObjectData monster = loaded.getObjects().get(0);
        assertEquals(11, monster.getWorldX(), "loaded monster x");
        assertEquals(5, monster.getMapIndex(), "loaded monster map");
        assertEquals(100, monster.getSpawnX(), "loaded monster spawn x");
        assertTrue(monster.hasSpawnIdentity(), "loaded monster spawn identity");
        assertTrue(monster.hasHealth(), "loaded monster has health snapshot");
        assertEquals(7, monster.getHealth(), "loaded monster health");
        assertTrue(loaded.hasWorldObjectSnapshot(), "new save has world object snapshot");
        assertEquals(1, loaded.getWorldObjects().size(), "loaded world object count");
        ObjectData key = loaded.getWorldObjects().get(0);
        assertEquals("KEY", key.getType(), "loaded world object type");
        assertEquals(3, key.getMapIndex(), "loaded world object map");

        Path oldFile = dir.resolve("old-save.json");
        Files.writeString(
                oldFile,
                "{\"player\":{\"worldX\":1,\"worldY\":2,\"health\":3,\"maxHealth\":4,"
                        + "\"weaponName\":null,\"mapIndex\":1,\"exp\":0,\"level\":1},"
                        + "\"objects\":[{\"type\":\"Bat\",\"worldX\":5,\"worldY\":6,"
                        + "\"spawnX\":7,\"spawnY\":8,\"active\":true}],"
                        + "\"mapIndex\":1,\"mapPath\":\"map1\"}"
        );
        GameData oldLoaded = new SaveRepository(oldFile).load();
        assertFalse(oldLoaded.hasWorldObjectSnapshot(), "old save lacks world object snapshot");
        assertFalse(oldLoaded.getObjects().get(0).hasMapIndex(), "old save monster lacks map snapshot");
        assertFalse(oldLoaded.getObjects().get(0).hasHealth(), "old save monster lacks health snapshot");
        assertEquals(GameData.CURRENT_VERSION, oldLoaded.getVersion(), "old save version fallback");

        Files.deleteIfExists(oldFile);
        Files.deleteIfExists(file);
        Files.deleteIfExists(dir);
    }

    private static void saveManagerReportsMissingAndCorruptedSave() throws Exception {
        Path dir = Files.createTempDirectory("codex-save-manager-test");
        Path missingFile = dir.resolve("missing.json");

        SaveManager missingManager = new SaveManager(new SaveRepository(missingFile));
        assertSame(LoadResult.MISSING, missingManager.loadGame(null), "missing save result");

        Path corruptedFile = dir.resolve("corrupted.json");
        Files.writeString(corruptedFile, "{not-json");
        SaveManager corruptedManager = new SaveManager(new SaveRepository(corruptedFile));
        assertSame(LoadResult.CORRUPTED, corruptedManager.loadGame(null), "corrupted save result");

        Files.deleteIfExists(corruptedFile);
        Files.deleteIfExists(missingFile);
        Files.deleteIfExists(dir);
    }

    private static void assetLoaderReportsMissingAssets() throws Exception {
        AssetLoader loader = AssetLoader.defaultLoader();

        assertEquals("/object/key.png", AssetLoader.normalizeImagePath("object/key"), "image path extension");
        assertFalse(
                loader.findImage("/missing/not_real_sprite", "EntityCoreSmokeTest").isPresent(),
                "missing image optional"
        );

        try {
            loader.requireImage("/missing/not_real_sprite", "EntityCoreSmokeTest");
            throw new AssertionError("missing image should throw");
        } catch (AssetLoadException e) {
            assertEquals("/missing/not_real_sprite.png", e.resourcePath(), "missing image path");
            assertTrue(e.getMessage().contains("EntityCoreSmokeTest"), "missing image requester");
        }

        BufferedImage placeholder = AssetLoader.placeholderImage(0, -5);
        assertEquals(1, placeholder.getWidth(), "placeholder width clamps");
        assertEquals(1, placeholder.getHeight(), "placeholder height clamps");
    }

    private static void worldBodyProjectsSolidAreaIntoWorldSpace() {
        WorldBody body = new FakeBody(100, 200, new Rectangle(3, 4, 5, 6));

        assertRect(body.getSolidAreaAt(7, 8), 10, 12, 5, 6, "projected custom point");
        assertRect(body.getSolidAreaWorld(), 103, 204, 5, 6, "projected world position");
    }

    private static void assertRect(Rectangle actual, int x, int y, int width, int height, String label) {
        if (actual.x != x || actual.y != y || actual.width != width || actual.height != height) {
            throw new AssertionError(label + " expected [" + x + "," + y + "," + width + "," + height
                    + "] but was [" + actual.x + "," + actual.y + "," + actual.width + "," + actual.height + "]");
        }
    }

    private static void assertSame(Object expected, Object actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected same instance");
        }
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label);
        }
    }

    private static void assertFalse(boolean condition, String label) {
        if (condition) {
            throw new AssertionError(label);
        }
    }

    private static final class FakeBody implements WorldBody {
        private final int worldX;
        private final int worldY;
        private final Rectangle solidArea;

        private FakeBody(int worldX, int worldY, Rectangle solidArea) {
            this.worldX = worldX;
            this.worldY = worldY;
            this.solidArea = new Rectangle(solidArea);
        }

        @Override
        public int getWorldX() {
            return worldX;
        }

        @Override
        public int getWorldY() {
            return worldY;
        }

        @Override
        public int getWidth() {
            return solidArea.width;
        }

        @Override
        public int getHeight() {
            return solidArea.height;
        }

        @Override
        public Rectangle getSolidArea() {
            return new Rectangle(solidArea);
        }
    }
}
