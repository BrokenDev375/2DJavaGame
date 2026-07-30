package entity;

import combat.*;
import main.DebugLog;
import main.GamePanel;
import ui.effects.DialogueUI;

import java.awt.*;
import java.awt.image.BufferedImage;
import ai.movement.MovementController;
import ai.movement.MovementIntent;
import world.WorldBody;

/** Base class for all world entities (player, monsters, NPCs). */
public class Entity implements CombatContext, WorldBody {

    private final MapPlacement placement = new MapPlacement();
    private final EntitySize size = new EntitySize();

    // --- animation ---
    private final EntitySprites sprites = new EntitySprites();
    private Direction direction = Direction.DOWN;
    private Direction attackDir = Direction.DOWN;
    private final EntityAnimationState animationState = new EntityAnimationState();

    // --- collision ---
    private final EntityCollision collisionState = new EntityCollision();
    private boolean collidable = false;

    // --- state / stats ---
    private String name;
    private int defaultSpeed, actualSpeed, buffSpeed;
    private boolean animationOn = false;

    private final EntityStats stats = new EntityStats();
    private final DamageState damageState = new DamageState();
    private final KnockbackState knockbackState = new KnockbackState();


    // --- systems/manager ---
    protected final GamePanel gp;
    private final EntityMovement emo;
    private final EntitySpriteManager esm;
    private final EntityDraw ed;

    // === Dialogue System ===
    private final DialogueComponent dialogue = new DialogueComponent(20, 20);

    // --- Combat ECS ---
    private final CombatComponent combat;

    // --- Movement Controller (AI / input strategy) ---
    private MovementController controller;
    private boolean hasAttackAnimation = false;

    public Entity(GamePanel gp) {
        this.gp = gp;

        this.emo = new EntityMovement(gp);
        this.esm = new EntitySpriteManager(gp.getAssetLoader());
        this.ed = new EntityDraw(gp);

        this.combat = new CombatComponent();
        this.attackDir = this.direction;  // init mặc định
    }

    // -------- controller ----------
    public void setController(MovementController c) {
        this.controller = c;
    }

    public MovementController getController() {
        return controller;
    }

    private void applyMovementIntent(MovementIntent intent) {
        if (intent == null || !intent.isMoving()) {
            stopMoving();
            return;
        }
        face(intent.getDirection());
        useMovementSpeed(intent.getSpeed());
        emo.moveByDirection(this);
    }

    // -------- movement behavior ----------
    public void face(Direction newDirection) {
        if (newDirection != null) {
            this.direction = newDirection;
        }
    }

    public void lockAttackDirection() {
        this.attackDir = this.direction;
    }

    public Direction getAttackDirection() {
        return attackDir;
    }

    public void useMovementSpeed(int speed) {
        this.actualSpeed = Math.max(0, speed);
    }

    public void useAtLeastMovementSpeed(int speed) {
        useMovementSpeed(Math.max(actualSpeed, Math.max(0, speed)));
    }

    public void stopMoving() {
        this.actualSpeed = 0;
    }

    public void setDefaultMovementSpeed(int speed) {
        this.defaultSpeed = Math.max(0, speed);
    }

    public void resetMovementSpeed() {
        useMovementSpeed(defaultSpeed);
    }

    public int getDefaultMovementSpeed() {
        return defaultSpeed;
    }

    public int getActualSpeed() {
        return actualSpeed;
    }

    public void setBuffSpeed(int speed) {
        this.buffSpeed = Math.max(0, speed);
    }

    public int getBuffSpeed() {
        return buffSpeed;
    }

    public void moveBy(int dx, int dy) {
        placement.moveBy(dx, dy);
    }

    public void moveTo(int x, int y) {
        setWorldPosition(x, y);
    }

    public void spawnAt(int x, int y) {
        setWorldPosition(x, y);
    }

    public void restorePosition(int x, int y) {
        setWorldPosition(x, y);
    }

    private void setWorldPosition(int x, int y) {
        placement.moveTo(x, y);
    }

    public boolean canMove() {
        return collisionState.canMove();
    }

    public boolean canMoveOnX() {
        return collisionState.canMoveOnX();
    }

    public boolean canMoveOnY() {
        return collisionState.canMoveOnY();
    }

    public boolean wasBlockedByCollision() {
        return collisionState.wasBlockedByCollision();
    }

    public void clearCollisionState() {
        collisionState.clearCollisionState();
    }

    public void clearCollisionXState() {
        collisionState.clearCollisionXState();
    }

    public void clearCollisionYState() {
        collisionState.clearCollisionYState();
    }

