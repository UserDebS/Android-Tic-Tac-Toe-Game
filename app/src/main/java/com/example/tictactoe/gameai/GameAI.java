package com.example.tictactoe.gameai;

import android.widget.ImageButton;

import com.example.tictactoe.gameai.algorithm.MiniMax;

public class GameAI {
    private final ImageButton[][] imageButtons;
    private final MiniMax computerAI;

    public GameAI(ImageButton[][] imageButtons) {
        this.imageButtons = imageButtons;
        this.computerAI = new MiniMax();
    }

    public void nextStep(byte[][] currentState) {
        int[] coordinates = computerAI.nextStep(currentState);
        if(coordinates != null) imageButtons[coordinates[0]][coordinates[1]].performClick();
    }
}
