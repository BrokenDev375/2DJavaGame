package player_manager;

import combat.CombatSystem;
import entity.Direction;
import entity.Entity;
import interact_manager.Interact;
import input_manager.InputController;
import main.GamePanel;
import main.GameState;
import main.DebugLog;
import ui.effects.MessageUI;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import object_data.weapons.Weapon;

public class Player extends Entity {
    private Weapon currentWeapon;

    public String equippedWeaponName() {
        return currentWeapon == null ? null : currentWeapon.getName();
    }

    private int keyCount = 0;
    int speedTimer = 0;

    // --- Level / EXP ---
    private int level = 1;
    private int exp = 0;
    private int expToNext = 10;

    // Base stats + tăng mỗi level
    private int baseHp = 15;
    private int baseAtk = 3;
    private int baseDef = 2;

    private int hpPerLevel = 3;
    private int atkPerLevel = 1;
    private int defPerLevel = 1;

    // UI
    private MessageUI msgUI;

    // managers
    private final Interact interactionRouter;
    private final InputController inputController;
    private final PlayerSpriteManager psm;
    private final PlayerMovement pm;
    private final PlayerAnimation pa;

    // combat input
    private int attackBtnLock = 0; // chống spam phím

    // --- Interaction debounce (avoid F-key spamming) ---
    private boolean interacting = false;

