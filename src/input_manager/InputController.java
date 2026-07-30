package input_manager;


public interface InputController {
    boolean isUpPressed();
    boolean isDownPressed();
    boolean isLeftPressed();   
    boolean isRightPressed();
    boolean isPicked();
    boolean isAttackPressed();
    boolean isTalkPressed();
    void resetTalkKey();
}
