package player_manager;

import combat.CombatSystem;
import entity.Direction;
import entity.Entity;
import entity.EntitySpriteManager;
import input_manager.InputController;
import interact_manager.Interact;
import main.DebugLog;
import main.GamePanel;
import object_data.weapons.Weapon;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Player extends Entity {
    private Weapon currentWeapon;
    private int keyCount = 0;
    int speedTimer = 0;

    private final PlayerProgression progression = new PlayerProgression();
    private final PlayerCombatInput combatInput = new PlayerCombatInput();

    private final Interact interactionRouter;
    private final InputController inputController;
    private final PlayerSpriteManager psm;
    private final PlayerMovement pm;

    private boolean interacting = false;

    public Player(GamePanel gp, InputController input) {
        super(gp);
        this.interactionRouter = new Interact(gp, this, input);
        this.inputController = input;

        defineSolidArea(new Rectangle(11, 16, 25, 25));
        resetToDefaults();

        psm = new PlayerSpriteManager(gp.getConfig(), new EntitySpriteManager(gp.getAssetLoader()));
        useSpriteProfile(psm.loadSprites());
        pm = new PlayerMovement(this, gp);

        applyProgressionStats();
    }

    public String equippedWeaponName() {
        return currentWeapon == null ? null : currentWeapon.getName();
    }

    public void beginInteraction() {
        this.interacting = true;
    }

    public void endInteraction() {
        this.interacting = false;
    }

    public boolean isInteracting() {
        return interacting;
    }

    public void interactObject(int objectIndex) {
        interactionRouter.InteractObject(objectIndex);
    }

    public void interactMonster(int monsterIndex) {
        interactionRouter.InteractMonster(monsterIndex);
    }

    public void interactNPC(int npcIndex) {
        interactionRouter.InteractNPC(npcIndex);
    }

    public boolean isMoveUpPressed() {
        return inputController.isUpPressed();
    }

    public boolean isMoveDownPressed() {
        return inputController.isDownPressed();
    }

    public boolean isMoveLeftPressed() {
        return inputController.isLeftPressed();
    }

    public boolean isMoveRightPressed() {
        return inputController.isRightPressed();
    }

    public boolean isAttackPressed() {
        return inputController.isAttackPressed();
    }

    public boolean isTalkPressed() {
        return inputController.isTalkPressed();
    }

    public void resetTalkInput() {
        inputController.resetTalkKey();
    }

    public void resetToDefaults() {
        resizeTo(gp.tileSize(), gp.tileSize());
        placeOnMap(3);
        spawnAt(gp.tileSize() * 15, gp.tileSize() * 22);

        configureDefaultMovementSpeed(5);
        configureBuffSpeed(4);
        resetMovementSpeed();
        face(Direction.UP);
        enableAnimation();
    }

    public int getLevel() {
        return progression.level();
    }

    public int getExp() {
        return progression.exp();
    }

    public int getExpToNext() {
        return progression.expToNext();
    }

    public void restoreProgression(int level, int exp) {
        progression.restore(level, exp);
        applyProgressionStats();
    }

    public void resetProgression() {
        restoreProgression(1, 0);
    }

    public void restoreHealthStats(int maxHealth, int health) {
        configureStats(maxHealth, getATK(), getDEF());
        restoreHP(health);
    }

    public void collectKey() {
        keyCount++;
    }

    public int getKeyCount() {
        return keyCount;
    }

    @Override
    public void update() {
        if (isKnockbackActive()) {
            applyKnockbackMovement();
            CombatSystem.tick(this);
            updateSpriteFrame(false, isAttacking());
            return;
        }

        PlayerMoveIntent moveIntent = pm.calculateMovement();
        pm.move(moveIntent);

        updateSpriteFrame(moveIntent.isMoving(), isAttacking());

        if (speedTimer > 0) speedTimer--;
        if (speedTimer == 0) resetMovementSpeed();

        handleAttackInput();
        CombatSystem.tick(this);
        interactionRouter.updateNPCProximity();
    }

    @Override
    public void draw(Graphics2D g2) {
        if (isInvulnerable()) {
            int fc = gp.getFrameCounter();
            boolean visible = (fc / 6) % 2 == 0;
            if (!visible) return;
        }

        BufferedImage image;
        int tempScreenX = gp.getCamera().anchorX();
        int tempScreenY = gp.getCamera().anchorY();

        boolean attacking = isAttacking();
        if (attacking) {
            switch (getDirection()) {
                case UP -> {
                    image = getAttackSprite(Direction.UP);
                    tempScreenY -= gp.tileSize();
                }
                case DOWN -> image = getAttackSprite(Direction.DOWN);
                case LEFT -> {
                    image = getAttackSprite(Direction.LEFT);
                    tempScreenX -= gp.tileSize();
                }
                case RIGHT -> image = getAttackSprite(Direction.RIGHT);
                default -> image = getMoveSprite(Direction.DOWN);
            }
        } else {
            image = getMoveSprite(getDirection());
        }

        if (image == null && attacking) image = getMoveSprite(getDirection());
        if (image == null) image = getMoveSprite(Direction.DOWN);
        if (image == null) return;

        g2.drawImage(image, tempScreenX, tempScreenY, null);
    }

    private void handleAttackInput() {
        if (combatInput.shouldStartAttack(isAttackPressed(), canStartAttack())) {
            startAttack();
            DebugLog.info("Combat started");
        }
    }

    public int[] attackKnockbackVector() {
        final int defaultForce = 3;
        final int maxForce = 3;
        int baseForce = getAttackKnockbackForce() > 0 ? getAttackKnockbackForce() : defaultForce;
        int scaled = (int) Math.round(baseForce * (1.0 + Math.min(Math.max(0, getATK()), 50) * 0.01));

        Direction direction = getDirection() == null ? Direction.RIGHT : getDirection();
        int knockbackX = direction.scaledDx(scaled);
        int knockbackY = direction.scaledDy(scaled);

        return new int[]{
                clamp(knockbackX, -maxForce, maxForce),
                clamp(knockbackY, -maxForce, maxForce)
        };
    }

    public void equipWeapon(Weapon weapon) {
        if (weapon == null) return;
        this.currentWeapon = weapon;

        configureAttackTiming(
                weapon.windup(), weapon.active(), weapon.recover(), weapon.cooldown()
        );
        configureAttackBox(
                weapon.atkBoxW(), weapon.atkBoxH()
        );

        useSpriteProfile(psm.loadAttackSprites(weapon.spriteKey()));
    }

    public PlayerProgressionResult gainExp(int amount) {
        if (amount <= 0) {
            return progression.gainExp(0);
        }

        int beforeExp = progression.exp();
        int beforeExpToNext = progression.expToNext();
        int beforeLevel = progression.level();

        DebugLog.info(
                "[EXP] gainExp +" + amount +
                        " | before: exp=" + beforeExp + "/" + beforeExpToNext +
                        " lv=" + beforeLevel
        );

        PlayerProgressionResult result = progression.gainExp(amount);
        if (result.leveledUp()) {
            applyProgressionStats();
            refillHP();
        }

        DebugLog.info(
                "[EXP] after gainExp: exp=" + progression.exp() + "/" + progression.expToNext() +
                        " lv=" + progression.level()
        );
        return result;
    }

    private void updateSpriteFrame(boolean moving, boolean attacking) {
        if (attacking || moving) {
            advanceSpriteFrame(8);
            return;
        }

        resetSpriteFrame();
    }

    private void applyProgressionStats() {
        PlayerProgressionStats stats = progression.statsForCurrentLevel();
        configureStats(stats.maxHp(), stats.attack(), stats.defense());
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
