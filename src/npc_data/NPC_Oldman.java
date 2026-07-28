package npc_data;

import ai.movement.WanderMovement;
import entity.Direction;
import entity.Entity;
import main.GamePanel;
import ui.effects.DialogueUI;

import java.awt.Rectangle;

public class NPC_Oldman extends Entity {

    public NPC_Oldman(GamePanel gp, int mapIndex){
        super(gp);
        setMapIndex(mapIndex);
        setName("oldman");

        setSize(gp.tileSize, gp.tileSize);
        getImage();

        setCollidable(true);
        setAnimationOn(true);
        useMovementSpeed(1);

        setSolidArea(new Rectangle(3, 18, 42, 30));

        // đi lang thang, đổi hướng mỗi ~2s
        setController(new WanderMovement(1, 120));

        // ==== hội thoại riêng cho NPC này ====
        setDialogue();
    }

    private void getImage(){
        setMoveSprites(
                Direction.UP,
                setup("/npc/oldman_up_1" , getWidth() , getHeight()),
                setup("/npc/oldman_up_2" , getWidth() , getHeight())
        );
        setMoveSprites(
                Direction.DOWN,
                setup("/npc/oldman_down_1" , getWidth() , getHeight()),
                setup("/npc/oldman_down_2" , getWidth() , getHeight())
        );
        setMoveSprites(
                Direction.RIGHT,
                setup("/npc/oldman_right_1", getWidth() , getHeight()),
                setup("/npc/oldman_right_2" , getWidth() , getHeight())
        );
        setMoveSprites(
                Direction.LEFT,
                setup("/npc/oldman_left_1" , getWidth() , getHeight()),
                setup("/npc/oldman_left_2" , getWidth() , getHeight())
        );
    }

    // ==== THÊM HỘI THOẠI ====
    public void setDialogue() {
        defineDialogueLine(0, 0, "Today, you finally turn 15… which means you're allowed to enter the Dungeon for the first time.\n");
        defineDialogueLine(0, 1, "I know you're excited — eager to earn your own money and explore the world out there.");
        defineDialogueLine(0, 2, "But remember, the Dungeon isn't just treasures… it's filled with monsters and danger.");
        defineDialogueLine(0, 3, "For your birthday, I have a gift for you.\n"
                + "This is the Leviathan Axe, the weapon that accompanied me through my youth.\n");
        defineDialogueLine(0, 4, "Take it with you, protect yourself… and come back to tell me all about your very first adventure.");

        defineDialogueLine(0, 5, "Now, head outside the house. Your first combat awaits near the entrance.");
        defineDialogueLine(0, 6, "And remember this — no matter how tough the journey is, you can always return home to rest and recover your health.");
        defineDialogueLine(0, 7, "Go on, my child. Your adventure begins now.");

        defineDialogueLine(1, 0, "If you become tired, rest at the water.");
        defineDialogueLine(1, 1, "However, the monsters reappear if you rest.\nI don't know why but that's how it works.");
        defineDialogueLine(1, 2, "In any case, don't push yourself too hard.");

        defineDialogueLine(2, 0, "I wonder how to open that door...");
    }

    // ==== HÀNH ĐỘNG NÓI CHUYỆN ====
    @Override
    public void speak(GamePanel gp) {
        // NPC quay mặt về phía người chơi
        facePlayer();

        // Lấy đúng set hội thoại hiện tại
        String[] currentSet = getCurrentDialogueSet();
        if (currentSet != null) {
            gp.getUiManager().get(DialogueUI.class).startDialogue(currentSet);
        }
    }
}
