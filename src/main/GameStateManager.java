package main;

public class GameStateManager {

    private GameState currentState = GameState.START;
    
    public GameState getState() {
        return currentState;
    }

    public void setState(GameState state) {
        if (state != null) {
            currentState = state;
        }
    }
}
