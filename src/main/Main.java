package main;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        JFrame window = new JFrame(); //  make new window 

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// click "X" then program end
        window.setResizable(true);// window size is constant
        window.setTitle("2D indie");// name for the window tab

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();

        window.setLocationRelativeTo(null);// place window in the middle of the screen
        window.setVisible(true);// view the window on screen
        
        gamePanel.setupGame();
        gamePanel.startGameThread();
    } 
}