    public void markCollision() {
        collisionState.markCollision();
    }

    public void markCollisionX() {
        collisionState.markCollisionX();
    }

    public void markCollisionY() {
        collisionState.markCollisionY();
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    public boolean isAnimationOn() {
        return animationOn;
    }

    public void setAnimationOn(boolean animationOn) {
        this.animationOn = animationOn;
    }

    public boolean hasAttackAnimation() {
        return hasAttackAnimation;
    }

    public void setHasAttackAnimation(boolean hasAttackAnimation) {
        this.hasAttackAnimation = hasAttackAnimation;
    }

    protected void useSpriteProfile(EntitySpriteProfile profile) {
        if (profile != null) {
            profile.applyTo(sprites);
        }
    }

    protected void setMoveSprites(Direction direction, BufferedImage firstFrame, BufferedImage secondFrame) {
        sprites.setMoveSprites(direction, firstFrame, secondFrame);
    }

    protected void setAttackSprites(Direction direction, BufferedImage firstFrame, BufferedImage secondFrame) {
        sprites.setAttackSprites(direction, firstFrame, secondFrame);
    }

    protected void useMoveSpritesForAttack() {
        sprites.useMoveSpritesForAttack();
    }

    protected BufferedImage getMoveSprite(Direction direction) {
        return sprites.getMoveSprite(direction, isFirstSpriteFrame());
    }

    protected BufferedImage getAttackSprite(Direction direction) {
        return sprites.getAttackSprite(direction, isFirstSpriteFrame());
    }

    protected void setStaticImage(BufferedImage image) {
        sprites.setStaticImage(image);
    }

    protected BufferedImage getStaticImage() {
        return sprites.getStaticImage();
    }

    protected void advanceSpriteFrame(int frameDelay) {
        animationState.advanceFrame(frameDelay);
    }

    protected void resetSpriteFrame() {
        animationState.resetFrame();
    }

    private boolean isFirstSpriteFrame() {
        return animationState.isFirstFrame();
    }

    protected boolean isAttacking() {
        return CombatSystem.isAttacking(combat);
    }

    protected boolean isAttackActive() {
        return CombatSystem.isAttackActive(combat);
    }

    public boolean isAttackHitting(WorldBody target) {
        return CombatSystem.isAttackHitting(combat, target);
    }

    public boolean tryLandAttackOn(WorldBody target) {
        return CombatSystem.tryLandAttackOn(combat, target);
    }

    protected boolean canStartAttack() {
        return CombatSystem.canStartAttack(combat);
    }

    protected void startAttack() {
        CombatSystem.startAttack(combat, this);
    }

    public void tickCombat() {
        CombatSystem.update(combat, this);
        CombatSystem.updateStatus(this);
    }

    protected void configureAttackBox(int width, int height) {
        CombatSystem.configureAttackBox(combat, width, height);
    }

    protected void configureAttackTiming(int windup, int active, int recover, int cooldown) {
        CombatSystem.configureAttackTiming(combat, windup, active, recover, cooldown);
    }

    protected int getAttackKnockbackForce() {
        return CombatSystem.getKnockbackForce(combat);
    }

    protected void setAttackKnockbackForce(int force) {
        CombatSystem.setKnockbackForce(combat, force);
    }

    public Entity getLastHitBy() {
        return damageState.getLastHitBy();
    }

    public void markHitBy(Entity attacker) {
        damageState.markHitBy(attacker);
    }

    public void clearLastHitBy() {
        damageState.clearLastHitBy();
    }

    protected void defineDialogueLine(int setIndex, int lineIndex, String text) {
        dialogue.defineLine(setIndex, lineIndex, text);
    }

    protected void chooseDialogueSet(int setIndex) {
        dialogue.chooseSet(setIndex);
    }

    protected void startCurrentDialogue(GamePanel gp) {
        if (gp == null || gp.getUiManager() == null) return;
        gp.getUiManager().get(DialogueUI.class).startDialogue(dialogue.getCurrentSet());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMapIndex() {
        return placement.getMapIndex();
    }

    public void setMapIndex(int mapIndex) {
        placement.setMapIndex(mapIndex);
    }

    public boolean isOnMap(int mapIndex) {
        return placement.isOnMap(mapIndex);
    }

    public void setSize(int width, int height) {
        size.set(width, height);
    }

    public int getWidth() {
        return size.getWidth();
    }

    public int getHeight() {
        return size.getHeight();
    }

    public void setSolidArea(Rectangle area) {
        collisionState.setSolidArea(area);
    }

    public Rectangle getSolidAreaAt(int worldX, int worldY) {
        return collisionState.getSolidAreaAt(worldX, worldY, getWidth(), getHeight());
    }

    public Rectangle getSolidAreaWorld() {
        return getSolidAreaAt(getWorldX(), getWorldY());
    }

    // -------- stats ----------
    public void setStats(int maxHp, int atk, int def) {
        stats.set(maxHp, atk, def);
    }

    public int getHP() {
        return stats.hp();
    }

    public int getMaxHP() {
        return stats.maxHp();
    }

    public int getATK() {
        return stats.attack();
    }

    public int getDEF() {
        return stats.defense();
    }

    public void restoreHP(int value) {
        stats.restoreHp(value);
    }

    public void refillHP() {
        stats.refillHp();
    }

    public void heal(int amount) {
        stats.heal(amount);
    }

    public void healPercent(double percent) {
        stats.healPercent(percent);
    }

    public void kill() {
        stats.kill();
    }

    protected void reduceHP(int amount) {
        int dmg = Math.max(0, amount);
        int old = stats.reduceHp(dmg);

        DebugLog.info("[HP] " + name +
                " -" + dmg +
                " (" + old + " -> " + getHP() + ")");
    }

    public boolean takeDamage(Entity attacker, int rawDamage, int knockbackX, int knockbackY) {
        if (isDead() || isInvulnerable()) return false;

        int damage = stats.damageAfterDefense(rawDamage);
        markHitBy(attacker);
        reduceHP(damage);
        startInvulnerability();
        startKnockback(knockbackX, knockbackY);
        return true;
    }

    public boolean takeDamage(int rawDamage, int knockbackX, int knockbackY) {
        return takeDamage(null, rawDamage, knockbackX, knockbackY);
    }

    public void setInvulnFrames(int frames) {
        damageState.setInvulnFrames(frames);
    }

    @Override
    public boolean isDead() {
        return stats.isDead();
    }

    public boolean isInvulnerable() {
        return damageState.isInvulnerable();
    }

    public void startInvulnerability() {
        damageState.startInvulnerability();
    }

    public void tickInvulnerability() {
        damageState.tickInvulnerability();
    }

    public boolean isKnockbackActive() {
        return knockbackState.isActive();
    }

    public boolean isKnockbackFinished() {
        return knockbackState.isFinished();
    }

    public void setKnockbackFrames(int f) {
        knockbackState.setDurationFrames(f);
    }

    public void tickKnockbackDuration() {
        knockbackState.tickDuration();
    }

    public void finishKnockback() {
        knockbackState.finish();
    }

    int knockbackVelocityX() {
        return knockbackState.velocityX();
    }

    int knockbackVelocityY() {
        return knockbackState.velocityY();
    }

    boolean hasKnockbackVelocity() {
        return knockbackState.hasVelocity();
    }

    void stopKnockbackVelocityX() {
        knockbackState.stopVelocityX();
    }

    void stopKnockbackVelocityY() {
        knockbackState.stopVelocityY();
    }

    @Override
    public int getWorldX() {
        return placement.getWorldX();
    }

    @Override
    public int getWorldY() {
        return placement.getWorldY();
    }

    @Override
    public Rectangle getSolidArea() {
        return collisionState.getSolidArea(getWidth(), getHeight());
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    protected int[] computeDelta() {
        return new int[]{
                direction.scaledDx(getActualSpeed()),
                direction.scaledDy(getActualSpeed())
        };
    }

    protected void applyKnockbackMovement() {
        emo.applyKnockback(this);
    }

    public void update() {
        // Nếu đang KB → chỉ đẩy; bỏ qua input/AI ở frame này
        if (isKnockbackActive()) {
            applyKnockbackMovement();
        } else {
            if (controller != null) {
                applyMovementIntent(controller.decide(this));
            } else {
                int[] d = computeDelta();
                emo.moveWithDelta(this, d[0], d[1]);
            }
        }

        CombatSystem.tick(this);     // (giữ như cũ)
        StatusSystem.update(this);   // chỉ timers (i-frames…), KHÔNG translate
        esm.updateSprite(this);
    }


    public void draw(Graphics2D g2) {
        ed.draw(g2, this);
    }

    protected BufferedImage setup(String path, int w, int h) {
        return esm.loadSprite(path, w, h);
    }

    public void startKnockback(int kbX, int kbY, int durationFrames) {
        knockbackState.start(kbX, kbY, durationFrames);
    }

    public void startKnockback(int kbX, int kbY) {
        knockbackState.start(kbX, kbY);
    }

    // --- Save/Load support ---

    public void revive() {
        clearLastHitBy();
        refillHP();
    }

}