    public void setInteracting(boolean value) {
        this.interacting = value;
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

    public Player(GamePanel gp, InputController input) {
        super(gp);
        this.interactionRouter = new Interact(gp, this, input);
        this.inputController = input;
        this.msgUI = gp.getUiManager().get(MessageUI.class);

        // default collision hitbox
        setSolidArea(new Rectangle(11, 16, 25, 25));

        setDefaultValues();

        // Managers
        psm = new PlayerSpriteManager(gp);
        psm.loadSprites(this);
        pm = new PlayerMovement(this, gp);
        pa = new PlayerAnimation(this);

        // ---- Combat config (dựa trên level)
        recalcStatsFromLevel();
    }

    public void setDefaultValues() {
        setSize(gp.tileSize, gp.tileSize);
        setMapIndex(3); // rất quan trọng

        // Ví dụ spawn trước cửa map3
        spawnAt(gp.tileSize * 15, gp.tileSize * 22);

        setDefaultMovementSpeed(5);
        setBuffSpeed(4);
        resetMovementSpeed();
        face(Direction.UP);
        setAnimationOn(true);
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getExpToNext() {
        return expToNext;
    }

    public int setLevel(int level) {
        this.level = level;
        recalcStatsFromLevel();
        return level;
    }

    public int setExp(int exp) {
        this.exp = exp;
        return exp;
    }

    public void collectKey() {
        keyCount++;
    }

    public int getKeyCount() {
        return keyCount;
    }

    @Override
    public void update() {
        // === ĐANG BỊ KNOCKBACK → chỉ đẩy & tick hệ thống, bỏ qua input ===
        if (isKnockbackActive()) {
            applyKnockbackMovement();
            CombatSystem.tick(this);
            pa.update(
                    false,
                    isAttacking(),
                    getAttackPhase()
            );
            return;
        }

        PlayerMoveIntent moveIntent = pm.calculateMovement();
        pm.move(moveIntent);

        pa.update(
                moveIntent.isMoving(),
                isAttacking(),
                getAttackPhase()
        );

        if (speedTimer > 0) speedTimer--;
        if (speedTimer == 0) resetMovementSpeed();

        handleAttackInput();
        CombatSystem.tick(this);
        handleNPCInteraction();
    }

    @Override
    public void draw(Graphics2D g2) {
        if (isInvulnerable()) {
            int fc = gp.getFrameCounter();
            boolean visible = (fc / 6) % 2 == 0;
            if (!visible) return;
        }

        BufferedImage image = null;
        int tempScreenX = gp.getCamera().anchorX();
        int tempScreenY = gp.getCamera().anchorY();

        boolean attacking = isAttacking();
        if (attacking) {
            switch (getDirection()) {
                case UP:
                    image = getAttackSprite(Direction.UP);
                    tempScreenY -= gp.tileSize;
                    break;
                case DOWN:
                    image = getAttackSprite(Direction.DOWN);
                    break;
                case LEFT:
                    image = getAttackSprite(Direction.LEFT);
                    tempScreenX -= gp.tileSize;
                    break;
                case RIGHT:
                    image = getAttackSprite(Direction.RIGHT);
                    break;
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
        if (attackBtnLock > 0) attackBtnLock--;

        if (isAttackPressed() && attackBtnLock == 0) {
            attackBtnLock = 6; // ~0.1s @60fps
            if (canStartAttack()) {
                startAttack();
                DebugLog.info("Combat started");
            }
        }
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

        psm.loadAttackSprites(this, weapon.spriteKey());
    }

    private void handleNPCInteraction() {
        // Tìm NPC đang chạm player
        int npcIndex = gp.getCollisionChecker().checkEntity(this, gp.getEntityManager().getNPCs(gp.getCurrentMap()), getWorldX(), getWorldY());

        // Không chạm NPC nào -> reset interacting để lần sau lại gần vẫn hiện hint
        if (npcIndex == 999) {
            setInteracting(false);
            return;
        }

        // Lấy NPC
        Entity npc = gp.getEntityManager().getNPCs(gp.getCurrentMap()).get(npcIndex);
        if (npc == null) return;

        if (gp.getGameState() != GameState.PLAY) return;

        // LẠI GẦN NHƯNG CHƯA BẤM E -> HIỆN HINT
        if (!isTalkPressed()) {
            if (msgUI == null && gp.getUiManager() != null) {
                msgUI = gp.getUiManager().get(MessageUI.class);
            }
            // Chỉ hint cho ông nội + tránh spam bằng isInteracting()
            if (msgUI != null && "oldman".equalsIgnoreCase(npc.getName()) && !isInteracting()) {
                msgUI.showTouchMessage(
                        "Press 'E' to talk to your grandpa.",
                        npc,   // MessageUI bám theo vị trí ông nội
                        gp
                );
                setInteracting(true);
            }
            // chưa bấm E thì chỉ hint, không mở thoại
            return;
        }

        // BẤM E -> MỞ ĐỐI THOẠI

        // Nếu DialogueUI đang mở sẵn thì bỏ qua
        ui.effects.DialogueUI dialogue = gp.getUiManager().get(ui.effects.DialogueUI.class);
        if (dialogue != null && dialogue.isActive()) return;

        npc.facePlayer();
        npc.speak(gp);
        gp.setGameState(GameState.DIALOGUE);
    }

    // count stats with level (không còn rank)
    private void recalcStatsFromLevel() {
        int hp = baseHp + (level - 1) * hpPerLevel;
        int atk = baseAtk + (level - 1) * atkPerLevel;
        int def = baseDef + (level - 1) * defPerLevel;

        // dùng setStats của Entity
        setStats(hp, atk, def);
    }

    // exp to next level
    private int calcExpToNext(int lv) {
        // ví dụ: 10 * 1.2^(lv-1)
        double base = 10.0;
        return (int) Math.round(base * Math.pow(1.2, lv - 1));
    }

    /**
     * Player nhận thêm EXP khi giết quái
     */
    public void gainExp(int amount) {
        if (amount <= 0) return;

        int beforeExp = exp;
        int beforeLevel = level;

        DebugLog.info(
                "[EXP] gainExp +" + amount +
                        " | trước: exp=" + beforeExp + "/" + expToNext +
                        " lv=" + beforeLevel
        );

        this.exp += amount;

        // Lên nhiều level nếu exp dư
        while (exp >= expToNext) {
            exp -= expToNext;
            levelUp();  // trong levelUp cũng sẽ in debug + hiện thông báo
        }

        DebugLog.info(
                "[EXP] sau gainExp: exp=" + exp + "/" + expToNext +
                        " lv=" + level
        );
    }

    private void levelUp() {
        level++;
        expToNext = calcExpToNext(level);

        recalcStatsFromLevel();   // cập nhật HP/ATK/DEF

        // Hồi full máu khi lên level
        refillHP();

        // --- Thông báo LEVEL UP bằng tiếng Anh ---
        if (msgUI == null && gp != null && gp.getUiManager() != null) {
            msgUI = gp.getUiManager().get(MessageUI.class);
        }
        if (msgUI != null) {
            msgUI.showTouchMessage(
                    "LEVEL UP!  You reached level " + level + "! Get stronger and stronger",
                    null, // không gắn với object nào cụ thể
                    gp
            );
        }
    }

    // Hồi 1 lượng máu cố định
    public void heal(int amount) {
        if (amount <= 0) return;
        int max = getMaxHP();
        int cur = getHP();
        int newHp = Math.min(max, cur + amount);
        restoreHP(newHp);
    }

    // Hồi theo % máu tối đa (vd 0.1 = 10%)
    public void healPercent(double percent) {
        if (percent <= 0) return;
        int max = getMaxHP();
        int healAmount = (int) Math.round(max * percent);
        if (healAmount <= 0) healAmount = 1; // luôn hồi ít nhất 1 máu
        heal(healAmount);
    }
}
