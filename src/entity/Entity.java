package entity;

import combat.*;
import main.DebugLog;
import main.GamePanel;

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
    private Direction direction = Direction.DOWN;  // hướng cho di chuyển / AI
    private Direction attackDir = Direction.DOWN;  // hướng đã lock cho animation tấn công
    private final EntityAnimationState animationState = new EntityAnimationState();

    // --- collision ---
    private final EntityCollision collisionState = new EntityCollision();
    private boolean collidable = false;

    // --- state / stats ---
    private String name;
    private int defaultSpeed, actualSpeed, buffSpeed;
    private boolean animationOn = false;

    private int hp = 1, maxHp = 1, atk = 1, def = 0;

    private final DamageState damageState = new DamageState();
    private final KnockbackState knockbackState = new KnockbackState();

    // --- Fields (thêm hoặc giữ nếu đã có) ---

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
    private boolean hasAttackAnimation = false; // default: false (Slime, v.v.)

    public Entity(GamePanel gp) {
        this.gp = gp;

        this.emo = new EntityMovement(gp);
        this.esm = new EntitySpriteManager();
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

    public void setMoveSprites(Direction direction, BufferedImage firstFrame, BufferedImage secondFrame) {
        sprites.setMoveSprites(direction, firstFrame, secondFrame);
    }

    public void setAttackSprites(Direction direction, BufferedImage firstFrame, BufferedImage secondFrame) {
        sprites.setAttackSprites(direction, firstFrame, secondFrame);
    }

    public void useMoveSpritesForAttack() {
        sprites.useMoveSpritesForAttack();
    }

    public BufferedImage getMoveSprite(Direction direction) {
        return sprites.getMoveSprite(direction, isFirstSpriteFrame());
    }

    public BufferedImage getAttackSprite(Direction direction) {
        return sprites.getAttackSprite(direction, isFirstSpriteFrame());
    }

    public void setStaticImage(BufferedImage image) {
        sprites.setStaticImage(image);
    }

    public BufferedImage getStaticImage() {
        return sprites.getStaticImage();
    }

    public void advanceSpriteFrame(int frameDelay) {
        animationState.advanceFrame(frameDelay);
    }

    public void resetSpriteFrame() {
        animationState.resetFrame();
    }

    public boolean isFirstSpriteFrame() {
        return animationState.isFirstFrame();
    }

    public boolean isAttacking() {
        return CombatSystem.isAttacking(combat);
    }

    public boolean isAttackActive() {
        return CombatSystem.isAttackActive(combat);
    }

    public boolean isAttackHitting(WorldBody target) {
        return isAttackActive() && target != null && combat.attackIntersects(target.getSolidAreaWorld());
    }

    public int getAttackPhase() {
        return CombatSystem.getPhase(combat);
    }

    public boolean canStartAttack() {
        return CombatSystem.canStartAttack(combat);
    }

    public void startAttack() {
        CombatSystem.startAttack(combat, this);
    }

    public void tickCombat() {
        CombatSystem.update(combat, this);
        CombatSystem.updateStatus(this);
    }

    public void configureAttackBox(int width, int height) {
        combat.setAttackBoxSize(width, height);
    }

    public void configureAttackTiming(int windup, int active, int recover, int cooldown) {
        combat.setTimingFrames(windup, active, recover, cooldown);
    }

    public boolean wasHitThisSwing(Object target) {
        return combat.wasHitThisSwing(target);
    }

    public void markHitLanded(Object target) {
        combat.markHit(target);
    }

    public void clearHitThisSwing() {
        combat.clearHitThisSwing();
    }

    public int getAttackKnockbackForce() {
        return combat.getKnockbackForce();
    }

    public void setAttackKnockbackForce(int force) {
        combat.setKnockbackForce(force);
    }

    public GamePanel getGamePanel() {
        return gp;
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

    public void defineDialogueLine(int setIndex, int lineIndex, String text) {
        dialogue.defineLine(setIndex, lineIndex, text);
    }

    public void chooseDialogueSet(int setIndex) {
        dialogue.chooseSet(setIndex);
    }

    public int getDialogueSetIndex() {
        return dialogue.getCurrentSetIndex();
    }

    public String[] getCurrentDialogueSet() {
        return dialogue.getCurrentSet();
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
        this.maxHp = Math.max(1, maxHp);
        this.hp = this.maxHp;
        this.atk = Math.max(0, atk);
        this.def = Math.max(0, def);
    }

    public int getHP() {
        return hp;
    }

    public int getMaxHP() {
        return maxHp;
    }

    public int getATK() {
        return atk;
    }

    public int getDEF() {
        return def;
    }

    private void setHP(int value) {
        this.hp = Math.max(0, Math.min(value, maxHp));
    }

    public void restoreHP(int value) {
        setHP(value);
    }

    public void refillHP() {
        restoreHP(maxHp);
    }

    public void heal(int amount) {
        if (amount <= 0) return;
        restoreHP(hp + amount);
    }

    public void healPercent(double percent) {
        if (percent <= 0) return;
        int healAmount = (int) Math.round(maxHp * percent);
        heal(Math.max(1, healAmount));
    }

    public void kill() {
        restoreHP(0);
    }

    protected void reduceHP(int amount) {
        int dmg = Math.max(0, amount);
        int old = hp;
        hp = Math.max(0, hp - dmg);

        DebugLog.info("[HP] " + name +
                " -" + dmg +
                " (" + old + " -> " + hp + ")");
    }

    public boolean takeDamage(Entity attacker, int rawDamage, int knockbackX, int knockbackY) {
        if (isDead() || isInvulnerable()) return false;

        int damage = Math.max(1, rawDamage - getDEF());
        markHitBy(attacker);
        reduceHP(damage);
        startInvulnerability();
        startKnockback(knockbackX, knockbackY);
        onDamaged(damage);
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
        return hp <= 0;
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

    public BufferedImage setup(String path, int w, int h) {
        return esm.setup(path, w, h);
    }

    public void onDamaged(int damage) {
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
        this.hp = Math.max(1, maxHp); // hồi sinh với full máu
    }

    // === Dialogue Support ===
    public void facePlayer() {
        if (gp == null || gp.getEntityManager() == null) return;
        var player = gp.getEntityManager().getPlayer();
        if (player == null) return;
        face(player.getDirection().opposite());
    }

    public void speak(GamePanel gp) {
        // Each NPC subclass overrides this to start its dialogue
    }

}
